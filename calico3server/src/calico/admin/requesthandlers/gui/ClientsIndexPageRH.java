package calico.admin.requesthandlers.gui;

import java.io.IOException;

import java.io.StringWriter;
import java.net.InetAddress;
import java.util.*;
import calico.clients.*;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import calico.*;
import calico.admin.CalicoAPIErrorException;
import calico.admin.requesthandlers.AdminBasicRequestHandler;

import org.apache.velocity.*;
import org.apache.velocity.app.*;

public class ClientsIndexPageRH extends AdminBasicRequestHandler
{
	
	public static class ClientInfo
	{
		public String host = "";
		public String port = "";
		public String clientid = "";
		public String username = "";
		public String getClientID(){return this.clientid;}
		public String getHost(){return this.host;}
		public String getPort(){return this.port;}
		public String getUsername(){return this.username;}
		public String getHostname()
		{
			try
			{
				return InetAddress.getByName(this.host).getCanonicalHostName();
			}
			catch(Exception e)
			{
				return "";
			}
		}
	}
	
	
	protected void handleRequest(final HttpRequest request, final HttpResponse response) throws HttpException, IOException, JSONException, CalicoAPIErrorException
	{
		try
		{
			GUITemplate gt = new GUITemplate("clients/index.vm");
			gt.setSection("clients");
			
			
			ArrayList<ClientInfo> clients = new ArrayList<ClientInfo>();
			int[] clientids = ClientManager.get_clientids();
			
			for(int i=0;i<clientids.length;i++)
			{
				ClientInfo temp = new ClientInfo();
				Properties props = ClientManager.getClientProperties(clientids[i]);
				temp.clientid = ""+clientids[i];
				temp.host = props.getProperty("tcp.host");
				temp.port = props.getProperty("tcp.port");
				temp.username = props.getProperty("username");
				clients.add(temp);
			}
			
			gt.put("clients", clients);
			
			//gt.put("test.param.yar", "this is a big test");
			gt.getOutput(response);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
