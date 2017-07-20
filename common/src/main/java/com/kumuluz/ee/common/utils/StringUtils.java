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
package com.kumuluz.ee.common.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Tilen Faganel
 * @since 2.3.0
 */
public class StringUtils {

    /**
     * Parse upper camel case to lower hyphen case.
     *
     * @param s string in upper camel case format
     * @return string in lower hyphen case format
     */
    public static String camelCaseToHyphenCase(String s) {

        StringBuilder parsedString = new StringBuilder(s.substring(0, 1).toLowerCase());

        for (char c : s.substring(1).toCharArray()) {

            if (Character.isUpperCase(c)) {
                parsedString.append("-").append(Character.toLowerCase(c));
            } else {
                parsedString.append(c);
            }
        }

        return parsedString.toString();
    }

    public static String hyphenCaseToCamelCase(String s) {

        List<String> words = Stream.of(s.split("-")).filter(w -> !"".equals(w)).collect(Collectors.toList());

        if (words.size() < 2) {
            return s;
        }

        StringBuilder parsedString = new StringBuilder(words.get(0));

        for (int i = 1; i < words.size(); i++) {

            parsedString.append(Character.toUpperCase(words.get(i).charAt(0))).append(words.get(i).substring(1));
        }

        return parsedString.toString();
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
