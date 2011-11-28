package calico.utils;

public class CalicoInvalidBackupException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String message = "Invalid Backup";
	
	public CalicoInvalidBackupException()
	{
		// blah
	}
	
	public CalicoInvalidBackupException(String message)
	{
		this.message = message;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
}
