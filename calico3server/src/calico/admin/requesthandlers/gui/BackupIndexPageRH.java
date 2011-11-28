package calico.admin.requesthandlers.gui;

import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;

import java.io.*;

import java.io.StringWriter;
import java.util.*;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.*;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
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
import calico.utils.*;
import calico.utils.CalicoUploadParser.Filedata;

import org.apache.velocity.*;
import org.apache.velocity.app.*;

public class BackupIndexPageRH extends AdminBasicRequestHandler
{
	public int getAllowedMethods()
	{
		return (METHOD_GET | METHOD_POST | METHOD_PUT );
	}
	
	
	protected void handleRequest(final HttpRequest request, final HttpResponse response, byte[] data) throws HttpException, IOException, JSONException, CalicoAPIErrorException
	{
		
		try
		{
			CalicoUploadParser parser = new CalicoUploadParser(data, request);
			parser.parse();
			Filedata ulfile = parser.getFile("Filedata");
			
			if( !ulfile.getName().endsWith(".csb"))
			{
				throw new CalicoInvalidBackupException("You must upload a .csb file");
			}
			
			FastByteArrayInputStream bais = new FastByteArrayInputStream(ulfile.getData() );
			//ByteArrayInputStream bais = new ByteArrayInputStream(ulfile.getData() );
			CalicoBackupHandler.restoreBackupStream(bais);
			
			throw new RedirectException("/gui/backup/?upload=1");
		}
		catch(CalicoInvalidBackupException e)
		{
			throw new RedirectException("/gui/backup/?upload=2");
		}
		
		
		
	}
	
	protected void handleRequest(final HttpRequest request, final HttpResponse response) throws HttpException, IOException, JSONException, CalicoAPIErrorException
	{
		Properties params = this.getURLParams(request);
		
		try
		{
			GUITemplate gt = new GUITemplate("backup/index.vm");
			gt.setSection("home");
			gt.put("get", params);
			gt.getOutput(response);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
