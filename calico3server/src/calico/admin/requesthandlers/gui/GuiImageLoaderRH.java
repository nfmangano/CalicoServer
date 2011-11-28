package calico.admin.requesthandlers.gui;

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
import calico.admin.requesthandlers.AdminBasicRequestHandler;

import org.apache.velocity.*;
import org.apache.velocity.app.*;

public class GuiImageLoaderRH extends AdminBasicRequestHandler
{
	protected void handleRequest(final HttpRequest request, final HttpResponse response) throws HttpException, IOException, JSONException, CalicoAPIErrorException
	{
		try
		{
			String uri = request.getRequestLine().getUri().replaceFirst("/gui/images", "");
			
			FileObject image = COptions.fs.resolveFile("jar:libs/admin_images.jar!"+uri);
			
	        /*StringEntity body = new StringEntity( uri );
			body.setContentType("text/html");
			response.setEntity(body);*/
			
			ByteArrayEntity bae = new ByteArrayEntity(FileUtil.getContent(image));	
			bae.setContentType("application/calico-backup-state");
			response.setEntity(bae);
			image.close();
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
