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
import calico.controllers.CCanvasController;

import it.unimi.dsi.fastutil.longs.*;

public class CanvasGetActionHistoryRequestHandler extends AdminBasicRequestHandler
{
	
	//  /session/canvases/get/<uuid>
	
	protected void handleRequest(final HttpRequest request, final HttpResponse response) throws HttpException, IOException, JSONException, CalicoAPIErrorException
	{
		response.setStatusCode(HttpStatus.SC_OK);
		
		URL requestURL = uri2url(request.getRequestLine().getUri());
		
		Properties params = urlQuery2Properties(requestURL);
		
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
			
		
		JSONObject resp = new JSONObject();

		//resp.put("actionhistory", CCanvasController.canvases.get(uuid).getActionHistoryAsJSON() );
		
		
		StringEntity body = new StringEntity(resp.toString());
		body.setContentType(COptions.APIDefaultContentType);
		response.setEntity(body);
		
	}
	
	
}