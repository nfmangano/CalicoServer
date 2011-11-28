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

public class CArrowController
{
	public static Long2ReferenceArrayMap<CArrow> arrows = new Long2ReferenceArrayMap<CArrow>();
	
	
	public static void setup()
	{
	
	}
	
	public static boolean exists(long uuid)
	{
		return arrows.containsKey(uuid);
	}
	
	public static void no_notify_start(long uuid, long cuid, int type, Color color, AnchorPoint pointa, AnchorPoint pointb)
	{
		CCanvasController.set_arrow_canvas(uuid, cuid);
		
		if (exists(uuid))
			no_notify_delete(uuid);
		// create the object
		arrows.put(uuid, new CArrow(uuid, cuid, type, color, pointa, pointb));

		// Add to the canvas
		CCanvasController.no_notify_add_child_arrow(cuid, uuid);
		
		if(pointa.getType()==CArrow.TYPE_GROUP)
		{
			CGroupController.no_notify_add_child_arrow(pointa.getUUID(), uuid);
		}
		
		if(pointb.getType()==CArrow.TYPE_GROUP)
		{
			CGroupController.no_notify_add_child_arrow(pointb.getUUID(), uuid);
		}
		
	}

	
	public static void no_notify_delete(long uuid)
	{
		if(!exists(uuid)){return;}
		
		// Clear the anchor A
		if(arrows.get(uuid).getAnchorA().getType()==CArrow.TYPE_GROUP)
		{
			CGroupController.no_notify_remove_child_arrow(arrows.get(uuid).getAnchorA().getUUID(), uuid);
		}
		
		// Clear anchor B
		if(arrows.get(uuid).getAnchorB().getType()==CArrow.TYPE_GROUP)
		{
			CGroupController.no_notify_remove_child_arrow(arrows.get(uuid).getAnchorB().getUUID(), uuid);
		}
		
		// remove from the canvas
		CCanvasController.no_notify_remove_child_arrow( CCanvasController.get_arrow_canvas(uuid), uuid);
		
		// Call the arrow delete, 
		arrows.get(uuid).delete();
		
		
		// remove from the DB
		arrows.remove( uuid );
	}


	public static void no_notify_move_group(long uuid, long groupuuid, int x, int y)
	{
		if(!exists(uuid)){return;}
		
		arrows.get(uuid).moveGroup(groupuuid, x, y);
	}

	
	public static void delete(long uuid)
	{
		if(!exists(uuid)){return;}
		
		no_notify_delete(uuid);
		
		// Notify
		ClientManager.send(CalicoPacket.getPacket( NetworkCommand.ARROW_DELETE, uuid ));
	}
	
	
	// THIS IS DIFFERENT FROM SET_PARENT ON GROUP/STROKES - It only changes the parent of the anchor node IF THE ANCHOR IS SET TO THE CANVAS
	public static void no_notify_parented_to_group(long uuid, long parent_uuid)
	{
		if(!exists(uuid)){return;}
		
		// TODO: Finish
	}
	
	public static void reload(final long uuid)
	{
		if(!exists(uuid)){return;}
		
		ClientManager.send(arrows.get(uuid).getUpdatePackets());
	}
	
	public static void no_notify_move_group_anchor(long uuid, long guuid, int x, int y)
	{
		arrows.get(uuid).moveGroup(guuid, x, y);
	}

	public static void recalculate_parent(long uuid) {
		CArrowController.arrows.get(uuid).calculateParent();
		reload(uuid);
	}
	
	public static int get_signature(long l) {
		if (!exists(l))
			return 0;
		
		return arrows.get(l).get_signature();
	}
	
}
