package calico.admin.requesthandlers;


import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.http.*;
import org.apache.http.entity.*;
import org.apache.http.protocol.*;
import org.apache.http.util.*;

import org.json.me.*;

import calico.*;
import calico.admin.*;
import calico.admin.exceptions.SuccessException;
import calico.clients.*;
import calico.utils.CalicoBackupHandler;

public class ServerGCRequestHandler extends AdminBasicRequestHandler
{
	
	
	protected void handleRequest(final HttpRequest request, final HttpResponse response) throws HttpException, IOException, JSONException, CalicoAPIErrorException
	{
		Runtime.getRuntime().gc();
				
		
		Properties props = new Properties();
		props.setProperty("Status", "OK");
		
		throw new SuccessException(props);
		
	}
	
	
}