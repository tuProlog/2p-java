package alice.tuprologx.middleware.LPaaS;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.Gson;

import alice.tuprolog.SolveInfo;

//Alberto
public final class LPaaSResponse implements Serializable {

	/**
     * @author Alberto Sita
     * 
     */
	
	private static final long serialVersionUID = 1L;
	
	private String message = null;
	private String response = null;

	public String getMessage() {
		if(message == null)
			return "No message available";
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	
	public SolveInfo getSolveInfoIfAvailable() {
		if(response == null)
			return null;
		else {
			try{
				return SolveInfo.fromJSON(response);
			} catch (Exception e){
				return null;
			}
		}
	}
	
	public ArrayList<SolveInfo> getArrayOfSolveInfoIfAvailable() {
		if(response == null)
			return null;
		else {
			ArrayList<SolveInfo> infos = new ArrayList<SolveInfo>();
			try{
				Gson gson = new Gson();
				@SuppressWarnings("unchecked")
				ArrayList<String> res = gson.fromJson(response, ArrayList.class);
				for(String s : res){
					infos.add(SolveInfo.fromJSON(s));
				}
				return infos;
			} catch (Exception e){
				return null;
			}
		}
	}
	
}
