package calico.plugins.googletalk;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.*;


import calico.controllers.CCanvasController;
import calico.controllers.CGroupController;
import calico.uuid.UUIDAllocator;

public class GTalkPacketListener implements PacketListener
{
	private static Logger logger = Logger.getLogger(CGroupController.class.getName());
	
	private String POST_TEXT = "cp:";
	
	private GoogleTalkPlugin parent = null;
	
	public GTalkPacketListener(GoogleTalkPlugin parent)
	{
		this.parent = parent;
	}
	
	public void processPacket(Packet p)
	{
		String message = ((Message)p).getBody();
		if (message != null && message.contains(POST_TEXT))
		{
			String groupText = message.substring(message.indexOf(POST_TEXT) + POST_TEXT.length());
			long uuid = UUIDAllocator.getUUID();
			long cuid = getCanvasUUID();
			long puid = 0L;
			CGroupController.start(uuid, cuid, puid, true);
			CGroupController.append(uuid, 300, 300);
			CGroupController.set_text(uuid, groupText);
			CGroupController.finish(uuid, false);
			
		}
		
//		parent.debug(p.getFrom() + ": " + p.toString());
//		if (p instanceof Message)
//		{
//			Message msg = (Message) p;
//			parent.debug(msg.getFrom() + ": " + msg.getBody());
//			if(msg.getBody()!=null)
//			{
//				this.parent.sendMessage(msg.getFrom(), "You said: \""+msg.getBody()+"\"");
//			}
//		}
	}
	
	private long getCanvasUUID() 
	{
		long[] uuids = CCanvasController.canvases.keySet().toLongArray();
		
		
		return uuids[0];
		
//		builder.append("UUID\tCoordinate\n");
//		for(int i=0;i<uuids.length;i++)
//		{
//			builder.append(uuids[i]+"\t"+CCanvasController.canvases.get(uuids[i]).getCoordText()+"\n");
//		}
	}
}
