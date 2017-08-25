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
package com.kumuluz.ee.common.runtime;

import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * @author Tilen Faganel
 * @since 2.4.0
 */
public class EeRuntimeInternal {

    private String instanceId = UUID.randomUUID().toString();
    private String version = ResourceBundle.getBundle("META-INF/kumuluzee/versions").getString("version");
    private List<EeRuntimeComponent> eeComponents = Collections.emptyList();

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<EeRuntimeComponent> getEeComponents() {
        return eeComponents;
    }

    public void setEeComponents(List<EeRuntimeComponent> eeComponents) {
        this.eeComponents = Collections.unmodifiableList(eeComponents);
    }
}
