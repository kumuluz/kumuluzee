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
package com.kumuluz.ee.jsonp.configuration.utils;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;

import javax.json.*;
import java.util.Optional;

/**
 * Enables retrieval of a part of a configuration hierarchy as a JSON-P object/array.
 *
 * @author Urban Malc
 * @since 3.4.0
 */
public class JsonConfigurationUtil {

    private static JsonConfigurationUtil instance = null;

    private static synchronized void initialize() {
        if (instance == null) {
            instance = getBuilder().build();
        }
    }

    public static JsonConfigurationUtil getInstance() {
        if (instance == null) {
            initialize();
        }

        return instance;
    }

    public static Builder getBuilder() {
        return new JsonConfigurationUtil.Builder();
    }

    private JsonConfigurationUtil() {
    }

    private ConfigurationUtil config = ConfigurationUtil.getInstance();
    private String mapToNull;
    private String mapToEmptyObject;
    private String mapToEmptyArray;

    private JsonValue getMappingIfAvailable(String s) {
        if (mapToNull != null && mapToNull.equals(s)) {
            return JsonValue.NULL;
        }
        if (mapToEmptyObject != null && mapToEmptyObject.equals(s)) {
            return JsonValue.EMPTY_JSON_OBJECT;
        }
        if (mapToEmptyArray != null && mapToEmptyArray.equals(s)) {
            return JsonValue.EMPTY_JSON_ARRAY;
        }

        return null;
    }

    public Optional<JsonObject> getJsonObject(String key) {

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

        config.getMapKeys(key).ifPresent(mapList -> mapList.forEach(prop -> {
            String nextKey = (key.equals("")) ? prop : key + "." + prop;

            config.getType(nextKey).ifPresent(type -> {
                switch (type) {
                    case MAP:
                        getJsonObject(nextKey).ifPresent(o -> objectBuilder.add(prop, o));
                        break;
                    case LIST:
                        getJsonArray(nextKey).ifPresent(a -> objectBuilder.add(prop, a));
                        break;
                    case INTEGER:
                        config.getInteger(nextKey).ifPresent(value -> objectBuilder.add(prop, value));
                        break;
                    case LONG:
                        config.getLong(nextKey).ifPresent(value -> objectBuilder.add(prop, value));
                        break;
                    case DOUBLE:
                        config.getDouble(nextKey).ifPresent(value -> objectBuilder.add(prop, value));
                        break;
                    case FLOAT:
                        config.getFloat(nextKey).ifPresent(value -> objectBuilder.add(prop, value));
                        break;
                    case BOOLEAN:
                        config.getBoolean(nextKey).ifPresent(value -> objectBuilder.add(prop, value));
                        break;
                    case STRING:
                        config.get(nextKey).ifPresent(s -> {
                            JsonValue mapped = getMappingIfAvailable(s);
                            if (mapped != null) {
                                objectBuilder.add(prop, mapped);
                            } else {
                                objectBuilder.add(prop, s);
                            }
                        });
                        break;
                }
            });
        }));

        JsonObject jsonObject = objectBuilder.build();
        if (jsonObject.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(jsonObject);
    }

    public Optional<JsonArray> getJsonArray(String key) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        config.getListSize(key).ifPresent(listSize -> {
            for (int i = 0; i < listSize; i++) {
                String nextKey = key + "[" + i + "]";
                config.getType(nextKey).ifPresent(type -> {
                    switch (type) {
                        case MAP:
                            getJsonObject(nextKey).ifPresent(arrayBuilder::add);
                            break;
                        case LIST:
                            getJsonArray(nextKey).ifPresent(arrayBuilder::add);
                            break;
                        case INTEGER:
                            config.getInteger(nextKey).ifPresent(arrayBuilder::add);
                            break;
                        case LONG:
                            config.getLong(nextKey).ifPresent(arrayBuilder::add);
                            break;
                        case DOUBLE:
                            config.getDouble(nextKey).ifPresent(arrayBuilder::add);
                            break;
                        case FLOAT:
                            config.getFloat(nextKey).ifPresent(arrayBuilder::add);
                            break;
                        case BOOLEAN:
                            config.getBoolean(nextKey).ifPresent(arrayBuilder::add);
                            break;
                        case STRING:
                            config.get(nextKey).ifPresent(s -> {
                                JsonValue mapped = getMappingIfAvailable(s);
                                if (mapped != null) {
                                    arrayBuilder.add(mapped);
                                } else {
                                    arrayBuilder.add(s);
                                }
                            });
                            break;
                    }
                });
            }
        });

        JsonArray jsonArray = arrayBuilder.build();
        if (jsonArray.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(jsonArray);
    }


    public static final class Builder {
        private String mapToNull;
        private String mapToEmptyObject;
        private String mapToEmptyArray;

        private Builder() {
        }

        public Builder mapToNull(String mapToNull) {
            this.mapToNull = mapToNull;
            return this;
        }

        public Builder mapToEmptyObject(String mapToEmptyObject) {
            this.mapToEmptyObject = mapToEmptyObject;
            return this;
        }

        public Builder mapToEmptyArray(String mapToEmptyArray) {
            this.mapToEmptyArray = mapToEmptyArray;
            return this;
        }

        public JsonConfigurationUtil build() {
            JsonConfigurationUtil jsonConfigurationUtil = new JsonConfigurationUtil();
            jsonConfigurationUtil.mapToEmptyObject = this.mapToEmptyObject;
            jsonConfigurationUtil.mapToEmptyArray = this.mapToEmptyArray;
            jsonConfigurationUtil.mapToNull = this.mapToNull;

            return jsonConfigurationUtil;
        }
    }
}
