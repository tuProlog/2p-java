package alice.tuprolog.factories;

import alice.tuprolog.AndroidPlatformLibraryManager;
import alice.tuprolog.JavaPlatformLibraryManager;
import alice.tuprolog.interfaces.ILibraryManager;

//import alice.tuprolog.DotNetPlatformLibraryManager;

//Alberto
public final class LibraryManagerFactory {

    /**
     * @author Alberto Sita
     */

    public static ILibraryManager getLibraryManagerForCurrentPlatform() {
        if (System.getProperty("java.vm.name").equals("Dalvik")) {
            //Android
            return new AndroidPlatformLibraryManager();
        } else if (System.getProperty("java.vm.name").equals("IKVM.NET")) {
            //.NET
//			return new DotNetPlatformLibraryManager();
            throw new IllegalStateException("Not supported");
        } else {
            //Java
            return new JavaPlatformLibraryManager();
        }
    }
}
