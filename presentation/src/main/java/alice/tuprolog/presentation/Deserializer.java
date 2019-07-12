package alice.tuprolog.presentation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public interface Deserializer<T> {

    Class<T> getSupportedType();

    MIMEType getSupportedMIMEType();

    T fromString(String string);

    List<T>  listFromString(String string);

    T read(Reader reader) throws IOException;

    default T read(InputStream reader) throws IOException {
        return read(new InputStreamReader(reader));
    }

    List<T>  readList(Reader reader) throws IOException;

    default List<T> readList(InputStream reader) throws IOException {
        return readList(new InputStreamReader(reader));
    }

    static <X> Deserializer<X> of(Class<X> type, MIMEType mimeType) {
        return Presentation.getDeserializer(type, mimeType);
    }
}
