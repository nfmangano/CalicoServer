package calico.admin.requesthandlers;


import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.http.*;
import org.apache.http.entity.*;
import org.apache.http.protocol.*;
import org.apache.http.util.*;

import org.apache.http.message.*;
import org.json.me.*;

import calico.admin.*;
import calico.admin.exceptions.*;
import calico.clients.*;
import calico.*;

import calico.components.*;
import calico.controllers.CArrowController;
import calico.controllers.CCanvasController;
import calico.controllers.CGroupController;
import calico.controllers.CStrokeController;
import calico.utils.CalicoBackupHandler;
import calico.utils.CalicoInvalidBackupException;
import calico.uuid.UUIDAllocator;

import java.util.zip.*;

public class BackupRestoreRequestHandler extends AdminBasicRequestHandler
{
	public int getAllowedMethods()
	{
		return (METHOD_GET | METHOD_POST | METHOD_PUT );
	}
	
	protected void handleRequest(final HttpRequest request, final HttpResponse response) throws HttpException, IOException, JSONException, CalicoAPIErrorException
	{
		handleRequest(request, response, new byte[]{(byte)0});
	}
	
	protected void handleRequest(final HttpRequest request, final HttpResponse response, byte[] bytes) throws HttpException, IOException, JSONException, CalicoAPIErrorException
	{

		Properties params = this.getURLParams(request);
		
		String source = params.getProperty("source","FILE").toUpperCase();
		
		try
		{
			if(source.equals("FILE"))
			{
				String filename = params.getProperty("file","./backup_auto.csb");
			
			
				Properties props = CalicoBackupHandler.getBackupFileInfo(filename);
				props.setProperty("Restored", "OK");
				
				CalicoBackupHandler.restoreBackupFile(filename);
				
				throw new SuccessException(props);
			}
			else if(source.equals("UPLOAD"))
			{
				FastByteArrayInputStream bais = new FastByteArrayInputStream(bytes);
				CalicoBackupHandler.restoreBackupStream(bais);
				
				Properties props = new Properties();
				props.setProperty("Restored", "OK");
				
				
				throw new SuccessException(props);
			}
			

		}
		catch(CalicoInvalidBackupException e)
		{
			throw new CalicoAPIErrorException("Invalid Backup");
		}
		
	}
		
	
	
}