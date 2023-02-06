package com.intellif.config;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * 解决Gson无法转换json为接口的问题
 * @author Liangzhifeng
 * date: 2018/10/29
 */
public class PropertyBasedInterfaceMarshal implements
        JsonSerializer<Object>, JsonDeserializer<Object> {

    private static final String CLASS_META_KEY = "CLASS_META_KEY";

    @Override
    public Object deserialize(JsonElement jsonElement, Type type,
                              JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
        JsonObject jsonObj = jsonElement.getAsJsonObject();
        JsonElement element = jsonObj.get(CLASS_META_KEY);
        if (element == null) {
            return null;
        }
        String className = element.getAsString();
        try {
            Class<?> clz = Class.forName(className);
            return jsonDeserializationContext.deserialize(jsonElement, clz);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }

    @Override
    public JsonElement serialize(Object object, Type type,
                                 JsonSerializationContext jsonSerializationContext) {
        JsonElement jsonEle = jsonSerializationContext.serialize(object, object.getClass());
        jsonEle.getAsJsonObject().addProperty(CLASS_META_KEY,
                object.getClass().getCanonicalName());
        return jsonEle;
    }

}