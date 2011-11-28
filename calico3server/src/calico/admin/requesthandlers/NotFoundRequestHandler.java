package calico.admin.requesthandlers;


import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.http.*;
import org.apache.http.entity.*;
import org.apache.http.protocol.*;
import org.apache.http.util.*;

import org.json.me.*;

import calico.ProcessQueue;
import calico.admin.*;
import calico.admin.exceptions.*;
import calico.clients.*;

public class NotFoundRequestHandler extends AdminBasicRequestHandler
{
	
	
	protected void handleRequest(final HttpRequest request, final HttpResponse response) throws HttpException, IOException, JSONException, CalicoAPIErrorException
	{
		throw new NotFoundException();
		
	}
	
	
}