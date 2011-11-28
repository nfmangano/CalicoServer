package calico.admin;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.HttpServerConnection;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpService;
import org.apache.log4j.Logger;

import calico.CalicoServer;

public class AdminWorkerThread extends Thread
{

	private static Logger logger = Logger.getLogger(AdminServer.class.getName());
	
	private final HttpService httpservice;
	private final HttpServerConnection conn;

	public AdminWorkerThread(final HttpService httpservice, final HttpServerConnection conn)
	{
		super();
		this.httpservice = httpservice;
		this.conn = conn;
	}

	public void run()
	{
		
		logger.trace("New connection thread");
		HttpContext context = new BasicHttpContext(null);
		try
		{
			while(!Thread.interrupted() && this.conn.isOpen())
			{
				this.httpservice.handleRequest(this.conn, context);
			}
		}
		catch(ConnectionClosedException ex)
		{
			logger.error("Client closed connection");
		}
		catch(SocketTimeoutException stoe)
		{
			// ignore
		}
		catch(IOException ex)
		{
			logger.error("I/O error: " + ex.toString());
		}
		catch(HttpException ex)
		{
			logger.error("Unrecoverable HTTP protocol violation: " + ex.getMessage());
		}
		finally
		{
			try
			{
				this.conn.shutdown();
			}
			catch(IOException ignore) {}
		}
	}

}