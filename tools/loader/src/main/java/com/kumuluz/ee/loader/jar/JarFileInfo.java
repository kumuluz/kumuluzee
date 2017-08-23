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
package com.kumuluz.ee.loader.jar;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Class that holds JAR file information.
 *
 * @author Benjamin Kastelic
 * @since 2.4.0
 */
public class JarFileInfo {

    private JarFile jarFile; // this is the essence of JarFileInfo wrapper
    private String simpleName; // accumulated for logging like: "topJar!childJar!kidJar"
    private File fileDeleteOnExit;
    private Manifest manifest; // required for package creation
    private ProtectionDomain protectionDomain;

    /**
     * @param jarFile
     *            Never null.
     * @param simpleName
     *            Used for logging. Never null.
     * @param jarFileParent
     *            Used to make simpleName for logging. Null for top level JAR.
     * @param fileDeleteOnExit
     *            Used only to delete temporary file on exit.
     *            Could be null if not required to delete on exit (top level JAR)
     */
    public JarFileInfo(JarFile jarFile, String simpleName, JarFileInfo jarFileParent, ProtectionDomain protectionDomain, File fileDeleteOnExit) {
        this.simpleName = (jarFileParent == null ? "" : jarFileParent.simpleName + "!") + simpleName;
        this.jarFile = jarFile;
        this.protectionDomain = protectionDomain;
        this.fileDeleteOnExit = fileDeleteOnExit;
        try {
            this.manifest = jarFile.getManifest(); // 'null' if META-INF directory is missing
        } catch (IOException e) {
            // Ignore and create blank manifest
        }
        if (this.manifest == null) {
            this.manifest = new Manifest();
        }
    }

    public String getSpecificationTitle() {
        return manifest.getMainAttributes().getValue(Attributes.Name.SPECIFICATION_TITLE);
    }

    public String getSpecificationVersion() {
        return manifest.getMainAttributes().getValue(Attributes.Name.SPECIFICATION_VERSION);
    }

    public String getSpecificationVendor() {
        return manifest.getMainAttributes().getValue(Attributes.Name.SPECIFICATION_VENDOR);
    }

    public String getImplementationTitle() {
        return manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_TITLE);
    }

    public String getImplementationVersion() {
        return manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
    }

    public String getImplementationVendor() {
        return manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
    }

    public URL getSealURL() {
        String seal = manifest.getMainAttributes().getValue(Attributes.Name.SEALED);
        if (seal != null) {
            try {
                return new URL(seal);
            } catch (MalformedURLException e) {
                // Ignore
            }
        }
        return null;
    }

    public JarFile getJarFile() {
        return jarFile;
    }

    public void setJarFile(JarFile jarFile) {
        this.jarFile = jarFile;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public File getFileDeleteOnExit() {
        return fileDeleteOnExit;
    }

    public void setFileDeleteOnExit(File fileDeleteOnExit) {
        this.fileDeleteOnExit = fileDeleteOnExit;
    }

    public Manifest getManifest() {
        return manifest;
    }

    public void setManifest(Manifest manifest) {
        this.manifest = manifest;
    }

    public ProtectionDomain getProtectionDomain() {
        return protectionDomain;
    }

    public void setProtectionDomain(ProtectionDomain protectionDomain) {
        this.protectionDomain = protectionDomain;
    }
}
