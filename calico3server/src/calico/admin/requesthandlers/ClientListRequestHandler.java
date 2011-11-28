package calico.admin.requesthandlers;


import java.io.*;
import java.net.*;
import java.util.*;
import calico.admin.exceptions.*;

import org.apache.http.*;
import org.apache.http.entity.*;
import org.apache.http.protocol.*;
import org.apache.http.util.*;

import org.json.me.*;

import calico.admin.*;
import calico.clients.*;
import calico.*;

public class ClientListRequestHandler extends AdminBasicRequestHandler
{
	
	
	protected void handleRequest(final HttpRequest request, final HttpResponse response) throws HttpException, IOException, JSONException, CalicoAPIErrorException
	{
		Properties resp = new Properties();
		
		int[] clientsArr = ClientManager.get_clientids();
		
		resp.setProperty("clientcount", ""+clientsArr.length);
		
		for(int i=0;i<clientsArr.length;i++)
		{
			int clientid = clientsArr[i];

			resp.putAll( ClientManager.getClientProperties(clientid, true) );
		}
		throw new SuccessException(resp);
	}
	
	
}