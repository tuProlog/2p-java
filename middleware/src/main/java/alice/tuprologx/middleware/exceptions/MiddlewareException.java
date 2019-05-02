package alice.tuprologx.middleware.exceptions;

//Alberto
public class MiddlewareException extends Exception {
	
	/**
     * @author Alberto Sita
     * 
     */

	private static final long serialVersionUID = 1L;
	
	private String message;
	
	public MiddlewareException(String message){
		super();
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
