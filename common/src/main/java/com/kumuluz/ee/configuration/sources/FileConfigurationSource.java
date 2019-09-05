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
package com.kumuluz.ee.configuration.sources;

import com.kumuluz.ee.configuration.ConfigurationSource;
import com.kumuluz.ee.logs.LogDeferrer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * Base class for file configuration sources.
 *
 * @author Urban Malc
 * @since 3.6.0
 */
public abstract class FileConfigurationSource implements ConfigurationSource {

    protected Logger log;
    protected LogDeferrer<Logger> logDeferrer;

    protected String filename;
    protected int defaultOrdinal;

    public FileConfigurationSource(String filename, int defaultOrdinal) {

        this.filename = filename;
        this.defaultOrdinal = defaultOrdinal;

        this.logDeferrer = new LogDeferrer<>();
        this.logDeferrer.init(() -> Logger.getLogger(PropertiesConfigurationSource.class.getName()));
    }

    public void postInit() {

        logDeferrer.execute();
        logDeferrer = null;

        log = Logger.getLogger(FileConfigurationSource.class.getName());
    }

    protected InputStream getInputStream() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);

        if (inputStream == null) {
            inputStream = Files.newInputStream(Paths.get(filename));
        }

        return inputStream;
    }
}
