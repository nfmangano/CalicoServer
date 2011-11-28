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
import calico.controllers.CGroupController;
import calico.controllers.CStrokeController;

public class GroupGetRequestHandler extends AdminBasicRequestHandler
{
	
	//  /session/groups/get/<uuid>
	
	protected void handleRequest(final HttpRequest request, final HttpResponse response) throws HttpException, IOException, JSONException, CalicoAPIErrorException
	{
		
		Properties params = request2params(request);
		
		final long uuid = Long.parseLong(params.getProperty("uuid","0"));
		if(uuid==0)
		{
			// ERROR
			throw new NotFoundException("The group you requested was not found.");
		}
		
		if(!CGroupController.groups.containsKey(uuid))
		{
			throw new NotFoundException("The group you requested was not found.");
		}
		
		

		throw new SuccessException( CGroupController.groups.get(uuid).toProperties() );
	}
	
	
}