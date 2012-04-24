package calico.controllers;

import calico.*;
import calico.clients.*;
import calico.components.*;
import calico.networking.*;
import calico.networking.netstuff.*;
import calico.utils.CalicoUtils;
import calico.uuid.*;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.*;

import org.apache.log4j.Logger;

import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.ints.*;

public class CCanvasController
{
	public static Logger logger = Logger.getLogger(CCanvasController.class.getName());
	
	public static Long2ReferenceArrayMap<CCanvas> canvases = new Long2ReferenceArrayMap<CCanvas>();
	private static Long2LongAVLTreeMap arrow_canvas = new Long2LongAVLTreeMap();
	private static Long2LongAVLTreeMap group_canvas = new Long2LongAVLTreeMap();
	private static Long2LongAVLTreeMap stroke_canvas = new Long2LongAVLTreeMap();
	private static Long2LongAVLTreeMap list_canvas = new Long2LongAVLTreeMap();
	
	public static void setup()
	{
		arrow_canvas.defaultReturnValue(0L);
		group_canvas.defaultReturnValue(0L);
		stroke_canvas.defaultReturnValue(0L);
	}
	
	public static void set_arrow_canvas(long uuid, long cuid)
	{
		arrow_canvas.put(uuid, cuid);
	}
	public static void set_stroke_canvas(long uuid, long cuid)
	{
		stroke_canvas.put(uuid, cuid);
	}
	public static void set_group_canvas(long uuid, long cuid)
	{
		group_canvas.put(uuid, cuid);
	}
	public static long get_group_canvas(long uuid)
	{
		return group_canvas.get(uuid);
	}
	public static long get_arrow_canvas(long uuid)
	{
		return arrow_canvas.get(uuid);
	}
	public static long get_stroke_canvas(long uuid)
	{
		return stroke_canvas.get(uuid);
	}
	public static long get_list_canvas(long uuid)
	{
		return list_canvas.get(uuid);
	}	
	
	
	
	public static void no_notify_start(long uuid)
	{
		
	}

	
	public static void no_notify_add_child_group(long uuid, long guuid)
	{
		canvases.get(uuid).addChildGroup(guuid);
	}
	
	public static void no_notify_remove_child_group(long uuid, long guuid)
	{
		canvases.get(uuid).deleteChildGroup(guuid);
	}
	
	
	public static void no_notify_remove_child_arrow(long uuid, long guuid)
	{
		canvases.get(uuid).deleteChildArrow(guuid);
	}
	public static void no_notify_add_child_arrow(long uuid, long guuid)
	{
		canvases.get(uuid).addChildArrow(guuid);
	}
	
	
	public static void no_notify_add_child_stroke(long uuid, long suuid)
	{
		canvases.get(uuid).addChildStroke(suuid);
	}
	
	public static void no_notify_remove_child_stroke(long uuid, long suuid)
	{
		canvases.get(uuid).deleteChildStroke(suuid);
	}
	
	public static void no_notify_add_child_list(long uuid, long luuid)
	{
		canvases.get(uuid).addChildList(luuid);
	}
	
	public static boolean undo(long uuid)
	{
		return CCanvasController.canvases.get(uuid).performUndo();
	}
	
	public static boolean redo(long uuid)
	{
		return CCanvasController.canvases.get(uuid).performRedo();
	}
	
	public static long get_smallest_containing_group_for_path(long canvas_uuid, GeneralPath path)
	{
		long[] uuids = canvases.get(canvas_uuid).getChildGroups();
		
		long group_uuid = 0L;
		double group_area = Double.MAX_VALUE;
		
		if(uuids.length>0)
		{
			for(int i=0;i<uuids.length;i++)
			{
				if( (CGroupController.groups.get(uuids[i]).getArea()< group_area) && CGroupController.group_contains_shape(uuids[i], path) )
				{
					group_area =CGroupController.groups.get(uuids[i]).getArea();
					group_uuid = uuids[i];
				}
			}
		}
		return group_uuid;
	}
	


	/**
	 * Takes a snapshot of the canvas
	 * @param uuid the UUID of the canvas
	 */
	public static void snapshot(long uuid)
	{
		canvases.get(uuid).saveCurrentCanvasState();
	}
	
	/**
	 * Takes a snapshot of the canvas that contains this stroke
	 * @param uuid The UUID of the Stroke that changed
	 */
	public static void snapshot_stroke(long uuid)
	{
		long cuid = get_stroke_canvas(uuid);
		
		if (!canvases.containsKey(cuid))
		{
			return;
		}
		canvases.get( cuid ).saveCurrentCanvasState();
	}
	
	/**
	 * Takes a snapshot of the canvas that contains the group
	 * @param uuid uuid of the group
	 */
	public static void snapshot_group(long uuid)
	{
		long cuid = get_group_canvas(uuid);
		
		if (!canvases.containsKey(cuid))
		{
			return;
		}
		canvases.get(  get_group_canvas(uuid)  ).saveCurrentCanvasState();
	}
	public static void snapshot_arrow(long uuid)
	{
		long cuid = get_arrow_canvas(uuid);
		
		if (!canvases.containsKey(cuid))
		{
			return;
		}
		canvases.get( get_arrow_canvas(uuid) ).saveCurrentCanvasState();
	}
	public static void snapshot_list(long uuid)
	{
		canvases.get( get_list_canvas(uuid) ).saveCurrentCanvasState();
	}


