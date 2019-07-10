package alice.tuprolog;

import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface Serializer<T> {

    MIMETypes getSupportedMIMEType();

    String toString(T object);

    String toString(Collection<? extends T> objects);

    void write(T object, Writer writer);

    void write(Collection<? extends T> objects, Writer writer);
}
