package calico.controllers;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import it.unimi.dsi.fastutil.longs.Long2ReferenceAVLTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import org.apache.log4j.Logger;

import calico.clients.ClientManager;
import calico.components.CConnector;
import calico.components.CStroke;
import calico.networking.netstuff.NetworkCommand;

public class CConnectorController {
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(CConnectorController.class.getName());
	
	/**
	 * This is the database of all the BGElements
	 */
	public static Long2ReferenceAVLTreeMap<CConnector> connectors = new Long2ReferenceAVLTreeMap<CConnector>();
	
	public static boolean exists(long uuid)
	{
		return connectors.containsKey(uuid);
	}
	
	private static long getConnectorCanvasUUID(long uuid)
	{
		return connectors.get(uuid).getCanvasUUID();
	}
	
	public static void create(long uuid, long cuid, Color color, float thickness, Point head, Point tail, double[] orthogonalDistance, 
			double[] travelDistance, long anchorHead, long anchorTail)
	{
		no_notify_create(uuid, cuid, color, thickness, head, tail, orthogonalDistance, travelDistance, anchorHead, anchorTail);
		
		ClientManager.send(connectors.get(uuid).getUpdatePackets()[0]);
	}
	
	/**
	 * This will create a Connector, but not notify anybody
	 * @param uuid
	 * @param cuid
	 * @param puid
	 * @param color
	 */
	public static void no_notify_create(long uuid, long cuid, Color color, float thickness, Point head, Point tail, double[] orthogonalDistance, 
			double[] travelDistance, long anchorHead, long anchorTail)
	{
		// does the element already exist?
		if(exists(uuid))
		{
			no_notify_delete(uuid);
		}
		// add to the DB
		connectors.put(uuid, new CConnector(uuid, cuid, color, thickness, head, tail, orthogonalDistance, travelDistance, anchorHead, anchorTail));
		
		// Add to the canvas
		CCanvasController.no_notify_add_child_connector(cuid, uuid);
		
		// We need to notify the groups 
		CGroupController.no_notify_add_child_connector(connectors.get(uuid).getAnchorUUID(CConnector.TYPE_HEAD), uuid);
		CGroupController.no_notify_add_child_connector(connectors.get(uuid).getAnchorUUID(CConnector.TYPE_TAIL), uuid);	
	}
	
	public static void no_notify_delete(long uuid)
	{
		if (!exists(uuid))
			return;
			
		connectors.get(uuid).delete();
		connectors.remove(uuid);
	}
	
	public static void no_notify_linearize(long uuid)
	{
		if (!exists(uuid))
			return;
		
		connectors.get(uuid).linearize();
	}
	
	public static void no_notify_move_group_anchor(long uuid, long guuid, int x, int y)
	{
		if (!exists(uuid))
			return;
		
		connectors.get(uuid).moveAnchor(guuid, x, y);
	}
	
	
	public static void delete(long uuid)
	{
		no_notify_delete(uuid);
		
		//Networking.send(NetworkCommand.CONNECTOR_DELETE, uuid);
		
	}
	
	public static int get_signature(long l) {
		if (!exists(l))
			return 0;
		
		return connectors.get(l).get_signature();
	}

}
