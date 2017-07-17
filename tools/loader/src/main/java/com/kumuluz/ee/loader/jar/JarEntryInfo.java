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

import com.kumuluz.ee.loader.exception.EeClassLoaderException;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarEntry;

/**
 * @author Benjamin Kastelic
 *
 * Class with JAR entry information. Keeps JAR file and entry object.
 */
public class JarEntryInfo {

    private JarFileInfo jarFileInfo;
    private JarEntry jarEntry;

    public JarEntryInfo(JarFileInfo jarFileInfo, JarEntry jarEntry) {
        this.jarFileInfo = jarFileInfo;
        this.jarEntry = jarEntry;
    }

    public JarFileInfo getJarFileInfo() {
        return jarFileInfo;
    }

    public void setJarFileInfo(JarFileInfo jarFileInfo) {
        this.jarFileInfo = jarFileInfo;
    }

    public JarEntry getJarEntry() {
        return jarEntry;
    }

    public void setJarEntry(JarEntry jarEntry) {
        this.jarEntry = jarEntry;
    }

    public URL getURL() { // used in findResource() and findResources()
        try {
            String jarFileName = jarFileInfo.getJarFile().getName().replace("\\", "/");
            return new URL("jar:file:" + jarFileName + "!/" + jarEntry);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public String getName() { // used in createTempFile() and loadJar()
        return jarEntry.getName().replace('/', '_');
    }

    /**
     * Read JAR entry and return byte array of this JAR entry.
     */
    public byte[] getJarBytes() throws EeClassLoaderException {
        DataInputStream dataInputStream = null;
        byte[] bytes;

        try {
            long jarEntrySize = jarEntry.getSize();
            if (jarEntrySize <= 0  ||  jarEntrySize >= Integer.MAX_VALUE) {
                throw new EeClassLoaderException("Invalid size " + jarEntrySize + " for entry " + jarEntry);
            }

            bytes = new byte[(int) jarEntrySize];

            InputStream inputStream = jarFileInfo.getJarFile().getInputStream(jarEntry);
            dataInputStream = new DataInputStream(inputStream);
            dataInputStream.readFully(bytes);
        } catch (IOException e) {
            throw new EeClassLoaderException(null, e);
        } finally {
            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }

        return bytes;
    }

    @Override
    public String toString() {
        return "JAR: " + jarFileInfo.getJarFile().getName() + " ENTRY: " + jarEntry;
    }
}
