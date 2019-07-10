package alice.tuprolog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Presentation {

    private static final Map<Pair<Class<?>, MIMETypes>, Serializer<?>> serializers = new HashMap<>();
    private static final Map<Pair<Class<?>, MIMETypes>, Deserializer<?>> deserializers = new HashMap<>();
    private static final Map<MIMETypes, ObjectMapper> mappers;

    static {
        final Map<MIMETypes, ObjectMapper> _mappers = new HashMap<>();
        _mappers.put(MIMETypes.APPLICATION_JSON, createMapper(ObjectMapper.class));
        _mappers.put(MIMETypes.APPLICATION_YAML, createMapper(YAMLMapper.class));
//        _mappers.put(MIMETypes.APPLICATION_XML, createMapper(XmlMapper.class));
        mappers = Collections.unmodifiableMap(_mappers);
    }

    public static EnumSet<MIMETypes> getSupportedMIMETypes() {
        return EnumSet.copyOf(mappers.keySet());
    }

    public static ObjectMapper getObjectMapper(MIMETypes mimeTypes) {
        return mappers.get(mimeTypes);
    }

    public static <OM extends ObjectMapper> OM createMapper(Class<OM> mapperClass) {
        try {
            final OM mapper = mapperClass.getConstructor().newInstance();
            mapper.registerModule(new JavaTimeModule());
            return mapper;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> Serializer<T> getSerializer(Class<T> type, MIMETypes mimeType) {
        final Pair<Class<T>, MIMETypes> key1 = Pair.of(type, mimeType);
        final Optional<Pair<Class<?>, MIMETypes>> key2;

        if (serializers.containsKey(key1)) {
            return (Serializer<T>) serializers.get(key1);
        } else if ((key2 = serializers.keySet().stream()
                               .filter(it -> it.getRight().equals(mimeType))
                               .filter(it -> it.getLeft().isAssignableFrom(type))
                               .findAny()).isPresent()) {
            return (Serializer<T>) serializers.get(key2);
        } else {
            throw new IllegalArgumentException("Class-MIMEType combo not supported: " + type.getName() + " --> " + mimeType);
        }
    }

    public static <T> Deserializer<T> getDeserializer(Class<T> type, MIMETypes mimeType) {
        final Pair<Class<T>, MIMETypes> key1 = Pair.of(type, mimeType);
        final Optional<Pair<Class<?>, MIMETypes>> key2;

        if (deserializers.containsKey(key1)) {
            return (Deserializer<T>) deserializers.get(key1);
        } else if ((key2 = deserializers.keySet().stream()
                                      .filter(it -> it.getRight().equals(mimeType))
                                      .filter(it -> it.getLeft().isAssignableFrom(type))
                                      .findAny()).isPresent()) {
            return (Deserializer<T>) deserializers.get(key2);
        } else {
            throw new IllegalArgumentException("Class-MIMEType combo not supported: " + type.getName() + " <-- " + mimeType);
        }
    }

    public static <T> void register(Class<T> type, Serializer<? super T> serializer) {
        final Pair<Class<?>, MIMETypes> key = Pair.of(type, serializer.getSupportedMIMEType());
        if (serializers.containsKey(key)) {
            throw new IllegalArgumentException("Class-MIMEType combo already registered: " + type.getName() + " --> " + serializer.getSupportedMIMEType());
        }
        serializers.put(key, serializer);
    }

    public static <T> void register(Class<T> type, Deserializer<? extends T> deserializer) {
        final Pair<Class<?>, MIMETypes> key = Pair.of(type, deserializer.getSupportedMIMEType());
        if (deserializers.containsKey(key)) {
            throw new IllegalArgumentException("Class-MIMEType combo already registered: " + type.getName() + " <-- " + deserializer.getSupportedMIMEType());
        }
        deserializers.put(key, deserializer);
    }


    static {
        for (final MIMETypes mimeType : getSupportedMIMETypes()) {
            register(Term.class, new DynamicSerializer<Term>(mimeType, getObjectMapper(mimeType)) {
                @Override
                public Object toDynamicObject(final Term object) {
                    return TermUtils.termToDynamicObject(object);
                }
            });
            register(Term.class, new DynamicDeserializer<Term>(Term.class, mimeType, getObjectMapper(mimeType)) {

                @Override
                public Term fromDynamicObject(final Object dynamicObject) {
                    return TermUtils.dynamicObjectToTerm(dynamicObject);
                }
            });
        }
    }
}
