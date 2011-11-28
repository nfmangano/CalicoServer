package calico.admin.exceptions;

import calico.admin.*;

import org.apache.http.*;

public class NotFoundException extends CalicoAPIErrorException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotFoundException()
	{
		super(HttpStatus.SC_NOT_FOUND, "NotFound");
	}
	public NotFoundException(String m)
	{
		super(HttpStatus.SC_NOT_FOUND, "NotFound", m);
	}
}