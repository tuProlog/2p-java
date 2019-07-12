package alice.tuprolog.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Objects;

class SimpleSerializer<T> implements Serializer<T> {

    private final MIMEType mimeType;
    private final ObjectMapper mapper;

    SimpleSerializer(MIMEType mimeType, ObjectMapper mapper) {
        this.mimeType = Objects.requireNonNull(mimeType);
        this.mapper = Objects.requireNonNull(mapper);
    }

    @Override
    public MIMEType getSupportedMIMEType() {
        return mimeType;
    }

    @Override
    public String toString(T object) {
        final StringWriter sw = new StringWriter();
        try {
            write(object, sw);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return sw.toString();
    }

    @Override
    public String toString(Collection<? extends T> objects) {
        final StringWriter sw = new StringWriter();
        try {
            write(objects, sw);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return sw.toString();
    }

    @Override
    public void write(T object, Writer writer) throws IOException {
        writeImpl(object, writer);
    }

    protected void writeImpl(Object object, Writer writer) throws IOException {
        try {
            mapper.writeValue(writer, object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Cannot convert " + object + " into " + mimeType, e);
        }
    }

    @Override
    public void write(Collection<? extends T> objects, Writer writer) throws IOException {
        writeImpl(objects, writer);
    }

    protected final ObjectMapper getMapper() {
        return mapper;
    }

}
