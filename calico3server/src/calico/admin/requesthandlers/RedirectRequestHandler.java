package calico.admin.requesthandlers;


import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.batik.dom.*;
import org.apache.batik.svggen.*;
import org.apache.commons.vfs.*;
import org.apache.http.*;
import org.apache.http.entity.*;
import org.apache.http.protocol.*;
import org.apache.http.util.*;

import org.apache.http.message.*;
import org.json.me.*;
import org.w3c.dom.*;

import calico.admin.*;
import calico.admin.exceptions.SuccessException;
import calico.clients.*;
import calico.controllers.*;
import calico.utils.*;
import calico.uuid.*;
import calico.*;

import java.util.zip.*;

public class RedirectRequestHandler extends AdminBasicRequestHandler
{
	private String redirectUrl = "";
	public RedirectRequestHandler(String redirectUrl)
	{
		this.redirectUrl = redirectUrl;
	}
	
	protected void handleRequest(final HttpRequest request, final HttpResponse response) throws HttpException, IOException, JSONException, CalicoAPIErrorException
	{
		
		
		StringEntity body = new StringEntity("Redirecting you to <a href=\""+this.redirectUrl+"\">"+this.redirectUrl+"</a>");
		response.setStatusCode(HttpStatus.SC_MOVED_TEMPORARILY);
		response.addHeader("Location", this.redirectUrl);
        body.setContentType("text/html");
        
        response.setEntity(body);
		
	}
	
	
}