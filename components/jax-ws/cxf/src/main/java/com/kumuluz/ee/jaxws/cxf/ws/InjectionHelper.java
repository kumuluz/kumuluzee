/*
 *  Copyright (c) 2014-2018 Kumuluz and/or its affiliates
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
package com.kumuluz.ee.jaxws.cxf.ws;

import jakarta.annotation.Resource;
import jakarta.xml.ws.WebServiceContext;

import java.lang.reflect.Field;

/**
 * @author gpor89
 * @since 3.0.0
 */
public final class InjectionHelper {

    private InjectionHelper() {
    }

    public static void injectWebServiceContext(final Object instance, final WebServiceContext ctx) {
        final Class<?> instanceClass = instance.getClass();

        //method injection not supported!

        // inject @Resource
        final Field[] resourceAnnotatedFields = instanceClass.getDeclaredFields();
        for (Field field : resourceAnnotatedFields) {
            try {
                if (field.getDeclaredAnnotation(Resource.class) != null && field.getType() == WebServiceContext.class) {
                    setField(instance, field, ctx);
                }
            } catch (Exception e) {
                throw new RuntimeException("Cannot inject @Resource annotated field: " + field, e);
            }
        }
    }

    private static void setField(final Object instance, final Field field, final Object value) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            field.setAccessible(field.isAccessible());
        }
    }

}