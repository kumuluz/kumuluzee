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
package com.kumuluz.ee.configuration.cdi.interceptors;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Interceptor class for ConfigBundle annotation.
 *
 * @author Tilen Faganel
 * @since 2.1.0
 */
@Interceptor
@ConfigBundle
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class ConfigBundleInterceptor {

    private static final Logger log = Logger.getLogger(ConfigBundleInterceptor.class.getName());

    /**
     * Method initialises class fields from configuration.
     */
    @PostConstruct
    public Object loadConfiguration(InvocationContext ic) throws Exception {

        Object target = ic.getTarget();
        Class targetClass = target.getClass();

        if (targetClassIsProxied(targetClass)) {
            targetClass = targetClass.getSuperclass();
        }

        ConfigurationUtil configurationUtil = ConfigurationUtil.getInstance();

        // invoke setters for fields which are defined in configuration
        for (Method m : targetClass.getMethods()) {

            if (m.getName().substring(0, 3).equals("set") && m.getParameters().length == 1) {

                if (m.getParameters()[0].getType().equals(String.class)) {

                    Optional<String> value = configurationUtil.get(getKeyName(targetClass, m.getName()));

                    if (value.isPresent()) {
                        m.invoke(target, value.get());
                    }

                    deployWatcher(target, targetClass, m, getKeyName(targetClass, m.getName()));

                } else if (m.getParameters()[0].getType().equals(Boolean.class)) {

                    Optional<Boolean> value = configurationUtil.getBoolean(getKeyName(targetClass, m.getName()));

                    if (value.isPresent()) {
                        m.invoke(target, value.get());
                    }

                    deployWatcher(target, targetClass, m, getKeyName(targetClass, m.getName()));

                } else if (m.getParameters()[0].getType().equals(Float.class)) {

                    Optional<Float> value = configurationUtil.getFloat(getKeyName(targetClass, m.getName()));

                    if (value.isPresent()) {
                        m.invoke(target, value.get());
                    }

                    deployWatcher(target, targetClass, m, getKeyName(targetClass, m.getName()));

                } else if (m.getParameters()[0].getType().equals(Double.class)) {

                    Optional<Double> value = configurationUtil.getDouble(getKeyName(targetClass, m.getName()));

                    if (value.isPresent()) {
                        m.invoke(target, value.get());
                    }

                    deployWatcher(target, targetClass, m, getKeyName(targetClass, m.getName()));

                } else if (m.getParameters()[0].getType().equals(Integer.class)) {

                    Optional<Integer> value = configurationUtil.getInteger(getKeyName(targetClass, m.getName()));

                    if (value.isPresent()) {
                        m.invoke(target, value.get());
                    }

                    deployWatcher(target, targetClass, m, getKeyName(targetClass, m.getName()));

                }
            }
        }

        return ic.proceed();
    }

    /**
     * Construct key name from prefix and field name or ConfigValue value (if present)
     *
     * @param targetClass target class
     * @param setter      name of the setter method
     * @return key in format prefix.key-name
     */
    private String getKeyName(Class targetClass, String setter) throws Exception {

        String key;

        // get prefix
        String prefix = ((ConfigBundle) targetClass.getAnnotation(ConfigBundle.class)).value();
        if (prefix.isEmpty()) {
            prefix = camelCaseToHyphenCase(targetClass.getSimpleName());
        }

        // get ConfigValue
        Field field = targetClass.getDeclaredField(setterToField(setter));
        ConfigValue fieldAnnotation = null;
        if (field != null) {
            fieldAnnotation = field.getAnnotation(ConfigValue.class);
        }

        if (fieldAnnotation != null && !fieldAnnotation.value().isEmpty()) {
            key = prefix + "." + camelCaseToHyphenCase(fieldAnnotation.value());

        } else {
            key = prefix + "." + camelCaseToHyphenCase(setter.substring(3));
        }

        return key;

    }

    /**
     * Parse setter name to field name.
     *
     * @param setter name of the setter method
     * @return field name
     */
    private String setterToField(String setter) {
        return Character.toLowerCase(setter.charAt(3)) + setter.substring(4);
    }

    /**
     * Parse upper camel case to lower hyphen case.
     *
     * @param s string in upper camel case format
     * @return string in lower hyphen case format
     */
    private String camelCaseToHyphenCase(String s) {

        String parsedString = s.substring(0, 1).toLowerCase();

        for (char c : s.substring(1).toCharArray()) {

            if (Character.isUpperCase(c)) {
                parsedString += "-" + Character.toLowerCase(c);
            } else {
                parsedString += c;
            }

        }

        return parsedString;

    }

    /**
     * Check if target class is proxied.
     *
     * @param targetClass target class
     * @return true if target class is proxied
     */
    private boolean targetClassIsProxied(Class targetClass) {
        return targetClass.getCanonicalName().contains("$Proxy");
    }

    /**
     * Subscribes to an event dispatcher and starts a watch for a given key.
     *
     * @param target      target instance
     * @param targetClass target class
     * @param method      method to invoke
     * @param watchedKey  watched key
     * @throws Exception
     */
    private void deployWatcher(Object target, Class targetClass, Method method, String watchedKey) throws Exception {

        String setter = method.getName();

        Field field = targetClass.getDeclaredField(setterToField(setter));
        ConfigValue fieldAnnotation = null;
        if (field != null) {
            fieldAnnotation = field.getAnnotation(ConfigValue.class);
        }

        if (fieldAnnotation != null && fieldAnnotation.watch() == true) {

            ConfigurationUtil.getInstance().subscribe((key, value) -> {

                if (watchedKey == key) {
                    try {
                        if (String.class.equals(method.getParameters()[0].getType())) {
                            method.invoke(target, value);
                        } else if (Boolean.class.equals(method.getParameters()[0].getType())) {
                            method.invoke(target, Boolean.parseBoolean(value));
                        } else if (Float.class.equals(method.getParameters()[0].getType())) {
                            try {
                                method.invoke(target, Float.parseFloat(value));
                            } catch (NumberFormatException e) {
                                log.severe("Exception while storing new value: Number format exception. " +
                                        "Expected: Float. Value: " + value);
                            }
                        } else if (Double.class.equals(method.getParameters()[0].getType())) {
                            try {
                                method.invoke(target, Double.parseDouble(value));
                            } catch (NumberFormatException e) {
                                log.severe("Exception while storing new value: Number format exception. Expected:" +
                                        " Double. Value: " + value);
                            }
                        } else if (Integer.class.equals(method.getParameters()[0].getType())) {
                            try {
                                method.invoke(target, Integer.parseInt(value));
                            } catch (NumberFormatException e) {
                                log.severe("Exception while storing new value: Number format exception. Expected:" +
                                        " Integer. Value: " + value);
                            }
                        }
                    } catch (IllegalAccessException e) {
                        log.severe("Illegal access exception: " + e.toString());
                    } catch (InvocationTargetException e) {
                        log.severe("Invocation target exception: " + e.toString());
                    }

                }
            }, watchedKey);

        }

    }
}


