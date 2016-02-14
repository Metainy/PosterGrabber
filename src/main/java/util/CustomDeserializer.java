package util;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Created by Mody on 16-Dec-15.
 */

/**
 * Custom Json deserializer to return the embedded Json objects
 *
 * @param <T> Generic data type
 */
public class CustomDeserializer<T> implements JsonDeserializer<T> {

    public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        // Get the "DiscoverMovie" element from the parsed Json
        JsonElement content = jsonElement.getAsJsonObject().get("results");

        // Return the deserialized Json
        return new Gson().fromJson(content, type);
    }
}
