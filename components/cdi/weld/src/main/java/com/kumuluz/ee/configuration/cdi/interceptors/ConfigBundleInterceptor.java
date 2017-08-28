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

import com.kumuluz.ee.common.utils.StringUtils;
import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;

/**
 * Interceptor class for ConfigBundle annotation.
 *
 * @author Tilen Faganel
 * @author Jan Meznaric
 * @since 2.1.0
 */
@Interceptor
@ConfigBundle
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class ConfigBundleInterceptor {

    private static final Logger log = Logger.getLogger(ConfigBundleInterceptor.class.getName());
    private static final ConfigurationUtil configurationUtil = ConfigurationUtil.getInstance();
    private final Class[] primitives = {String.class, Boolean.class, Float.class, Double.class, Integer.class, Long
            .class};

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

        ConfigBundle configBundleAnnotation = (ConfigBundle) targetClass.getDeclaredAnnotation(ConfigBundle.class);

        processConfigBundleBeanSetters(target, targetClass, getKeyPrefix(targetClass, null), new HashMap<>(),
                configBundleAnnotation.watch());

        return ic.proceed();
    }

    /**
     * Processes and invokes all setters in Bean annotated with @ConfigBundle
     *
     * @param target                  target object
     * @param targetClass             target class
     * @param keyPrefix               a prefix for generating key names
     * @param processedClassRelations class pairs that have already been processed (for cycle detection)
     * @param watchAllFields          if true, enable watch on all fields
     * @return returns true, if at least one field was successfully populated from configuration sources
     * @throws Exception
     */
    private boolean processConfigBundleBeanSetters(Object target, Class targetClass, String keyPrefix, Map<Class, Class>
            processedClassRelations, boolean watchAllFields) throws Exception {

        boolean isConfigBundleEmpty = true;

        // invoke setters
        for (Method method : targetClass.getMethods()) {

            if (method.getName().substring(0, 3).equals("set") && method.getParameters().length == 1) {

                Class parameterType = method.getParameters()[0].getType();

                // get field annotation - @ConfigValue
                Field field = targetClass.getDeclaredField(setterToField(method.getName()));
                ConfigValue fieldAnnotation = null;
                if (field != null) {
                    fieldAnnotation = field.getAnnotation(ConfigValue.class);
                }

                // watch nested class or list if all fields in the bean are annotated with watch or if a field is
                // annotated with watch
                boolean watchNestedClass = watchAllFields;
                if (watchNestedClass == false) {
                    if (fieldAnnotation != null) {
                        watchNestedClass = fieldAnnotation.watch();
                    }
                }

                // process primitives
                if (Arrays.asList(primitives).contains(parameterType)) {

                    Optional<String> value = getValueOfPrimitive(parameterType, getKeyName(targetClass, method
                            .getName(), keyPrefix));

                    if (value.isPresent()) {
                        isConfigBundleEmpty = false;
                        method.invoke(target, value.get());
                    }

                    if (watchAllFields || (fieldAnnotation != null && fieldAnnotation.watch())) {
                        deployWatcher(target, method, getKeyName(targetClass, method.getName(), keyPrefix));
                    }

                    // process nested objeccts
                } else if (!parameterType.isArray()) {

                    processedClassRelations.put(targetClass, parameterType);

                    Object nestedTarget = processNestedObject(targetClass, method, parameterType, keyPrefix,
                            processedClassRelations, -1, watchNestedClass);

                    // invoke setter method with initialised instance
                    method.invoke(target, nestedTarget);

                    // process arrays
                } else {

                    Class componentType = parameterType.getComponentType();

                    Object array = Array.newInstance(componentType, configurationUtil.getListSize(getKeyName
                            (targetClass, method.getName(), keyPrefix)).orElse(0));

                    // process list of primitives
                    if (Arrays.asList(primitives).contains(componentType)) {
                        for (int i = 0; i < Array.getLength(array); i++) {
                            Array.set(array, i, getValueOfPrimitive(componentType, getKeyName(targetClass, method
                                    .getName(), keyPrefix) + "[" + i + "]").get());
                        }

                        // process list of nested classes
                    } else {
                        for (int i = 0; i < Array.getLength(array); i++) {
                            Array.set(array, i, processNestedObject(targetClass, method, componentType, keyPrefix,
                                    processedClassRelations, i, watchNestedClass));

                        }
                    }

                    method.invoke(target, array);

                }
            }
        }
        return isConfigBundleEmpty;
    }

    /**
     * Returns a value of a primitive configuration type
     *
     * @param type configuration value type
     * @param key  configuration value key
     * @return
     */
    private Optional getValueOfPrimitive(Class type, String key) {

        if (type.equals(String.class)) {
            return configurationUtil.get(key);
        } else if (type.equals(Boolean.class)) {
            return configurationUtil.getBoolean(key);
        } else if (type.equals(Float.class)) {
            return configurationUtil.getFloat(key);
        } else if (type.equals(Double.class)) {
            return configurationUtil.getDouble(key);
        } else if (type.equals(Integer.class)) {
            return configurationUtil.getInteger(key);
        } else if (type.equals(Long.class)) {
            return configurationUtil.getLong(key);
        } else {
            return Optional.empty();
        }

    }

    /**
     * Create a new instance for nested class, check for cycles and populate nested instance.
     *
     * @param targetClass             target class
     * @param method                  processed method
     * @param parameterType           parameter type
     * @param keyPrefix               prefix used for generation of a configuration key
     * @param processedClassRelations class pairs that have already been processed (for cycle detection)
     * @param arrayIndex              array index for arrays of nested objects
     * @param watchAllFields          if true, enable watch on all fields
     * @return
     * @throws Exception
     */
    private Object processNestedObject(Class targetClass, Method method, Class parameterType, String keyPrefix,
                                       Map<Class, Class> processedClassRelations, int arrayIndex, boolean
                                               watchAllFields) throws Exception {

        Object nestedTarget = parameterType.getConstructor().newInstance();
        Class nestedTargetClass = nestedTarget.getClass();

        // check for cycles
        if (processedClassRelations.containsKey(nestedTargetClass) && processedClassRelations.get(nestedTargetClass)
                .equals(targetClass)) {
            log.warning("There is a cycle in the configuration class tree. ConfigBundle class may not " +
                    "be populated as expected.");
        } else {

            String key = getKeyName(targetClass, method.getName(), keyPrefix);

            if (arrayIndex >= 0) {
                key += "[" + arrayIndex + "]";
            }

            boolean isEmpty = processConfigBundleBeanSetters(nestedTarget, nestedTargetClass, key,
                    processedClassRelations, watchAllFields);

            if (isEmpty) {
                return null;
            }
        }

        return nestedTarget;
    }

    /**
     * Construct key name from prefix and field name or ConfigValue value (if present)
     *
     * @param targetClass target class
     * @param setter      name of the setter method
     * @param keyPrefix   prefix used for generation of a configuration key
     * @return key in format prefix.key-name
     */
    private String getKeyName(Class targetClass, String setter, String keyPrefix) throws Exception {

        String key;
        String prefix = keyPrefix;

        // get ConfigValue
        Field field = targetClass.getDeclaredField(setterToField(setter));
        ConfigValue fieldAnnotation = null;
        if (field != null) {
            fieldAnnotation = field.getAnnotation(ConfigValue.class);
        }

        if (fieldAnnotation != null && !fieldAnnotation.value().isEmpty()) {
            key = prefix + "." + StringUtils.camelCaseToHyphenCase(fieldAnnotation.value());

        } else {
            key = prefix + "." + StringUtils.camelCaseToHyphenCase(setter.substring(3));
        }

        return key;

    }

    /**
     * Generate a key prefix from annotation, class name, or parent prefix in case of nested classes.
     *
     * @param targetClass target class
     * @param keyPrefix   prefix used for generation of a configuration key
     * @return key prefix
     */
    private String getKeyPrefix(Class targetClass, String keyPrefix) {

        if (keyPrefix != null) {
            return keyPrefix;
        }

        String prefix = ((ConfigBundle) targetClass.getAnnotation(ConfigBundle.class)).value();

        if (prefix.isEmpty()) {
            prefix = StringUtils.camelCaseToHyphenCase(targetClass.getSimpleName());
        }

        return prefix;
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
     * @param target     target instance
     * @param method     method to invoke
     * @param watchedKey watched key
     * @throws Exception
     */
    private void deployWatcher(Object target, Method method, String watchedKey) throws Exception {

        configurationUtil.subscribe(watchedKey, (key, value) -> {

            if (Objects.equals(watchedKey, key)) {

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
                    } else if (Long.class.equals(method.getParameters()[0].getType())) {
                        try {
                            method.invoke(target, Long.parseLong(value));
                        } catch (NumberFormatException e) {
                            log.severe("Exception while storing new value: Number format exception. Expected:" +
                                    " Long. Value: " + value);
                        }
                    }
                } catch (IllegalAccessException e) {
                    log.severe("Illegal access exception: " + e.toString());
                } catch (InvocationTargetException e) {
                    log.severe("Invocation target exception: " + e.toString());
                }

            }
        });
    }
}


