package calico.admin;

import org.apache.http.*;
import java.util.*;

public class CalicoAPIErrorException extends Exception
{
	/**
	 * 
	 */
	protected static final long serialVersionUID = 1L;
	
	
	public String name = "UnknownError";
	public String message = "";
	public int code = HttpStatus.SC_BAD_REQUEST;
	public String comments = null;
	
	
	public CalicoAPIErrorException(int code, String n, String m)
	{
		this.code = code;
		this.name = n;
		this.message = m;
	}
	
	public CalicoAPIErrorException(){this("UnknownError");}
	public CalicoAPIErrorException(String n){this(n,"");}
	public CalicoAPIErrorException(int c, String n){this(c, n,"");}
	public CalicoAPIErrorException(String n, String m){this(HttpStatus.SC_BAD_REQUEST, n, m);}
	
	public String toJSON()
	{
		String resp = "{\"CalicoAPIError\":{";
		resp += "\"code\":\""+name+"\"";
		if(message.length()>0)
		{
			resp += ",\"message\":\""+message.replaceAll("\"", "\\\"")+"\"";
		}
		resp += "}}";
		return resp;
		//{"memory":{"max":532742144,"used":753424,"total":2031616,"free":1278192}}
	}
	
	public Properties toProperties()
	{
		Properties props = new Properties();
		props.setProperty("ErrorName", this.name);
		props.setProperty("ErrorMessage", this.message);
		props.setProperty("ErrorCode", Integer.toString(this.code));
		return props;
	}

}