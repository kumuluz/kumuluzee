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
package com.kumuluz.ee.configuration;

import java.util.Optional;

/**
 * @author Tilen Faganel
 * @since 2.1.0
 */
public interface ConfigurationSource {

    void init();

    Optional<String> get(String key);

    Optional<Boolean> getBoolean(String key);

    Optional<Integer> getInteger(String key);

    Optional<Double> getDouble(String key);

    Optional<Float> getFloat(String key);

    Optional<Integer> getListSize(String key);

    void watch(String key);

    void set(String key, String value);

    void set(String key, Boolean value);

    void set(String key, Integer value);

    void set(String key, Double value);

    void set(String key, Float value);
}
