
package calico;

import calico.networking.*;
import calico.networking.netstuff.*;
import calico.plugins.*;
import calico.plugins.events.*;
import calico.plugins.events.scraps.ScrapCreate;
import calico.plugins.googletalk.GoogleTalkPlugin;
import calico.admin.*;
import calico.sessions.SessionManager;
import calico.clients.*;
import calico.components.CCanvas;
import calico.controllers.*;
import calico.events.CalicoEventHandler;
import calico.utils.CalicoBackupHandler;
import calico.utils.CalicoUtils;
import calico.utils.Ticker;
import calico.uuid.*;


import java.awt.Color;
import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.channels.*;
import java.nio.channels.FileChannel.MapMode;



import java.util.concurrent.*;

import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.Selectors;
import org.apache.log4j.*;

import it.unimi.dsi.fastutil.ints.Int2ReferenceAVLTreeMap;
import it.unimi.dsi.fastutil.longs.Long2ReferenceAVLTreeMap;
import it.unimi.dsi.fastutil.objects.*;



/*
* Needs to handle all the clients
* maintain who is the master server
* Manage heartbeats
*/


public class CalicoServer
{
	public static int sentPackets = 0;
	
	public static Logger logger = Logger.getLogger(CalicoServer.class.getName());

	public static InetAddress listenHost;

	public static Object2ReferenceOpenHashMap<Client,ClientThread> clientThreads = new Object2ReferenceOpenHashMap<Client,ClientThread>();
	public static Long2ReferenceAVLTreeMap<CanvasThread> canvasThreads = new Long2ReferenceAVLTreeMap<CanvasThread>();
	public static Int2ReferenceAVLTreeMap<Object> canvasCommands = CanvasThread.getCanvasCommands();
	
	public static String[] args = null;


	public static void main(String[] args)
	{
		CalicoServer.args = args;
		PropertyConfigurator.configure(System.getProperty("log4j.configuration","log4j.properties"));
		
		CalicoConfig.setup();
		CalicoEventHandler.getInstance();
		
		COptions.ServerStartTime = System.currentTimeMillis();
		
		try
		{
			// Check for a backup that already exists.
			//FileObject backupFile = COptions.fsManager.resolveFile(COptions.fsCWD, COptions.server.backup.backup_file);
			FileObject backupFile = COptions.fs.resolveFile(COptions.server.backup.backup_file);
			if(backupFile.exists())
			{
				FileObject backupFileBkup = COptions.fs.resolveFile(COptions.server.backup.backup_file+".bkup");
				backupFileBkup.copyFrom(backupFile, Selectors.SELECT_ALL);
				logger.warn("Backup file: "+COptions.server.backup.backup_file+" already exists, renaming to "+COptions.server.backup.backup_file+".bkup");
				backupFileBkup.close();
			}
			backupFile.close();
		}
		catch(FileSystemException fse)
		{
			fse.printStackTrace();
		}

		// Setup the UUID Allocator
		UUIDAllocator.setup();

		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run()
			{
				CalicoServer.logger.info("Shutting down server...");
				
				CalicoPluginManager.shutdownPlugins();
				
				COptions.fs.close();
				
				//Thread.sleep(1000L);
			}
		});
		
		
		

		NetworkCommand.getFormat(0);
		ProcessQueue.setup();
		ClientManager.setup();
		
		// run the setups
		CArrowController.setup();
		CCanvasController.setup();
		CStrokeController.setup();
		CGroupController.setup();
		CSessionController.setup();
		
		
		logger.info(Runtime.getRuntime().availableProcessors()+" available CPUs");
		logger.info(Runtime.getRuntime().freeMemory()+"/"+Runtime.getRuntime().totalMemory()+" ("+CalicoUtils.printByteSize(Runtime.getRuntime().maxMemory())+") memory");
		logger.info("Starting Calico3 Server...");
		try
		{
			listenHost = InetAddress.getByName(COptions.listen.host);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		
		logger.info("Listening for connections on "+COptions.listen.host+":"+COptions.listen.port);


		ServerSocket sock = null;
		boolean listening = true;

		
		try
		{
			Thread t = new AdminRequestListenerThread();
	        t.setDaemon(false);
	        t.start();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		

		Ticker pe = new Ticker();
		pe.start();
		
		Thread udprecv = new Thread(new UDPReceiveQueue());
		udprecv.start();

		for(int i=0;i<COptions.GridRows;i++)
		{	  	
			for(int y=0;y<COptions.GridCols;y++)
			{
				// Make the canvas
				CCanvas can = new CCanvas(UUIDAllocator.getUUID());
				can.setGridPos(i, y);
				// Add to the main list
				CCanvasController.canvases.put(can.getUUID(), can);
			
			}
		}
//		CCanvas initialCanvas = new CCanvas(UUIDAllocator.getUUID());
//		CCanvasController.canvases.put(initialCanvas.getUUID(), initialCanvas);

		CalicoPluginManager.setup();
		
		
		try
		{
			sock = new ServerSocket(COptions.listen.port,50,listenHost);
//			sock.setSoTimeout(60000);

			logger.info("Opening socket");
			while(listening)
			{
				ClientManager.newClientThread( sock.accept() );
				//new ClientThread( sock.accept() ).start();
			}

			sock.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}//main

	
	
	
	
}
