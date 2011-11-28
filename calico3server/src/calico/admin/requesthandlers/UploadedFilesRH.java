package calico.admin.requesthandlers;

import java.io.IOException;

import java.io.StringWriter;

import org.apache.commons.vfs.*;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import calico.*;
import calico.admin.CalicoAPIErrorException;
import calico.admin.exceptions.NotFoundException;
import calico.admin.requesthandlers.AdminBasicRequestHandler;

import org.apache.velocity.*;
import org.apache.velocity.app.*;

public class UploadedFilesRH extends AdminBasicRequestHandler
{
	protected void handleRequest(final HttpRequest request, final HttpResponse response) throws HttpException, IOException, JSONException, CalicoAPIErrorException
	{
		try
		{
			String uri = request.getRequestLine().getUri().replaceFirst("/uploads/", "");
			
			FileObject file = COptions.fs.resolveFile("uploads/"+uri);
			if(!file.exists())
			{
				throw new IOException();
			}
			
			
			
			
			FileContent content = file.getContent();
			String mimetype = content.getContentInfo().getContentType();
			
			System.out.println("TYPE: "+mimetype);
			
	        /*StringEntity body = new StringEntity( uri );
			body.setContentType("text/html");
			response.setEntity(body);*/
			
			ByteArrayEntity bae = new ByteArrayEntity(FileUtil.getContent(file));
			//ByteArrayEntity bae = new ByteArrayEntity();
			bae.setContentType(mimetype);
			response.setEntity(bae);
			file.close();
			
			
		}
		catch(IOException e)
		{
			throw new NotFoundException();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
