package calico.sessions;
import java.util.*;
import java.net.*;
import java.io.*;
import calico.networking.netstuff.*;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import calico.COptions;
import calico.networking.*;
import calico.admin.*;
import calico.uuid.UUIDAllocator;
import calico.clients.*;
import calico.components.*;
import calico.controllers.CArrowController;
import calico.controllers.CCanvasController;
import calico.controllers.CGroupController;
import calico.controllers.CStrokeController;


import it.unimi.dsi.fastutil.objects.*;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.longs.*;


import org.apache.log4j.*;

public class Session
{
	private String name = "";
	private int rows = 0;
	private int cols = 0;
	private int sessionid = 0;

	// list of connected clients
	private IntArrayList clientlist = new IntArrayList();
	
	// List of canvases in the session
	private LongArrayList canvasList = new LongArrayList();
		
	private Logger logger = null;


	@Deprecated
	public String toString()
	{
		return name+"<"+sessionid+"><"+rows+"><"+cols+">";
	}

	/**
	 * This creates a new session
	 * @param sid sessionid
	 * @param n Session name
	 * @param r number of rows
	 * @param c number of columns
	 * @deprecated
	 */
	public Session(int sid, String n, int r, int c)
	{
		sessionid = sid;
		name = n;
		rows = r;
		cols = c;
		//int total = r*c;
		
		for(int i=0;i<rows;i++)
		{
			for(int y=0;y<cols;y++)
			{
				// Make the canvas
				CCanvas can = new CCanvas(UUIDAllocator.getUUID(), sid);
//				can.setGridPos(i, y);
				
				// add it to the canvas list
				canvasList.add(can.getUUID());
				
				// Add to the main list
				CCanvasController.canvases.put(can.getUUID(), can);
			}
		}
				
		// Logger
		// Setup the Logger
		logger = Logger.getLogger("session."+name);
		logger.removeAllAppenders();
		logger.setLevel(Level.DEBUG);
		try
		{
			logger.addAppender( new DailyRollingFileAppender( new PatternLayout(COptions.LOG_FORMAT_STD), COptions.log_path+"session."+name+".log", COptions.DATEFILE_FMT) );
			//log.addAppender( new ConsoleAppender( new PatternLayout(COptions.LOG_FORMAT_STD) ) );
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		logger.info("Created session \""+toString()+"\"");

	}

	@Deprecated
	public int[] getClientIDList()
	{
		return clientlist.toIntArray();
	}

	/**
	 * Add the client to the server, and send them an update.
	 * @param clientid
	 * @deprecated
	 */
	public void joinClient(int clientid)
	{
		if(clientlist.contains(clientid))
		{
			return;
		}
		
		
		
		clientlist.add(clientid);
		
		logger.info("\""+ClientManager.client2string(clientid)+"\" has joined \""+toString()+"\"");
			
		// Send the welcome message
		//CalicoPacket resp = new CalicoPacket( NetworkCommand.STATUS_MESSAGE );
		//resp.putString("You have successfully joined the "+name+" session!");
		//ClientManager.send(clientid,resp);

		// Notify the other users that a noob has joined
		CalicoPacket resp2 = new CalicoPacket( NetworkCommand.STATUS_MESSAGE);
		//resp2.putString(ClientManager.getUsername(clientid)+" has joined the session!");
		
		for(int i=0;i<clientlist.size();i++)
		{
			int tclient = clientlist.getInt(i);
			if(tclient!=clientid)
			{
				ClientManager.send(tclient,resp2);
			}
		}
		
	}
	
	/**
	 * Drop out
	 * @param clientid
	 * @deprecated
	 */
	public void dropClient(int clientid)
	{
		if(clientlist.contains(clientid))
		{
			clientlist.rem(clientid);
		}
	}


	@Deprecated
	public String getName()
	{
		return name;
	}
	@Deprecated
	public int getRows()
	{
		return rows;
	}
	@Deprecated
	public int getCols()
	{
		return cols;
	}
	
	/**
	 * This sends the list of canvases to the client.
	 * THIS IS ONLY DONE AT THE START OF THE SESSION
	 * @param c
	 * @deprecated
	 */
	public void sendCanvasList(Client c)
	{
		for(int i=0;i<canvasList.size();i++)
		{
			ClientManager.send(c,CCanvasController.canvases.get(canvasList.get(i)).getInfoPacket());
		}
		//
	}
	


	@Deprecated
	public SessionInfo getSessionInfo()
	{
		return new SessionInfo( sessionid, name, rows, cols );
	}
	

}
