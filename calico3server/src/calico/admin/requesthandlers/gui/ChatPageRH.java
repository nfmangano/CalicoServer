package calico.admin.requesthandlers.gui;

import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;

import java.io.IOException;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.*;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import calico.*;
import calico.admin.CalicoAPIErrorException;
import calico.admin.exceptions.RedirectException;
import calico.admin.requesthandlers.AdminBasicRequestHandler;
import calico.controllers.CCanvasController;
import calico.controllers.CGroupController;
import calico.networking.netstuff.CalicoPacket;
import calico.networking.netstuff.NetworkCommand;
import calico.plugins.CalicoPluginManager;
import calico.plugins.PluginCommandParameters;
import calico.utils.CalicoBackupHandler;
import calico.utils.CalicoInvalidBackupException;
import calico.utils.CalicoUploadParser;
import calico.uuid.UUIDAllocator;

import org.apache.velocity.*;
import org.apache.velocity.app.*;

public class ChatPageRH extends AdminBasicRequestHandler
{

	
	public int getAllowedMethods()
	{
		return (METHOD_GET | METHOD_POST | METHOD_PUT );
	}
	
	protected void handleRequest(final HttpRequest request, final HttpResponse response, byte[] data) throws HttpException, IOException, JSONException, CalicoAPIErrorException
	{
		String strData = new String(data);
		Properties params = parseURLParams(strData);

		handleRequestInternal(request, response, params);
	}
	
	protected void handleRequest(final HttpRequest request, final HttpResponse response) throws HttpException, IOException, JSONException, CalicoAPIErrorException
	{
		handleRequestInternal(request, response, new Properties());
	}
	
	protected void handleRequestInternal(final HttpRequest request, final HttpResponse response, Properties params) throws HttpException, IOException, JSONException, CalicoAPIErrorException
	{
		try
		{
			GUITemplate gt = new GUITemplate("chat.vm");
			gt.setSection("chat");
			gt.getOutput(response);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	
}
