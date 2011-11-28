package calico.admin.exceptions;

import java.util.*;

import org.apache.http.*;

import calico.admin.*;
import java.util.*;
import calico.admin.CalicoAPIErrorException;

public class RedirectException extends CalicoAPIErrorException
{
	private static final long serialVersionUID = 1L;

	private String url = "/";
	
	public RedirectException(String url)
	{
		super(HttpStatus.SC_OK, "OK");
		this.url = url;
	}
	
	public Properties toProperties()
	{
		return new Properties();
	}
	
	public String getURL()
	{
		return this.url;
	}
	
}
