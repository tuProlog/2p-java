package alice.tuprolog.presentation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Objects;

class SimpleDeserializer<T> implements Deserializer<T> {

    private final Class<T> clazz;
    private final MIMEType mimeType;
    private final ObjectMapper mapper;

    SimpleDeserializer(Class<T> clazz, MIMEType mimeType, ObjectMapper mapper) {
        this.clazz = Objects.requireNonNull(clazz);
        this.mimeType = Objects.requireNonNull(mimeType);
        this.mapper = Objects.requireNonNull(mapper);
    }

    @Override
    public Class<T> getSupportedType() {
        return clazz;
    }

    @Override
    public MIMEType getSupportedMIMEType() {
        return mimeType;
    }

    @Override
    public T fromString(String string) {
        try {
            return read(new StringReader(string));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<T> listFromString(String string) {
        try {
            return readList(new StringReader(string));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public T read(Reader reader) throws IOException {
        return readImpl(reader, getSupportedType());
    }

    protected final <X> X readImpl(Reader reader, Class<X> clazz) throws IOException {
        return mapper.readValue(reader, clazz);
    }

    protected final <X> X readImpl(Reader reader, TypeReference<X> clazz) throws IOException {
        return mapper.readValue(reader, clazz);
    }

    @Override
    public List<T> readList(Reader reader) throws IOException {
        return readImpl(reader, new TypeReference<List<T>>() {});
    }

}
