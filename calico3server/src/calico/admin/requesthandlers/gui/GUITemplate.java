package calico.admin.requesthandlers.gui;

import java.io.IOException;

import java.io.StringWriter;
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

public class GUITemplate
{
	private VelocityEngine engine = null;
	private Template template = null;
	private VelocityContext context = null;
	
	private String tplname = "";
	
	public GUITemplate()
	{
		
	}
	
	public GUITemplate(String tplname) throws Exception
	{
		this.tplname = tplname;
		init();
	}
	
	public void init() throws Exception
	{
		this.engine = new VelocityEngine();
		
		this.engine.setProperty("file.resource.loader.class","org.apache.velocity.runtime.resource.loader.FileResourceLoader");
		this.engine.setProperty("file.resource.loader.path","admintpl/");
		
		
		//this.engine.setProperty("resource.loader","jar");
		this.engine.setProperty("resource.loader","file");
		
		this.engine.setProperty("jar.resource.loader.class","org.apache.velocity.runtime.resource.loader.JarResourceLoader");
		this.engine.setProperty("jar.resource.loader.path","jar:file:libs/calico_templates.jar");
		this.engine.init();
		
		this.template = this.engine.getTemplate(this.tplname);
		this.context = new VelocityContext();
		
		
		put("g_title", "Calico3Server Admin");
		put("coptions", CalicoConfig.getConfigHashMap() );
		
		setSection("home");
		
	}
	
	
	public void put(String key, Object value)
	{
		this.context.put(key, value);
	}
	
	public String getOutputString() throws IOException
	{
		StringWriter writer = new StringWriter();
        this.template.merge( this.context, writer );
        return writer.toString();
	}
	
	public void getOutput(final HttpResponse response) throws IOException
	{
		StringEntity body = new StringEntity( getOutputString() );
		body.setContentType("text/html");
		response.setEntity(body);
	}
	
	
	public void setSection(String section)
	{
		put("g_section",section);
	}
	
	
}
