package calico.admin.exceptions;

import calico.admin.*;

import org.apache.http.*;

public class AuthenticationFailureException extends CalicoAPIErrorException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AuthenticationFailureException()
	{
		this.code = HttpStatus.SC_UNAUTHORIZED;
		this.name = "AuthenticationFailure";
		//message = "The requested method is not allowed.";
	}
}