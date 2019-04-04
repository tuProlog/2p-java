package alice.tuprolog.json;

import com.google.gson.*;

import java.lang.reflect.Type;

//Alberto
public class JSONMarshaller implements JsonSerializer<Object>, JsonDeserializer<Object> {

	private static final String TYPE_INFO = "TYPE_INFO";

	@Override
	public JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
		JsonElement jsonEle = jsonSerializationContext.serialize(object, object.getClass());
		jsonEle.getAsJsonObject().addProperty(TYPE_INFO, object.getClass().getCanonicalName());
		return jsonEle;
	}
	
	@Override
	public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObj = jsonElement.getAsJsonObject();
		String className = jsonObj.get(TYPE_INFO).getAsString();
		try {
			Class<?> clz = Class.forName(className);
			return jsonDeserializationContext.deserialize(jsonElement, clz);
		}
		catch (ClassNotFoundException e) {
			throw new JsonParseException(e);
		}
	}
	
}
