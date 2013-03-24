package calico.plugins.iip.controllers;

import it.unimi.dsi.fastutil.longs.Long2ReferenceArrayMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang.ArrayUtils;

import calico.plugins.iip.CCanvasLink;
import calico.plugins.iip.CCanvasLinkAnchor;
import calico.plugins.iip.CIntentionCell;
import calico.plugins.iip.CIntentionType;
import calico.plugins.iip.IntentionalInterfaceState;

public class CIntentionCellController
{
	public static CIntentionCellController getInstance()
	{
		return INSTANCE;
	}

	private static final CIntentionCellController INSTANCE = new CIntentionCellController();

	private static Long2ReferenceArrayMap<CIntentionType> activeIntentionTypes = new Long2ReferenceArrayMap<CIntentionType>();

	private static Long2ReferenceArrayMap<CIntentionCell> cells = new Long2ReferenceArrayMap<CIntentionCell>();
	private static Long2ReferenceArrayMap<CIntentionCell> cellsByCanvasId = new Long2ReferenceArrayMap<CIntentionCell>();

	public void populateState(IntentionalInterfaceState state)
	{
		for (CIntentionType type : activeIntentionTypes.values())
		{
			state.addCellPacket(type.getState());
		}
		
		for (CIntentionCell cell : cells.values())
		{
			cell.populateState(state);
		}
	}
	
	public void clearState()
	{
		activeIntentionTypes.clear();
		cells.clear();
		cellsByCanvasId.clear();
	}

	public Collection<CIntentionCell> getAllCells()
	{
		return cells.values();
	}

	public void addCell(CIntentionCell cell)
	{
		cells.put(cell.getId(), cell);
		cellsByCanvasId.put(cell.getCanvasId(), cell);
	}

	public CIntentionCell getCellById(long uuid)
	{
		return cells.get(uuid);
	}

	public CIntentionCell getCellByCanvasId(long canvas_uuid)
	{
		return cellsByCanvasId.get(canvas_uuid);
	}

	public void removeCellById(long uuid)
	{
		cells.remove(uuid);
	}

	public CIntentionType createIntentionType(long uuid, String name, int colorIndex, String description)
	{
		if (colorIndex < 0)
		{
			colorIndex = chooseColorIndex();
		}

		CIntentionType type = new CIntentionType(uuid, name, colorIndex, description);
		activeIntentionTypes.put(uuid, type);
		return type;
	}
	
	private int chooseColorIndex()
	{
		int freeColorIndex = 0;
		boolean[] used = new boolean[CIntentionType.AVAILABLE_COLOR_COUNT];
		Arrays.fill(used, false);
		for (CIntentionType type : activeIntentionTypes.values())
		{
			used[type.getColorIndex()] = true;
		}
		for (int i = 0; i < used.length; i++)
		{
			if (!used[i])
			{
				return i;
			}
		}
		
		System.out.println("Warning: no unused CIntentionType colors to choose from!");
		return 0;
	}

	public Collection<CIntentionType> getActiveIntentionTypes()
	{
		return activeIntentionTypes.values();
	}

	public void renameIntentionType(long typeId, String name)
	{
		activeIntentionTypes.get(typeId).setName(name);
	}

	public void setIntentionTypeColor(long typeId, int color)
	{
		activeIntentionTypes.get(typeId).setColorIndex(color);
	}

	public void removeIntentionType(long typeId)
	{
		activeIntentionTypes.remove(typeId);

		for (CIntentionCell cell : cells.values())
		{
			if (cell.hasIntentionType())
			{
				cell.clearIntentionType();
			}
		}
	}
	
	public long[] getCIntentionCellChildren(long memberCanvasId)
	{
		ArrayList<Long> childrenCanvases = new ArrayList<Long>();
		for (long anchorId : CCanvasLinkController.getInstance().getAnchorIdsForCanvasId(memberCanvasId))
		{
			CCanvasLinkAnchor anchor = CCanvasLinkController.getInstance().getAnchor(anchorId);
			CCanvasLink link = CCanvasLinkController.getInstance().getLink(anchor.getLinkId());
			if (link.getAnchorB().getId() == anchorId)
			{
				continue;
			}
			long linkedCanvasId = CCanvasLinkController.getInstance().getOpposite(anchorId).getCanvasId();

			if (linkedCanvasId < 0L)
			{
				continue; // this is not a canvas, nothing is here
			}
			
			childrenCanvases.add(new Long(linkedCanvasId));
		}
		
		return ArrayUtils.toPrimitive(childrenCanvases.toArray(new Long[0]));
	}
}
