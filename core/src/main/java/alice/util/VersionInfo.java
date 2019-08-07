package alice.util;

public class VersionInfo {
    private static final String ENGINE_VERSION = "4.0";
    private static final String JAVA_SPECIFIC_VERSION = "3";
    private static final String NET_SPECIFIC_VERSION = "0";

    public static String getEngineVersion() {
        return ENGINE_VERSION;
    }

    public static String getPlatform() {
        String vmName = System.getProperty("java.vm.name");

        if (vmName.equals("IKVM.NET")) {
            return ".NET";
        } else {
            return  "Java";
        }
    }

    public static String getSpecificVersion() {
        String vmName = System.getProperty("java.vm.name");

        if (vmName.equals("IKVM.NET")) {
            return NET_SPECIFIC_VERSION;
        } else {
            return JAVA_SPECIFIC_VERSION;
        }
    }

    public static String getCompleteVersion() {
        return getEngineVersion() + "." + getSpecificVersion();
    }
}