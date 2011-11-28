package calico.sessions;
import java.util.*;
import java.net.*;
import java.io.*;

import calico.networking.*;
import calico.admin.*;
import calico.clients.*;
import calico.uuid.*;
import calico.components.*;
import calico.networking.netstuff.*;


import it.unimi.dsi.fastutil.objects.*;

public class SessionInfo
{
	public int sessionid = 0;
	public String name = "";
	public int rows = 0;
	public int cols = 0;
	
	
	/**
	 * Creates a session Info class, this just has info on the session, no more
	 * @param id
	 * @param n
	 * @param r
	 * @param c
	 */
	public SessionInfo(int id, String n, int r, int c)
	{
		sessionid = id;
		name = n;
		rows = r;
		cols = c;
	}
	
	/**
	 * Returns the session id
	 * @return
	 */
	public int getSessionID()
	{
		return sessionid;
	}
	
	public int getRows()
	{
		return rows;
	}
	public int getCols()
	{
		return cols;
	}
	public String getName()
	{
		return name;
	}

	
}
