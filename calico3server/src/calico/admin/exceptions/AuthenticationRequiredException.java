package calico.admin.exceptions;

import calico.admin.*;

import org.apache.http.*;

public class AuthenticationRequiredException extends CalicoAPIErrorException
{
	private static final long serialVersionUID = 1L;

	public AuthenticationRequiredException()
	{
		this.code = HttpStatus.SC_FORBIDDEN;
		this.name = "AuthenticationRequired";
		//message = "The requested method is not allowed.";
	}
}