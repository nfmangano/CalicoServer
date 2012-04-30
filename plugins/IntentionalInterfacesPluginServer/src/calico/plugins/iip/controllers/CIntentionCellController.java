package calico.plugins.iip.controllers;

import it.unimi.dsi.fastutil.longs.Long2ReferenceArrayMap;

import java.util.Arrays;
import java.util.Collection;

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
		for (CIntentionCell cell : cells.values())
		{
			cell.populateState(state);
		}
		
		for (CIntentionType type : activeIntentionTypes.values())
		{
			state.addCellPacket(type.getState());
		}
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
	
	public CIntentionType createIntentionType(long uuid, String name)
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
				freeColorIndex = i;
				break;
			}
		}
		
		CIntentionType type = new CIntentionType(uuid, name, freeColorIndex);
		activeIntentionTypes.put(uuid, type);
		return type;
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
			cell.removeIntentionType(typeId);
		}
	}
}
