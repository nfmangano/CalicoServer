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
import calico.admin.exceptions.*;
import calico.clients.*;
import calico.controllers.*;

import it.unimi.dsi.fastutil.longs.*;

public class CanvasGetRequestHandler extends AdminBasicRequestHandler
{
	
	
	
	//  /session/canvases/get/<uuid>
	
	protected void handleRequest(final HttpRequest request, final HttpResponse response) throws HttpException, IOException, JSONException, CalicoAPIErrorException, SuccessException
	{
	
		Properties params = getURLParams(request);
		
		final long uuid = Long.parseLong(params.getProperty("uuid","0"));
		
		if(uuid==0)
		{
			// ERROR
			throw new NotFoundException("The canvas you requested was not found.");
		}
		
		if(!CCanvasController.canvases.containsKey(uuid))
		{
			throw new NotFoundException("The canvas you requested was not found.");
		}
		


		throw new SuccessException( CCanvasController.canvases.get(uuid).toProperties() );
		
		
	}
	
	
}