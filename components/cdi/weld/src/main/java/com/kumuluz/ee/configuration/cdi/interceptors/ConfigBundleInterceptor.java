package com.kumuluz.ee.configuration.cdi.interceptors;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * @author Tilen Faganel
 * @since 2.1.0
 */
@Interceptor
@ConfigBundle
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class ConfigBundleInterceptor {

    /**
     * Method initialises class fields from configuration.
     */
    @PostConstruct
    public Object loadConfiguration(InvocationContext ic) throws Exception {

        Object target = ic.getTarget();
        ConfigurationUtil configurationUtil = ConfigurationUtil.getInstance();

        // invoke setters for fields which are defined in configuration
        for (Method m : target.getClass().getMethods()) {

            if (m.getName().substring(0, 3).equals("set") && m.getParameters().length == 1) {

                if (m.getParameters()[0].getType().equals(String.class)) {

                    Optional<String> value = configurationUtil.get(getKeyName(target, m.getName()));

                    if (value.isPresent()) {
                        m.invoke(target, value.get());
                    }

                } else if (m.getParameters()[0].getType().equals(Boolean.class)) {

                    Optional<Boolean> value = configurationUtil.getBoolean(getKeyName(target, m.getName()));

                    if (value.isPresent()) {
                        m.invoke(target, value.get());
                    }

                } else if (m.getParameters()[0].getType().equals(Float.class)) {

                    Optional<Float> value = configurationUtil.getFloat(getKeyName(target, m.getName()));

                    if (value.isPresent()) {
                        m.invoke(target, value.get());
                    }

                } else if (m.getParameters()[0].getType().equals(Double.class)) {

                    Optional<Double> value = configurationUtil.getDouble(getKeyName(target, m.getName()));

                    if (value.isPresent()) {
                        m.invoke(target, value.get());
                    }

                } else if (m.getParameters()[0].getType().equals(Integer.class)) {

                    Optional<Integer> value = configurationUtil.getInteger(getKeyName(target, m.getName()));

                    if (value.isPresent()) {
                        m.invoke(target, value.get());
                    }

                }
            }
        }

        return ic.proceed();
    }

    /**
     * Construct key name from prefix and field name or ConfigValue value (if present)
     *
     * @param target target class
     * @param setter name of the setter method
     * @return key in format prefix.key-name
     */
    private String getKeyName(Object target, String setter) throws Exception {

        String key;

        // get prefix
        String prefix = target.getClass().getAnnotation(ConfigBundle.class).value();
        if (prefix.isEmpty()) {
            prefix = camelCaseToHyphenCase(target.getClass().getSuperclass().getSimpleName());
        }

        // get ConfigValue
        Field field = target.getClass().getSuperclass().getDeclaredField(setterToField(setter));
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
}


