package calico.admin.requesthandlers.gui;

import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;

import java.io.IOException;
import java.io.OutputStream;

import java.io.StringWriter;
import java.util.*;

import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
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
import calico.networking.netstuff.ByteUtils;
import calico.utils.*;
import calico.utils.CalicoUploadParser.Filedata;

import org.apache.velocity.*;
import org.apache.velocity.app.*;

public class ImageUploadRH extends AdminBasicRequestHandler
{
	public int getAllowedMethods()
	{
		return (METHOD_GET | METHOD_POST | METHOD_PUT );
	}
	

	
	protected void handleRequest(final HttpRequest request, final HttpResponse response, byte[] data) throws HttpException, IOException, JSONException, CalicoAPIErrorException
	{
		
		try
		{
			//System.out.println("CONTENT: "+);
			/*
			FileObject backupFile2 = COptions.fs.resolveFile("uploads/images/ultest.dat");
			backupFile2.createFile();
			
			
			FileContent content2 = backupFile2.getContent();
		
			OutputStream fos2 = content2.getOutputStream();

			fos2.write(Arrays.toString(request.getAllHeaders()).getBytes());
			fos2.write(data);
			fos2.close();
						
			backupFile2.close();
			*/
			
			
			CalicoUploadParser parser = new CalicoUploadParser(data, request);
			parser.parse();
			
			Filedata ulfile = parser.getFile("Filedata");
			
			
			//String mimetype = parser.getFileInfo().getProperty("type");
			
			String filename = CalicoUtils.cleanFilename(ulfile.getName());
			System.out.println("FILENAME: "+filename);
			
			FileObject backupFile = COptions.fs.resolveFile("uploads/images/"+System.currentTimeMillis()+"_"+filename);
			backupFile.createFile();
			
			
			FileContent content = backupFile.getContent();
		
			OutputStream fos = content.getOutputStream();

			fos.write(ulfile.getData());
			fos.close();
						
			backupFile.close();
			
			//b3f566bc3275bf39781d3f11ea87572c
			
			throw new RedirectException("/gui/imagemgr/upload?upload=1");
		}
		catch(IOException e)
		{
			e.printStackTrace();
			throw new RedirectException("/gui/imagemgr/upload?upload=2");
		}
		
		
		
	}
	
	protected void handleRequest(final HttpRequest request, final HttpResponse response) throws HttpException, IOException, JSONException, CalicoAPIErrorException
	{
		Properties params = this.getURLParams(request);
		try
		{
			GUITemplate gt = new GUITemplate("imagemgr/upload.vm");
			gt.setSection("home");
			gt.put("get", params);
			
			//gt.put("test.param.yar", "this is a big test");
			gt.getOutput(response);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
