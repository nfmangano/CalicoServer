package calico.admin;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.http.*;
import org.apache.http.impl.*;
import org.apache.http.params.*;
import org.apache.http.protocol.*;
import org.apache.log4j.Logger;

import calico.COptions;
import calico.admin.requesthandlers.*;
import calico.admin.requesthandlers.gui.*;

public class AdminRequestListenerThread extends Thread
{
	private static Logger logger = Logger.getLogger(AdminServer.class.getName());

	
	private final HttpParams params; 
	private final HttpService httpService;

	public AdminRequestListenerThread() throws IOException
	{
		// 50 = backlog
		COptions.admin.serversocket = new ServerSocket(COptions.admin.listen.port, 50, InetAddress.getByName(COptions.admin.listen.host));

		this.params = new BasicHttpParams();
		this.params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, COptions.admin.listen.timeout);
		this.params.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, COptions.admin.listen.buffer);
		this.params.setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, COptions.admin.listen.stale_conn_check);
		this.params.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, COptions.admin.listen.tcp_nodelay);
		this.params.setParameter(CoreProtocolPNames.ORIGIN_SERVER, COptions.admin.server_signature);

		// Set up the HTTP protocol processor
		BasicHttpProcessor httpproc = new BasicHttpProcessor();//new HttpResponseInterceptor[] {
		httpproc.addInterceptor(new ResponseDate());
		httpproc.addInterceptor(new ResponseServer());
		httpproc.addInterceptor(new ResponseContent());
		httpproc.addInterceptor(new ResponseConnControl());
		
		// Set up request handlers
		HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();

		// Clients
		reqistry.register("/client/list*", new ClientListRequestHandler());
		reqistry.register("/client/kick*", new NotImplementedRequestHandler());
		reqistry.register("/client/get*", new NotImplementedRequestHandler());

		
		// Session Stuff///////////

		reqistry.register("/backup/generate*", new BackupGenerateRequestHandler());
		reqistry.register("/backup/restore*", new BackupRestoreRequestHandler());
		
		reqistry.register("/stroke/list*", new StrokeListRequestHandler());
		reqistry.register("/stroke/get*", new StrokeGetRequestHandler());
		
		reqistry.register("/group/list*", new GroupListRequestHandler());
		reqistry.register("/group/get*", new GroupGetRequestHandler());

		reqistry.register("/canvas/list*", new CanvasListRequestHandler());
		reqistry.register("/canvas/getimage*", new CanvasGetImageRequestHandler());
		reqistry.register("/canvas/get*", new CanvasGetRequestHandler());
		reqistry.register("/canvas/getactions*", new CanvasGetActionHistoryRequestHandler());
		

		reqistry.register("/arrow/list*", new NotImplementedRequestHandler());
		reqistry.register("/arrow/get*", new NotImplementedRequestHandler());
		
		///// END SESSION
		
		reqistry.register("/chat*", new ChatRequestHandler());
		
		// Config
		
		// Server
		reqistry.register("/server/shutdown*", new NotImplementedRequestHandler());
		reqistry.register("/server/gc*", new ServerGCRequestHandler());
		reqistry.register("/stats*", new StatsRequestHandler());
		
		
		// Debugging and stuff
		reqistry.register("/debug/unittest*", new UnitTestRequestHandler());

		reqistry.register("/gui/", new IndexPage());
		reqistry.register("/gui/backup/", new BackupIndexPageRH());
		reqistry.register("/gui/images/*", new GuiImageLoaderRH());
		reqistry.register("/gui/config/", new ConfigIndexRH());
		reqistry.register("/gui/command/help", new CommandHelpPageRH());
		reqistry.register("/gui/command*", new CommandPageRH());
		reqistry.register("/gui/clients/*", new ClientsIndexPageRH());
		
		reqistry.register("/gui/imagemgr/upload*", new ImageUploadRH());
		
		reqistry.register("/gui/chat*", new ChatPageRH());
		
		reqistry.register("/gui", new RedirectRequestHandler("/gui/"));
		
		reqistry.register("/uploads/*", new UploadedFilesRH());

		reqistry.register("/", new RedirectRequestHandler("/gui/"));
		
		// Default?
		reqistry.register("*", new NotFoundRequestHandler());

		// Set up the HTTP service
		this.httpService = new HttpService(
			httpproc, 
			new DefaultConnectionReuseStrategy(), 
			new DefaultHttpResponseFactory()
		);
		this.httpService.setParams(this.params);
		this.httpService.setHandlerResolver(reqistry);
	}

	public void run()
	{
		logger.info("Listening on port " + COptions.admin.serversocket.getLocalPort());
		while(!Thread.interrupted())
		{
			try
			{
				// Set up HTTP connection
				Socket socket = COptions.admin.serversocket.accept();
				DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
				logger.trace("Incoming connection from " + socket.getInetAddress());
				conn.bind(socket, this.params);

				// Start worker thread
				Thread t = new AdminWorkerThread(this.httpService, conn);
				t.setDaemon(true);
				t.start();
			}
			catch(InterruptedIOException ex)
			{
				break;
			}
			catch (IOException e)
			{
				logger.error("I/O error initialising connection thread: " + e.getMessage());
				break;
			}
		}
	}
}