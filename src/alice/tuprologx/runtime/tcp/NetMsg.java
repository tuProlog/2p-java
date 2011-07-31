package alice.tuprologx.runtime.tcp;

@SuppressWarnings("serial")
public class NetMsg implements java.io.Serializable {
    /**
	 * @uml.property  name="methodName"
	 */
    public String methodName;

    public NetMsg(){
    }

    public NetMsg(String name) {
        methodName=name;
    }
}

