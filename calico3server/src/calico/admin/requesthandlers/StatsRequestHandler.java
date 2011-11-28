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
import calico.utils.Ticker;

public class StatsRequestHandler extends AdminBasicRequestHandler
{
	
	
	protected void handleRequest(final HttpRequest request, final HttpResponse response) throws HttpException, IOException, JSONException, CalicoAPIErrorException
	{
		
		Properties props = new Properties();

		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		props.setProperty("Memory.FreeMemory", ""+free );
		props.setProperty("Memory.UsedMemory",""+(total-free));
		props.setProperty("Memory.TotalMemory", ""+total );
		props.setProperty("Memory.MaxMemory", ""+Runtime.getRuntime().maxMemory() );
		
		props.setProperty("Server.AvgTickRate", ""+Ticker.ticker.getAverageTickrate() );
		
		//getAverageTickrate
		
		throw new SuccessException(props);
		
	}
	
	
}