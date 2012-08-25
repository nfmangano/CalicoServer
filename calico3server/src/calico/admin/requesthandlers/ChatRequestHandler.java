package calico.admin.requesthandlers;

import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.io.*;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.*;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.*;
import org.apache.http.entity.StringEntity;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import calico.*;
import calico.admin.*;
import calico.admin.exceptions.*;
import calico.admin.requesthandlers.AdminBasicRequestHandler;
import calico.controllers.*;
import calico.utils.CalicoBackupHandler;
import calico.utils.CalicoInvalidBackupException;
import calico.utils.CalicoUploadParser;
import calico.uuid.UUIDAllocator;

import org.apache.velocity.*;
import org.apache.velocity.app.*;

public class ChatRequestHandler extends AdminBasicRequestHandler
{
	private static TreeMap<String,ChatSession> sessions = new TreeMap<String,ChatSession>();

	private static Pattern PAT_SET_CANVAS = Pattern.compile("^/set canvas ([a-zA-Z0-9]{2,})$", Pattern.CASE_INSENSITIVE);
	private static Pattern PAT_SCRAP = Pattern.compile("^/scrap (.+)$", Pattern.CASE_INSENSITIVE);
	
	private static Pattern PAT_GET_CANVAS_IMAGE = Pattern.compile("^/get canvas ([a-zA-Z0-9]{2,})$", Pattern.CASE_INSENSITIVE);
	private static Pattern PAT_IMAGE_SCRAP = Pattern.compile("^/imagescrap ([-_:\\.\\/a-zA-Z0-9]+)$", Pattern.CASE_INSENSITIVE);
//	/imagescrap "http://website.com/image.jpg"
	
	private class ChatSession
	{
		public String service = "";
		public String user = "";
		public String key = "";
		public long currentCanvasUUID = 0L;
		
		public ChatSession(String service, String user)
		{
			this.service = service;
			this.user = user;
			this.key = user.toUpperCase()+"@"+service.toUpperCase();
		}
	}
	
	public int getAllowedMethods()
	{
		return (METHOD_GET | METHOD_POST );
	}
	
	
	protected void handleRequest(final HttpRequest request, final HttpResponse response, byte[] data) throws HttpException, IOException, JSONException, CalicoAPIErrorException
	{
		String strData = new String(data);
		Properties params = parseURLParams(strData);
		
		
		String chatService = params.getProperty("service","SERVER").toUpperCase();
		String username = params.getProperty("user");
		String message = params.getProperty("msg");
		
		//System.out.println(username+" ("+chatService+") says \""+message+"\"");

		handleChatMessage(chatService, username, message, response);
	}
	
	protected void handleRequest(final HttpRequest request, final HttpResponse response) throws HttpException, IOException, JSONException, CalicoAPIErrorException
	{
		Properties params = this.getURLParams(request);
		
		String chatService = params.getProperty("service","SERVER").toUpperCase();
		String username = params.getProperty("user");
		String message = params.getProperty("msg");
		
		handleChatMessage(chatService, username, message, response);
	}
	
	private boolean handleChatMessage(String service, String user, String message, final HttpResponse response) throws HttpException, IOException
	{
		
		if(user==null || user.length()==0)
		{
			StringEntity body = new StringEntity("EMPTY_USER");
			response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
			body.setContentType("text/plain");
	        response.setEntity(body);
	        return false;
		}
		if(message==null || message.length()==0)
		{
			StringEntity body = new StringEntity("EMPTY_MESSAGE");
			response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
			body.setContentType("text/plain");
	        response.setEntity(body);
	        return false;
		}
		
		// Chat session key
		String key = user.toUpperCase()+"@"+service.toUpperCase();
		
		ChatSession session = null;
		////// Chat session loading
		if(ChatRequestHandler.sessions.containsKey(key))
		{
			System.out.println("Loading previous chat session");
			session = ChatRequestHandler.sessions.get(key);
		}
		else
		{
			session = new ChatSession(service,user);
		}
		//////////////////////////// COMMAND MATCHING 
		
		if(message.charAt(0)=='/')
		{
			Matcher matcher = Pattern.compile(".*").matcher(message);
			if(matcher.usePattern(PAT_SET_CANVAS).find())
			{
				String canvmatch = matcher.group(1).toUpperCase();
				long cuuid = canvasCoordToUUID(canvmatch);
				if(cuuid==0L)
				{
					
					return respond(session, response, canvmatch+" is not a valid canvas");
					
				}
				session.currentCanvasUUID = cuuid;
				return respond(session, response, "Setting current canvas to "+canvmatch);
			}
			else if (matcher.usePattern(PAT_IMAGE_SCRAP).find())
			{
				if(session.currentCanvasUUID==0L)
				{
					return respond(session, response, "You need to set your canvas first using \"/set canvas [coord]\"");
				}
				String imageURL = matcher.group(1);
				long uuid = UUIDAllocator.getUUID();
				CGroupController.createImageGroup(uuid, session.currentCanvasUUID, imageURL, 50, 50);
				return respond(session, response, "Image scrap created");
				
			}
			else if(matcher.usePattern(PAT_SCRAP).find())
			{
				if(session.currentCanvasUUID==0L)
				{
					return respond(session, response, "You need to set your canvas first using \"/set canvas [coord]\"");
				}
				String scraptxt = matcher.group(1);
				long uuid = UUIDAllocator.getUUID();
				CGroupController.start(uuid, session.currentCanvasUUID, 0L, true);
				CGroupController.append(uuid, 300, 300);
				CGroupController.set_text(uuid, scraptxt);
				CGroupController.finish(uuid, false);
				return respond(session, response, "Scrap created");
			}
			else if(matcher.usePattern(PAT_GET_CANVAS_IMAGE).find())
			{
				
			}
		}
		
		
		//////////////////////////// END COMMAND MATCHING 
		
		System.out.println(user+" ("+service+") says \""+message+"\"");
		return respond(session, response, "Sorry, I didn't understand that.");
		
	}
	
	private boolean respond(ChatSession session, final HttpResponse response, String message) throws HttpException, IOException
	{
		// update the session
		ChatRequestHandler.sessions.put(session.key, session);
		
		// output
		StringEntity body = new StringEntity(message);
		body.setContentType("text/plain");
        response.setEntity(body);
        return true;
	}
	
	private boolean respond(ChatSession session, final HttpResponse response) throws HttpException, IOException
	{
		return respond(session, response, "");
	}
	
	
	private long canvasCoordToUUID(String coord)
	{
		coord = coord.toUpperCase();
//		int index = Integer.parseInt(indexString);
		long[] cuuids = CCanvasController.canvases.keySet().toLongArray();
		for(int i=0;i<cuuids.length;i++)
		{
			if(coord.equalsIgnoreCase(CCanvasController.canvases.get(cuuids[i]).getCoordText()))
			{
				return cuuids[i];
			}
		}
		return 0L;
	}
	
}
