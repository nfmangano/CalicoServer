package calico.admin;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.http.*;
import org.apache.http.entity.*;
import org.apache.http.protocol.*;
import org.apache.http.util.*;

import org.json.me.*;

public class AdminClientsRequestHandler implements HttpRequestHandler
{
        
    
    public AdminClientsRequestHandler()
    {
        super();
    }
    
    public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context) throws HttpException, IOException
    {

        String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
        if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST"))
        {
            throw new MethodNotSupportedException(method + " method not supported"); 
        }
        final String target = request.getRequestLine().getUri();

        JSONObject jsondata = new JSONObject();
        
        if (request instanceof HttpEntityEnclosingRequest)
        {
            HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
            byte[] entityContent = EntityUtils.toByteArray(entity);
            String content = new String(entityContent);
            System.out.println("Incoming entity content (bytes): " + entityContent.length);
            System.out.println("Incoming entity content (bytes): " + content);
            try
            {
            	jsondata = new JSONObject(content);
            }
            catch(Exception e)
            {
            }
        }
    
        String apikey = request.getFirstHeader("X-Calico-APIKey").getValue();

        test(request, response, context);
        
        /*
        try
        {
        
        response.setStatusCode(HttpStatus.SC_OK);
        response.addHeader("X-Calico-RequestNumber", System.currentTimeMillis()+"");
        response.addHeader("X-Calico-Apikeygiven", apikey);
        StringEntity body = new StringEntity("You requested "+target+" file"+jsondata.getString("test"));
        body.setContentType("text/plain");
        
        response.setEntity(body);
        }
        catch(JSONException ex)
        {
        	
        }*/
   
    }
    
    public void test(final HttpRequest request, final HttpResponse response, final HttpContext context) throws HttpException, IOException
    {
    	 response.setStatusCode(HttpStatus.SC_OK);
         response.addHeader("X-Calico-RequestNumber", System.currentTimeMillis()+"");
         response.addHeader("X-Calico-Apikeygiven", "BLAH");
         StringEntity body = new StringEntity("You requested YAR file");
         body.setContentType("text/plain");
         
         response.setEntity(body);
    }
    
    
}
