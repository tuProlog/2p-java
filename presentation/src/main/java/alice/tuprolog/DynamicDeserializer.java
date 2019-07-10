package alice.tuprolog;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Reader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

abstract class DynamicDeserializer<T> extends SimpleDeserializer<T> {

    DynamicDeserializer(Class<T> clazz, MIMETypes mimeType, ObjectMapper mapper) {
        super(clazz, mimeType, mapper);
    }

    protected abstract T fromDynamicObject(Object dynamicObject);

    @Override
    public T read(Reader reader) {
        return fromDynamicObject(readImpl(reader, Object.class));
    }

    @Override
    public List<T> readList(Reader reader) {
        return listFromDynamicObject(readImpl(reader, Object.class));
    }

    protected List<T> listFromDynamicObject(Object dynamicObject) {
        if (!(dynamicObject instanceof List) && !(dynamicObject instanceof Map)) {
            throw new IllegalArgumentException();
        }

        List<?> list = (dynamicObject instanceof List)
                       ? (List<?>) dynamicObject
                       : Collections.singletonList((Map<?, ?>) dynamicObject);

        return list.stream().map(this::fromDynamicObject).collect(Collectors.toList());
    }
}
