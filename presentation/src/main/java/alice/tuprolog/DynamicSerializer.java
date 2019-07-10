package alice.tuprolog;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

abstract class DynamicSerializer<T> extends SimpleSerializer<T> {

    DynamicSerializer(MIMETypes mimeType, ObjectMapper mapper) {
        super(mimeType, mapper);
    }

    protected abstract Object toDynamicObject(T object);

    @Override
    public void write(T object, Writer writer) {
        writeImpl(toDynamicObject(object), writer);
    }

    @Override
    public void write(Collection<? extends T> objects, Writer writer) {
        writeImpl(toDynamicObject(objects), writer);
    }

    protected List<Object> toDynamicObject(Collection<? extends T> objects) {
        return objects.stream().map(this::toDynamicObject).collect(Collectors.toList());
    }
}
