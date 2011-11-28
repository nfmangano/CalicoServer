package calico.admin.exceptions;

import org.apache.http.*;

import calico.admin.*;
import java.util.*;

public class SuccessException extends CalicoAPIErrorException
{
	private static final long serialVersionUID = 1L;

	private Properties props = new Properties();
	
	public SuccessException(Properties props)
	{
		super(HttpStatus.SC_OK, "OK");
		this.props = props;
	}
	
	public Properties toProperties()
	{
		return this.props;
	}
}