package calico.admin.exceptions;

import calico.admin.*;

import org.apache.http.*;

public class MethodNotAllowedException extends CalicoAPIErrorException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MethodNotAllowedException()
	{
		super(HttpStatus.SC_METHOD_NOT_ALLOWED, "MethodNotAllowed");
		//message = "The requested method is not allowed.";
	}
}