/*
 *  Copyright (c) 2014-2017 Kumuluz and/or its affiliates
 *  and other contributors as indicated by the @author tags and
 *  the contributor list.
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://opensource.org/licenses/MIT
 *
 *  The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or
 *  implied, including but not limited to the warranties of merchantability,
 *  fitness for a particular purpose and noninfringement. in no event shall the
 *  authors or copyright holders be liable for any claim, damages or other
 *  liability, whether in an action of contract, tort or otherwise, arising from,
 *  out of or in connection with the software or the use or other dealings in the
 *  software. See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.kumuluz.ee.loader;

import com.kumuluz.ee.loader.exception.EeClassLoaderException;
import com.kumuluz.ee.loader.jar.FileInfo;
import com.kumuluz.ee.loader.jar.JarEntryInfo;
import com.kumuluz.ee.loader.jar.JarFileInfo;
import org.zeroturnaround.zip.ZipUtil;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * @author Benjamin Kastelic
 * @since 2.4.0
 */
public class EeClassLoader extends ClassLoader {

    /**
     * Directory name for temporary files.
     */
    private static final String TMP_DIRECTORY = "tmp/EeClassLoader";
    private final Thread mainThread = Thread.currentThread();
    private Boolean DEBUG = false;
    private File tempDir;
    private List<JarFileInfo> jarFiles;
    private List<FileInfo> files;
    private Set<File> deleteOnExit;
    private Map<String, Class<?>> classes;

    private JarFileInfo jarFileInfo;

    /**
     * Default constructor.
     * Defines system class loader as a parent class loader.
     */
    public EeClassLoader() {
        this(ClassLoader.getSystemClassLoader());
    }

