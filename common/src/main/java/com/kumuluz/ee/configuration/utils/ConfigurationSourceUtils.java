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

import java.util.*;

/**
 * @author Urban Malc
 * @since 2.5.0
 */
public class ConfigurationSourceUtils {


    public static Optional<Integer> getListSize(String key, Collection<String> allKeys) {

        Integer maxIndex = -1;

        for (String propertyKey : allKeys) {

            if (propertyKey.startsWith(key + "[")) {

                int openingIndex = key.length() + 1;
                int closingIndex = propertyKey.indexOf("]", openingIndex + 1);

                try {
                    Integer idx = Integer.parseInt(propertyKey.substring(openingIndex, closingIndex));
                    maxIndex = Math.max(maxIndex, idx);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        if (maxIndex != -1) {
            return Optional.of(maxIndex + 1);
        }

        return Optional.empty();
    }

    public static Optional<List<String>> getMapKeys(String key, Collection<String> allKeys) {

        Set<String> mapKeys = new HashSet<>();

        for (String propertyKey : allKeys) {

            String mapKey = "";

            if (key.isEmpty()) {
                mapKey = propertyKey;
            } else if (propertyKey.startsWith(key)) {

                int index = key.length() + 1;

                if (index < propertyKey.length() && propertyKey.charAt(index - 1) == '.') {
                    mapKey = propertyKey.substring(index);
                }
            }

            if (!mapKey.isEmpty()) {

                int endIndex = mapKey.indexOf(".");

                if (endIndex > 0) {
                    mapKey = mapKey.substring(0, endIndex);
                }

                int bracketIndex = mapKey.indexOf("[");
                if (bracketIndex > 0) {
                    mapKey = mapKey.substring(0, bracketIndex);
                }

                mapKeys.add(mapKey);
            }
        }

        if (mapKeys.isEmpty()) {

            return Optional.empty();
        }

        return Optional.of(new ArrayList<>(mapKeys));
    }
}