	public static void no_notify_clear(long uuid)
	{
		// CLEAR IT OUT
		canvases.get(uuid).resetLock();
		long[] groups = canvases.get(uuid).getChildGroups();
		long[] strokes = canvases.get(uuid).getChildStrokes();
		long[] arrows  = canvases.get(uuid).getChildArrows();
		
		if(strokes.length>0)
		{
			for(int i=0;i<strokes.length;i++)
			{
				CStrokeController.no_notify_delete(strokes[i]);
			}
		}
		
		if(arrows.length>0)
		{
			for(int i=0;i<arrows.length;i++)
			{
				CArrowController.no_notify_delete(arrows[i]);
			}
		}
		
		if(groups.length>0)
		{
			for(int i=0;i<groups.length;i++)
			{
				CGroupController.no_notify_delete(groups[i]);
			}
		}
	}
	
	
	
	public static void no_notify_clear_for_state_change(long uuid)
	{
		no_notify_clear(uuid);
	}
	public static void no_notify_state_change_complete(long uuid)
	{
		
	}
	
	public static void state_change_complete(long uuid)
	{	
		no_notify_state_change_complete(uuid);
		ClientManager.send(CalicoPacket.getPacket(NetworkCommand.CANVAS_SC_FINISH, uuid));
		
	}
	
	// for some damn reason, parenting checks are not working as they should, so this just rechecks parents on everything
	public static void recheck_everyones_parents_ffs(long cuid)
	{
		long[] guids = canvases.get(cuid).getChildGroups();
	}
	
	public static void copy_canvas(long cuidFrom, long cuidTo){
		logger.debug("CCanvasController.copy_canvas");
		long[] groups = canvases.get(cuidFrom).getChildGroups();
		long[] strokes = canvases.get(cuidFrom).getChildStrokes();
		long[] arrows  = canvases.get(cuidFrom).getChildArrows();
		Long2ReferenceArrayMap<Long> groupMappings = new Long2ReferenceArrayMap<Long>();
		
		if(groups.length>0)
		{			
			for(int i=0;i<groups.length;i++)
			{
				CGroup temp = CGroupController.groups.get(groups[i]);
				
				if(temp.getParentUUID()==0l && temp.isPermanent()){
					long new_uuid = UUIDAllocator.getUUID();
					groupMappings.put(groups[i], new Long(new_uuid));
					groupMappings.putAll(CGroupController.copy(groups[i], new_uuid, cuidTo, 0, 0,true));
				}
			}
		}
		if(strokes.length>0)
		{			
			for(int i=0;i<strokes.length;i++)
			{				
				CStroke temp = CStrokeController.strokes.get(strokes[i]);
				if(temp.getParentUUID()==0l){
					
					long new_uuid = UUIDAllocator.getUUID();
					CStrokeController.copy(temp.getUUID(), new_uuid, 0l, cuidTo, 0, 0, true);			
				}
			}
		}
		if(arrows.length>0)
		{			
			for(int i=0;i<arrows.length;i++)
			{	
				CArrow temp = CArrowController.arrows.get(arrows[i]);
				if(temp.getAnchorA().getUUID()==cuidFrom||temp.getAnchorB().getUUID()==cuidFrom||(temp.getAnchorA().getUUID()!=temp.getAnchorB().getUUID())){				
					long new_uuid = UUIDAllocator.getUUID();
					AnchorPoint anchorA = temp.getAnchorA().clone();
					AnchorPoint anchorB = temp.getAnchorB().clone();
					
					if(groupMappings.containsKey(temp.getAnchorA().getUUID())){				
						anchorA.setUUID(groupMappings.get(temp.getAnchorA().getUUID()).longValue());
					}else{
						anchorA.setUUID(cuidTo);
					}
					if(groupMappings.containsKey(temp.getAnchorB().getUUID())){				
						anchorB.setUUID(groupMappings.get(temp.getAnchorB().getUUID()).longValue());
					}else{
						anchorB.setUUID(cuidTo);
					}
					CArrowController.no_notify_start(new_uuid, cuidTo, temp.getArrowType(), temp.getArrowColor(),anchorA, anchorB);
					CArrowController.reload(new_uuid);
				}				
			}
		}		
	}

	public static void no_notify_lock_canvas(long canvas, boolean lock, String lockedBy, long time) {
		if (!exists(canvas)) { return; }
		
		canvases.get(canvas).setCanvasLock(lock, lockedBy, time);
	}
	
	public static boolean exists(long cuuid)
	{
		return canvases.containsKey(cuuid);
	}

	public static int getCanvasSignature(long canvas) {
		if (!exists(canvas))
			return 0;
		
		return canvases.get(canvas).getSignature();
	}
	
	public static CalicoPacket getCanvasConsistencyDebugPacket(long canvas) {
		if (!exists(canvas))
			return new CalicoPacket(0);
		
		return canvases.get(canvas).getConsistencyDebugPacket();
	}
	
	
}
