package relativity.transform;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class TransformElementAdapter implements JsonSerializer<TransformElement>, JsonDeserializer<TransformElement> {

	private static final String CLASSNAME = "classname";
	private static final String DATA = "data";

	@SuppressWarnings("unchecked")
	public TransformElement deserialize(JsonElement jsonElement, Type type,
			JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

		JsonObject jsonObject = jsonElement.getAsJsonObject();
		JsonPrimitive prim = (JsonPrimitive) jsonObject.get(CLASSNAME);
		String className = prim.getAsString();
		Class<TransformElement> klass;
		try {
			klass = (Class<TransformElement>) Class.forName(className);
			return jsonDeserializationContext.deserialize(jsonObject.get(DATA), klass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public JsonElement serialize(TransformElement jsonElement, Type type,
			JsonSerializationContext jsonSerializationContext) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(CLASSNAME, jsonElement.getClass().getName());
		jsonObject.add(DATA, jsonSerializationContext.serialize(jsonElement));
		return jsonObject;
	}
}