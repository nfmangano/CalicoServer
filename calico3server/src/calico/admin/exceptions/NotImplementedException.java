package calico.admin.exceptions;

import calico.admin.*;

import org.apache.http.*;

public class NotImplementedException extends CalicoAPIErrorException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotImplementedException()
	{
		super(HttpStatus.SC_NOT_IMPLEMENTED, "NotImplemented");
	}
}