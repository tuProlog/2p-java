package alice.tuprolog.presentation;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;

public interface Serializer<T> {

    MIMEType getSupportedMIMEType();

    String toString(T object);

    String toString(Collection<? extends T> objects);

    void write(T object, Writer writer) throws IOException;

    default void write(T object, OutputStream writer) throws IOException {
        write(object, new OutputStreamWriter(writer));
    }

    void write(Collection<? extends T> objects, Writer writer) throws IOException;

    default void write(Collection<? extends T> objects, OutputStream writer) throws IOException {
        write(objects, new OutputStreamWriter(writer));
    }

    static <X> Serializer<X> of(Class<? extends X> type, MIMEType mimeType) {
        return Presentation.getSerializer(type, mimeType);
    }
}
