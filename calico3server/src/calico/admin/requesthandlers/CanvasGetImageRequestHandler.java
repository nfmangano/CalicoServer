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

import calico.components.*;
import calico.controllers.CCanvasController;


import java.awt.*;
import java.awt.image.BufferedImage;

import org.apache.batik.svggen.*;
import org.apache.batik.dom.*;

import org.w3c.dom.*;

import javax.imageio.*;


public class CanvasGetImageRequestHandler extends AdminBasicRequestHandler
{
	
	//  /session/canvases/get/<uuid>
	
	protected void handleRequest(final HttpRequest request, final HttpResponse response) throws HttpException, IOException, JSONException, CalicoAPIErrorException
	{
		response.setStatusCode(HttpStatus.SC_OK);
		
		URL requestURL = uri2url(request.getRequestLine().getUri());
		
		Properties params = urlQuery2Properties(requestURL);
		
		final long uuid = Long.parseLong(params.getProperty("uuid","0"));
		
		
		if(uuid==0)
		{
			// ERROR
			throw new NotFoundException("The canvas you requested was not found.");
		}
		
		
		final String imageType = params.getProperty("type","PNG").toUpperCase();

		final int imageWidth = Integer.parseInt(params.getProperty("w",COptions.canvas.width + "" /*"1024"*/));
		final int imageHeight = Integer.parseInt(params.getProperty("h",COptions.canvas.height + "" /*"768"*/));
		
		if(!CCanvasController.canvases.containsKey(uuid))
		{
			throw new NotFoundException("The canvas you requested was not found.");
		}
		EntityTemplate body = null;
		if(imageType.equals("SVG"))
		{
			 body = new EntityTemplate(new ContentProducer() {
				public void writeTo(final OutputStream outstream) throws IOException {
					DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
	
					// Create an instance of org.w3c.dom.Document.
					String svgNS = "http://www.w3.org/2000/svg";
					Document document = domImpl.createDocument(svgNS, "svg", null);
	
					SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(document);
					ctx.setComment("Calico SVG Generator");
					
					// Create an instance of the SVG Generator.
					SVGGraphics2D svgGenerator = new SVGGraphics2D(ctx,false);
	
					CCanvasController.canvases.get(uuid).render(svgGenerator);
					
					// Finally, stream out SVG to the standard output using UTF-8 encoding.
					boolean useCSS = true; // we want to use CSS style attributes
					Writer out = new OutputStreamWriter(outstream, "UTF-8");
					svgGenerator.stream(out, useCSS);
				}
			});
			body.setContentType("image/svg+xml");
		}
		else if(imageType.equals("PNG") || imageType.equals("JPEG") || imageType.equals("BMP"))
		{
			// Generate the raw image
			body = new EntityTemplate(new ContentProducer() {
				public void writeTo(final OutputStream outstream) throws IOException {
					BufferedImage bi = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
					Graphics2D ig2 = bi.createGraphics();
					CCanvasController.canvases.get(uuid).render(ig2);
					ImageIO.write(bi, imageType, outstream);
				}
			});
			
			// Set the mimetype
			if(imageType.equals("PNG"))
			{
				body.setContentType("image/png");
			}
			else if(imageType.equals("JPEG"))
			{
				body.setContentType("image/jpeg");
			}
			else if(imageType.equals("BMP"))
			{
				body.setContentType("image/bitmap");
			}
		}
		
		response.setEntity(body);
		
	}

	/*
	GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
	Font[] fontlist = env.getAllFonts();
	for(int i=0;i<fontlist.length;i++)
	{
		System.out.println("FONT: "+fontlist[i].getName());
	}*/
	
	
}