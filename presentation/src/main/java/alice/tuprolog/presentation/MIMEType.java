package alice.tuprolog.presentation;

import java.util.stream.Stream;

public enum MIMEType {

    APPLICATION_JSON("application", "json", ".json"),

    APPLICATION_YAML("application", "yaml", ".yml"),

    APPLICATION_XML("application", "xml", ".xml"),

    APPLICATION_PROLOG("application", "prolog", ".pl"),

    TEXT_HTML("text", "html", ".html"),

    TEXT_PLAIN("text", "plain", ".txt"),

    ANY("*", "*", ""),

    APPLICATION_ANY("application", "*", ".*");

    private final String type, subtype, extension;

    MIMEType(String type, String subtype, String extension) {
        this.type = type;
        this.subtype = subtype;
        this.extension = extension;
    }

    public String getFileExtension() {
        return extension;
    }

    public String getType() {
        return type;
    }

    public String getSubtype() {
        return subtype;
    }

    @Override
    public String toString() {
        return type + "/" + subtype;
    }

    public boolean matches(String other) {
        return match(this, other);
    }

    public static boolean match(MIMEType mime, String other) {
        if (other == null || !other.contains("/")) return false;

        final String[] parts = other.split("/");

        if (parts.length != 2) return false;

        return match(mime, parts[0], parts[1]);
    }

    private static boolean match(MIMEType mime, String type, String subtype) {
        return (Stream.of(mime.getType(), type).anyMatch("*"::equals)
                || mime.getType().equalsIgnoreCase(type))
                && (Stream.of(mime.getSubtype(), subtype).anyMatch("*"::equals)
                        || mime.getSubtype().equalsIgnoreCase(subtype));
    }

    public static MIMEType parse(String string) {
        return Stream.of(values()).filter(it -> it.toString().equalsIgnoreCase(string)).findAny().orElseGet(() -> {
           throw new IllegalArgumentException(string);
        });
    }
}
