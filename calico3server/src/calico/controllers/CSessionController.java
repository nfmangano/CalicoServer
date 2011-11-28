package calico.controllers;

import calico.*;
import calico.clients.*;
import calico.components.*;
import calico.networking.*;
import calico.networking.netstuff.*;
import calico.uuid.*;

import java.awt.*;
import java.util.*;

import it.unimi.dsi.fastutil.longs.*;

public class CSessionController
{
	public static ArrayList<CSession> sessions = new ArrayList<CSession>();
	
	
	public static void setup()
	{
		sessions.clear();
		sessions.add(new CSession("default", COptions.listen.host, COptions.listen.port));
	}
	
	public static void sendSessionList() {
		
		for(int i=0;i<sessions.size();i++) {
			CSession session = sessions.get(i);
			ClientManager.send(CalicoPacket.getPacket(NetworkCommand.SESSION_INFO, (i+1), session.getName(), session.getHost(), session.getPort() ));
		}
		
		
	}
	
	
}
