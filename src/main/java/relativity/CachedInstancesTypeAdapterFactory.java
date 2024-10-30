package relativity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class CachedInstancesTypeAdapterFactory implements TypeAdapterFactory {

	@SuppressWarnings("rawtypes")
	private final Map<Class, Map> cachedMaps = new HashMap<>();

	public CachedInstancesTypeAdapterFactory(@SuppressWarnings("rawtypes") Set<Class> customizedClasses) {
		Objects.requireNonNull(customizedClasses);
		customizedClasses.forEach(clazz -> cachedMaps.compute(clazz, (c, m) -> new HashMap<>()));
	}

	public final <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		if (cachedMaps.containsKey(type.getRawType())) {
			final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
			return createCustomTypeAdapter(delegate);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private <T> TypeAdapter<T> createCustomTypeAdapter(TypeAdapter<T> delegate) {
		return new TypeAdapter<T>() {
			@Override
			public void write(JsonWriter out, T value) throws IOException {
				delegate.write(out, value);
			}

			@Override
			public T read(JsonReader in) throws IOException {
				Object deserialized = delegate.read(in);

				@SuppressWarnings("rawtypes")
				Map tInstances = Objects.requireNonNull(cachedMaps.get(deserialized.getClass()));
				return (T) tInstances.computeIfAbsent(deserialized, k -> deserialized);
			}
		};
	}
}