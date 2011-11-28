package calico.admin.requesthandlers.gui;

import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;

import java.io.*;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.*;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.*;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import calico.*;
import calico.admin.*;
import calico.admin.exceptions.*;
import calico.admin.requesthandlers.AdminBasicRequestHandler;
import calico.utils.CalicoBackupHandler;
import calico.utils.CalicoInvalidBackupException;
import calico.utils.CalicoUploadParser;

import org.apache.velocity.*;
import org.apache.velocity.app.*;

public class ConfigIndexRH extends AdminBasicRequestHandler
{
	public int getAllowedMethods()
	{
		return (METHOD_GET | METHOD_POST | METHOD_PUT );
	}
	
	
	protected void handleRequest(final HttpRequest request, final HttpResponse response, byte[] data) throws HttpException, IOException, JSONException, CalicoAPIErrorException
	{
		String strData = new String(data);
		Properties params = parseURLParams(strData);
		
		try
		{
			for (Enumeration<?> e = params.propertyNames(); e.hasMoreElements();)
			{
				String str = (String) e.nextElement();
				System.out.println("Varsubmit: "+str+" = "+params.getProperty(str));
				if(str.startsWith("config."))
				{
					str = str.replaceAll("config\\.", "");
					CalicoConfig.setConfigVariable(str, params.getProperty("config."+str));
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		handleRequest(request,response);
	}
	
	protected void handleRequest(final HttpRequest request, final HttpResponse response) throws HttpException, IOException, JSONException, CalicoAPIErrorException
	{
		Properties params = this.getURLParams(request);
		
		try
		{
			GUITemplate gt = new GUITemplate("config/index.vm");
			gt.setSection("config");
			gt.put("get", params);
			
			//gt.put("config_client_threadopts_sleeptime", CalicoConfig.getConfigField("client.threadopts.sleeptime").get(null));
			setupConfigClass(gt);
			gt.getOutput(response);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	private static void setupConfigClass(GUITemplate gt) throws IllegalAccessException
	{
		Class<?>[] configClasses = COptions.class.getDeclaredClasses();
		
		for(int i=0;i<configClasses.length;i++)
		{
			processClassConfig(configClasses[i], gt);
		}
	}
	
	private static void processClassConfig(Class<?> className, GUITemplate gt) throws IllegalAccessException
	{
		//logger.debug("CONFIG ["+className.getCanonicalName()+"]");
		
		Field[] fields = className.getDeclaredFields();
		for(int i=0;i<fields.length;i++)
		{
			Field field = fields[i];
			
			String fieldname = className.getCanonicalName()+"."+field.getName();
			fieldname = fieldname.replaceFirst(COptions.class.getCanonicalName()+".", "");
			
			System.out.println("CONFIG SET: "+"config_"+fieldname.replaceAll("\\.","_")+" = "+field.get(null).toString());
			gt.put("this.is.a.test","YARRRRR");
			gt.put("config_"+fieldname.replaceAll("\\.","_"), field.get(null).toString());
			
			
		}///
		
		
		Class<?>[] configClasses = className.getDeclaredClasses();
		for(int i=0;i<configClasses.length;i++)
		{
			processClassConfig(configClasses[i], gt);
		}
	}///////
}
