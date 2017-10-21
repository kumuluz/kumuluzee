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
package com.kumuluz.ee.configuration.utils;

import com.kumuluz.ee.configuration.ConfigurationListener;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Tilen Faganel
 * @since 2.3.0
 */
// Fire configuration events and notify subscription
public class ConfigurationDispatcher {

    private List<ConfigurationListener> subscriptions = new ArrayList<>();

    public void notifyChange(String key, String value) {

        for (ConfigurationListener subscription : subscriptions) {
            subscription.onChange(key, value);
        }
    }

    public void subscribe(ConfigurationListener listener) {
        subscriptions.add(listener);
    }

    public void unsubscribe(ConfigurationListener listener) {
        subscriptions.remove(listener);
    }
}