    /**
     * Constructor.
     */
    public EeClassLoader(ClassLoader parent) {
        super(parent);

        String debugString = System.getProperty("com.kumuluz.ee.loader.debug");

        if (debugString != null) {

            DEBUG = Boolean.valueOf(debugString);
        }

        long startTime = System.currentTimeMillis();

        debug("Initialising KumuluzEE classloader");

        classes = new HashMap<>();
        jarFiles = new ArrayList<>();
        files = new ArrayList<>();
        deleteOnExit = new HashSet<>();

        String mainJarURLString;
        ProtectionDomain protectionDomain = getClass().getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URL mainJarURL = codeSource.getLocation();
        String protocol = mainJarURL.getProtocol();

        // Decoding required for 'space char' in URL:
        //    URL.getFile() returns "/C:/my%20dir/MyApp.jar" for "/C:/my dir/MyApp.jar"
        try {
            mainJarURLString = URLDecoder.decode(mainJarURL.getFile(), "UTF-8");
        } catch (UnsupportedEncodingException e) {

            String msg = String.format("Failed to decode URL: %s %s", mainJarURL, e.toString());

            throw new EeClassLoaderException(msg, e);
        }

        File mainJarFile = new File(mainJarURLString);

        try {
            jarFileInfo = new JarFileInfo(new JarFile(mainJarFile), mainJarFile.getName(), null, protectionDomain, null);

            debug(String.format("Loading from main JAR: '%s' PROTOCOL: '%s'", mainJarURLString, protocol));
        } catch (IOException e) {

            String msg = String.format("Not a JAR: %s %s", mainJarURLString, e.toString());

            throw new EeClassLoaderException(msg, e);
        }

        // load main JAR:
        try {
            // start recursive JAR loading
            extractMainJar(jarFileInfo);
            loadJar(jarFileInfo);
        } catch (Exception e) {

            String msg = String.format("Not a valid URL: %s %s", mainJarURL, e.toString());

            throw new EeClassLoaderException(msg, e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                debug("Shutting down and cleaning up ...");
                mainThread.join();
                shutdown();
            } catch (InterruptedException e) {
                debug("Failed to shutdown and clean up gracefully.");
            }
        }));

        debug(String.format("Initialised KumuluzEE classloader @%dms", System.currentTimeMillis() - startTime));
    }

    private void createTempDirectory() throws URISyntaxException {
        // Create temp directory for classpath initialization
        if (tempDir == null) {

            ProtectionDomain protectionDomain = getClass().getProtectionDomain();
            CodeSource codeSource = protectionDomain.getCodeSource();
            URI location = (codeSource == null ? null : codeSource.getLocation().toURI());
            String path = (location == null ? null : location.getSchemeSpecificPart());
            if (path == null) {
                throw new IllegalStateException("Unable to determine code source archive");
            }

            File jarDir = new File(path);
            String jarFolder = jarDir.getParentFile().getPath();

            File dir = new File(jarFolder, TMP_DIRECTORY);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            dir.deleteOnExit();

            chmod777(dir); // Unix - allow temp directory RW access to all users.
            if (!dir.exists() || !dir.isDirectory()) {
                throw new EeClassLoaderException("Cannot create temp directory " + dir.getAbsolutePath());
            }
            tempDir = dir;
        }
    }

    private File getFileReference(JarEntry jarEntry) throws EeClassLoaderException {
        File file = new File(tempDir + File.separator + jarEntry.getName());
        file.deleteOnExit();
        chmod777(file); // Unix - allow temp file deletion by any user
        return file;
    }

    private File getJarFileReference(JarEntryInfo jarEntryInfo) throws EeClassLoaderException {
        File file = new File(tempDir + File.separator + jarEntryInfo.getName());
        file.deleteOnExit();
        chmod777(file); // Unix - allow temp file deletion by any user
        return file;
    }

    private void extractMainJar(JarFileInfo jarFileInfo) throws URISyntaxException {
        final String LIB_DIRECTORY = "lib/";

        createTempDirectory();

        ZipUtil.unpack(new File(jarFileInfo.getJarFile().getName()), tempDir);

        files = jarFileInfo.getJarFile()
                .stream()
                .parallel()
                .filter(je -> !je.getName().toLowerCase().startsWith(LIB_DIRECTORY))
                .map(je -> {
                    try {
                        JarEntryInfo jarEntryInfo = new JarEntryInfo(jarFileInfo, je);
                        File tempFile = getFileReference(je);

                        debug(String.format("Loading file %s from temp file %s", jarEntryInfo.getJarEntry(), getFilenameForLog(tempFile)));

                        return new FileInfo(tempFile, je.getName());
                    } catch (EeClassLoaderException e) {
                        throw new RuntimeException("ERROR on loading inner file: " + e.getMessageAll());
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Loads specified JAR.
     */
    private void loadJar(final JarFileInfo jarFileInfo) {
        final String JAR_SUFFIX = ".jar";

        jarFiles.add(jarFileInfo);
        jarFileInfo.getJarFile()
                .stream()
                .filter(je -> !je.isDirectory() && je.getName().toLowerCase().endsWith(JAR_SUFFIX))
                .forEach(je -> {
                    try {
                        JarEntryInfo jarEntryInfo = new JarEntryInfo(jarFileInfo, je);
                        File tempFile = getJarFileReference(jarEntryInfo);

                        debug(String.format("Loading inner JAR %s from temp file %s", jarEntryInfo.getJarEntry(), getFilenameForLog(tempFile)));

                        // Construct ProtectionDomain for this inner JAR:
                        URL url = tempFile.toURI().toURL();
                        ProtectionDomain pdParent = jarFileInfo.getProtectionDomain();
                        // 'csParent' is never null: top JAR has it, classloader creates it for child JAR:
                        CodeSource csParent = pdParent.getCodeSource();
                        Certificate[] certParent = csParent.getCertificates();
                        CodeSource csChild = certParent == null
                                ? new CodeSource(url, csParent.getCodeSigners())
                                : new CodeSource(url, certParent);
                        ProtectionDomain pdChild = new ProtectionDomain(csChild, pdParent.getPermissions(), pdParent.getClassLoader(), pdParent.getPrincipals());
                        loadJar(new JarFileInfo(new JarFile(tempFile), jarEntryInfo.getName(), jarFileInfo, pdChild, tempFile));
                    } catch (IOException e) {
                        throw new RuntimeException(String.format("Cannot load jar entries from jar %s", je.getName().toLowerCase()), e);
                    } catch (EeClassLoaderException e) {
                        throw new RuntimeException("ERROR on loading inner JAR: " + e.getMessageAll());
                    }
                });
    }

    private JarEntryInfo findJarEntry(String name) {
        for (JarFileInfo jarFileInfo : jarFiles) {
            JarFile jarFile = jarFileInfo.getJarFile();
            JarEntry jarEntry = jarFile.getJarEntry(name);
            if (jarEntry != null) {
                return new JarEntryInfo(jarFileInfo, jarEntry);
            }
        }
        return null;
    }

    private URL findFile(String name) {
        for (FileInfo fileInfo : files) {
            if (fileInfo.getSimpleName().equals(name)) {
                try {
                    return fileInfo.getFile().toURI().toURL();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        return null;
    }

    private List<JarEntryInfo> findJarEntries(String name) {

        List<JarEntryInfo> jarEntryInfoList = new ArrayList<>();

        for (JarFileInfo jarFileInfo : jarFiles) {

            JarFile jarFile = jarFileInfo.getJarFile();
            JarEntry jarEntry = jarFile.getJarEntry(name);
            if (jarEntry != null) {
                jarEntryInfoList.add(new JarEntryInfo(jarFileInfo, jarEntry));
            }
        }

        return jarEntryInfoList;
    }

    private List<URL> findFiles(String name) {

        List<URL> urlList = new ArrayList<>();

        for (FileInfo fileInfo : files) {

            if (fileInfo.getSimpleName().equals(name)) {
                try {
                    URL fileUrl = fileInfo.getFile().toURI().toURL();
                    urlList.add(fileUrl);
                } catch (Exception e) {
                    // ignore
                }
            }
        }

        return urlList;
    }

    /**
     * Finds native library entry.
     *
     * @param libraryName Library name. For example for the library name "Native"
     *  - Windows returns entry "Native.dll"
     *  - Linux returns entry "libNative.so"
     *  - Mac returns entry "libNative.jnilib" or "libNative.dylib"
     *    (depending on Apple or Oracle JDK and/or JDK version)
     * @return Native library entry.
     */
    private JarEntryInfo findJarNativeEntry(String libraryName) {
        String name = System.mapLibraryName(libraryName);
        for (JarFileInfo jarFileInfo : jarFiles) {
            JarFile jarFile = jarFileInfo.getJarFile();
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (jarEntry.isDirectory()) {
                    continue;
                }

                // Example: name is "Native.dll"
                String jarEntryName = jarEntry.getName(); // "Native.dll" or "abc/xyz/Native.dll"
                // name "Native.dll" could be found, for example
                //   - in the path: abc/Native.dll/xyz/my.dll <-- do not load this one!
                //   - in the partial name: abc/aNative.dll   <-- do not load this one!
                String[] token = jarEntryName.split("/"); // the last token is library name
                if (token.length > 0 && token[token.length - 1].equals(name)) {

                    debug(String.format("Loading native library '%s' found as '%s' in JAR %s", libraryName, jarEntryName, jarFileInfo.getSimpleName()));

                    return new JarEntryInfo(jarFileInfo, jarEntry);
                }
            }
        }
        return null;
    }

    /**
     * Loads class from a JAR and searches for all jar-in-jar.
     */
    private Class<?> findJarClass(String className) throws EeClassLoaderException {
        Class<?> clazz = classes.get(className);
        if (clazz != null) {
            return clazz;
        }

        // Char '/' works for Win32 and Unix.
        String fullClassName = className.replace('.', '/') + ".class";
        JarEntryInfo jarEntryInfo = findJarEntry(fullClassName);
        String jarSimpleName = null;
        if (jarEntryInfo != null) {
            jarSimpleName = jarEntryInfo.getJarFileInfo().getSimpleName();
            definePackage(className, jarEntryInfo);
            byte[] bytes = jarEntryInfo.getJarBytes();
            try {
                clazz = defineClass(className, bytes, 0, bytes.length, jarEntryInfo.getJarFileInfo().getProtectionDomain());
            } catch (ClassFormatError e) {
                throw new EeClassLoaderException(null, e);
            }
        }
        if (clazz == null) {
            throw new EeClassLoaderException(className);
        }
        classes.put(className, clazz);

        debug(String.format("Loaded %s by %s from JAR %s", className, getClass().getName(), jarSimpleName));

        return clazz;
    }

    /**
     * Called on shutdown to cleanup temporary files.
     */
    private void shutdown() {

        debug("Shutting down and cleaning up ...");

        for (JarFileInfo jarFileInfo : jarFiles) {
            try {
                jarFileInfo.getJarFile().close();
            } catch (IOException e) {
                // Ignore. In the worst case temp files will accumulate.
            }
            File file = jarFileInfo.getFileDeleteOnExit();
            if (file != null && !file.delete()) {
                deleteOnExit.add(file);
            }
        }
        // Private configuration file with failed to delete temporary files:
        //   WinXP: C:/Documents and Settings/username/.EeClassLoader
        //    Unix: /export/home/username/.EeClassLoader
        //           -or-  /home/username/.EeClassLoader
        File configFile = new File(System.getProperty("user.home") + File.separator + ".EeClassLoader");
        deleteOldTemp(configFile);
        persistNewTemp(configFile);
    }

    /**
     * Deletes temporary files listed in the file.
     * The method is called on shutdown().
     *
     * @param configFile file with temporary files list.
     */
    private void deleteOldTemp(File configFile) {
        BufferedReader reader = null;
        try {
            int count = 0;
            reader = new BufferedReader(new FileReader(configFile));
            String line;
            while ((line = reader.readLine()) != null) {
                File file = new File(line);
                if (!file.exists()) {
                    continue; // already deleted; from command line?
                }
                if (file.delete()) {
                    count++;
                } else {
                    // Cannot delete, will try next time.
                    deleteOnExit.add(file);
                }
            }

            debug(String.format("Deleted %d old temp files listed in %s", count, configFile.getAbsolutePath()));
        } catch (IOException e) {
            // Ignore. This file may not exist.
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    /**
     * Creates file with temporary files list. This list will be used to
     * delete temporary files on the next application launch.
     * The method is called from shutdown().
     *
     * @param configFile file with temporary files list.
     */
    private void persistNewTemp(File configFile) {
        if (deleteOnExit.size() == 0) {

            debug("No temp file names to persist on exit.");

            configFile.delete(); // do not pollute disk
            return;
        }

        debug(String.format("Persisting %d temp file names into %s", deleteOnExit.size(), configFile.getAbsolutePath()));

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(configFile));
            for (File file : deleteOnExit) {
                if (!file.delete()) {
                    String filePath = file.getCanonicalPath();
                    writer.write(filePath);
                    writer.newLine();

                    debug(String.format("JVM failed to release %s", filePath));
                }
            }
        } catch (IOException e) {
            // Ignore. In the worst case temp files will accumulate.
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    /**
     * Checks how the application was loaded: from JAR or file system.
     */
    private boolean isLaunchedFromJar() {
        return jarFiles != null && !jarFiles.isEmpty();
    }

    /**
     * Invokes main() method on class with provided parameters.
     */
    public void invokeMain(String className, String[] args) throws Throwable {

        Class<?> clazz = loadClass(className);

        debug(String.format("Launch: %s.main(); Loader: %s", className, clazz.getClassLoader()));

        Method method = clazz.getMethod("main", String[].class);

        if (method == null) {
            throw new NoSuchMethodException("The main() method in class \"" + className + "\" not found.");
        }

        try {
            method.invoke(null, (Object)args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    @Override
    protected synchronized Class<?> loadClass(String className, boolean bResolve) throws ClassNotFoundException {

        debug(String.format("LOADING %s (resolve=%b)", className, bResolve));

        Thread.currentThread().setContextClassLoader(this); // !!!

        Class<?> clazz = null;

        try {
            // Step 1. This class is already loaded by system classloader.
            if (getClass().getName().equals(className)) {
                return EeClassLoader.class;
            }
            // Step 2. Already loaded class.
            clazz = findLoadedClass(className);
            if (clazz != null) {

                debug(String.format("Class %s already loaded", className));

                return clazz;
            }
            // Step 3. Load from JAR.
            if (isLaunchedFromJar()) {
                try {
                    clazz = findJarClass(className); // Do not simplify! See "finally"!
                    return clazz;
                } catch (EeClassLoaderException e) {
                    if (e.getCause() == null) {
                        debug(String.format("Not found %s in JAR by %s: %s", className, getClass().getName(), e.getMessage()));
                    } else {
                        debug(String.format("Error loading %s in JAR by %s: %s", className, getClass().getName(), e.getCause()));
                    }
                    // keep looking...
                }
            }
            // Step 4. Load by parent (usually system) class loader.
            try {
                ClassLoader classLoader = getParent();
                clazz = classLoader.loadClass(className);

                debug(String.format("Loaded %s by %s", className, classLoader.getClass().getName()));

                return clazz;
            } catch (ClassNotFoundException e) {
                // Ignore
            }
            // Nothing else to try ...
            throw new ClassNotFoundException("Failure to load: " + className);
        } finally {
            if (clazz != null  &&  bResolve) {
                resolveClass(clazz);
            }
        }
    }

    @Override
    protected URL findResource(String name) {

        debug(String.format("findResource: %s", name));

        if (isLaunchedFromJar()) {
            URL file = findFile(normalizeResourceName(name));
            if (file != null) {
                debug(String.format("found resource: %s", file));

                return file;
            }

            JarEntryInfo inf = findJarEntry(normalizeResourceName(name));
            if (inf != null) {
                URL url = inf.getURL();

                debug(String.format("found resource: %s", url));

                return url;
            }

            debug(String.format("not found resource: %s", name));

            return null;
        }

        return super.findResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return findResources(name);
    }

    @Override
    public Enumeration<URL> findResources(String name) throws IOException {

        debug(String.format("getResources: %s", name));

        if (isLaunchedFromJar()) {
            List<URL> fileUrls = findFiles(normalizeResourceName(name));
            List<JarEntryInfo> jarEntries = findJarEntries(normalizeResourceName(name));

            List<URL> urls = new ArrayList<>(fileUrls);

            for (JarEntryInfo jarEntryInfo : jarEntries) {
                if (jarEntryInfo.getJarFileInfo().equals(this.jarFileInfo)) {
                    continue;
                }

                URL url = jarEntryInfo.getURL();
                if (url != null) {
                    urls.add(url);
                }
            }

            return Collections.enumeration(urls);
        }

        return super.findResources(name);
    }

    @Override
    protected String findLibrary(String name) {

        debug(String.format("findLibrary: %s", name));

        if (isLaunchedFromJar()) {
            JarEntryInfo jarEntryInfo = findJarNativeEntry(name);
            if (jarEntryInfo != null) {
                try {
                    File file = getJarFileReference(jarEntryInfo);

                    debug(String.format("Loading native library %s from temp file %s", jarEntryInfo.getJarEntry(), getFilenameForLog(file)));

                    deleteOnExit.add(file);
                    return file.getAbsolutePath();
                } catch (EeClassLoaderException e) {

                    debug(String.format("Failure to load native library %s: %s", name, e.toString()));
                }
            }
            return null;
        }
        return super.findLibrary(name);
    }

    /**
     * The default <code>ClassLoader.defineClass()</code> does not create package
     * for the loaded class and leaves it null. Each package referenced by this
     * class loader must be created only once before the
     * <code>ClassLoader.defineClass()</code> call.
     * The base class <code>ClassLoader</code> keeps cache with created packages
     * for reuse.
     *
     * @param className class to load.
     * @throws  IllegalArgumentException
     *          If package name duplicates an existing package either in this
     *          class loader or one of its ancestors.
     */
    private void definePackage(String className, JarEntryInfo jarEntryInfo) throws IllegalArgumentException {
        int index = className.lastIndexOf('.');
        String packageName = index > 0 ? className.substring(0, index) : "";
        if (getPackage(packageName) == null) {
            JarFileInfo jarFileInfo = jarEntryInfo.getJarFileInfo();
            definePackage(
                    packageName, jarFileInfo.getSpecificationTitle(), jarFileInfo.getSpecificationVersion(),
                    jarFileInfo.getSpecificationVendor(), jarFileInfo.getImplementationTitle(),
                    jarFileInfo.getImplementationVersion(), jarFileInfo.getImplementationVendor(),
                    jarFileInfo.getSealURL()
            );
        }
    }

    /**
     * The system class loader could load resources defined as
     * "com/abc/Foo.txt" or "com\abc\Foo.txt".
     * This method converts path with '\' to default '/' JAR delimiter.
     *
     * @param name resource name including path.
     * @return normalized resource name.
     */
    private String normalizeResourceName(String name) {
        return name.replace('\\', '/');
    }

    private void chmod777(File file) {
        file.setReadable(true, false);
        file.setWritable(true, false);
        file.setExecutable(true, false); // Unix: allow content for dir, redundant for file
    }

    private String getFilenameForLog(File file) {
        try {
            // In form "C:\Documents and Settings\..."
            return file.getCanonicalPath();
        } catch (IOException e) {
            // In form "C:\DOCUME~1\..."
            return file.getAbsolutePath();
        }
    }

    private void debug(String msg) {

        if (DEBUG) {
            System.out.println(msg);
        }
    }
}
