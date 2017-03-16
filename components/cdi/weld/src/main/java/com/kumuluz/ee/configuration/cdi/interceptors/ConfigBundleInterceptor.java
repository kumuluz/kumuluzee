package com.kumuluz.ee.configuration.cdi.interceptors;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;

import javax.annotation.PostConstruct;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;

/**
 * @author Tilen Faganel
 * @since 2.1.0
 */
@Interceptor
@ConfigBundle
public class ConfigBundleInterceptor {

    @PostConstruct
    public Object loadConfiguration(InvocationContext ic) throws Exception {

        Object target = ic.getTarget();

        for (Method m : target.getClass().getMethods()) {

            if (m.getName().substring(0, 3).equals("set") && m.getParameters().length == 1) {

                if (m.getParameters()[0].getType().equals(String.class)) {

                    m.invoke(target, ConfigurationUtil.getInstance().get(setterToField(m.getName()))
                            .orElse(null));

                } else if (m.getParameters()[0].getType().equals(Boolean.class)) {

                    m.invoke(target, ConfigurationUtil.getInstance().getBoolean(setterToField(m
                            .getName())).orElse(null));

                } else if (m.getParameters()[0].getType().equals(Float.class)) {

                    m.invoke(target, ConfigurationUtil.getInstance().getFloat(setterToField(m.getName
                            ())).orElse(null));

                } else if (m.getParameters()[0].getType().equals(Double.class)) {

                    m.invoke(target, ConfigurationUtil.getInstance().getDouble(setterToField(m.getName
                            ())).orElse(null));

                } else if (m.getParameters()[0].getType().equals(Integer.class)) {

                    m.invoke(target, ConfigurationUtil.getInstance().getInteger(setterToField(m
                            .getName())).orElse(null));
                }
            }
        }

        return ic.proceed();
    }

    private String setterToField(String setter) {
        return Character.toLowerCase(setter.charAt(3)) + setter.substring(4);
    }
}


