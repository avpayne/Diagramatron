package relativity;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class EventElementAdapter implements JsonSerializer<AbstractEvent>, JsonDeserializer<AbstractEvent> {

	private static final String CLASSNAME = "classname";
	private static final String DATA = "data";

	@SuppressWarnings("unchecked")
	public AbstractEvent deserialize(JsonElement jsonElement, Type type,
			JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

		JsonObject jsonObject = jsonElement.getAsJsonObject();
		JsonPrimitive prim = (JsonPrimitive) jsonObject.get(CLASSNAME);
		String className = prim.getAsString();
		Class<AbstractEvent> klass;
		try {
			klass = (Class<AbstractEvent>) Class.forName(className);
			return jsonDeserializationContext.deserialize(jsonObject.get(DATA), klass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public JsonElement serialize(AbstractEvent jsonElement, Type type,
			JsonSerializationContext jsonSerializationContext) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(CLASSNAME, jsonElement.getClass().getName());
		jsonObject.add(DATA, jsonSerializationContext.serialize(jsonElement));
		return jsonObject;
	}
}