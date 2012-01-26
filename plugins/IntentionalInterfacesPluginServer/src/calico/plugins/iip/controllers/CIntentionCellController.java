package calico.plugins.iip.controllers;

import it.unimi.dsi.fastutil.longs.Long2ReferenceArrayMap;
import calico.plugins.iip.CIntentionCell;
import calico.plugins.iip.IntentionalInterfaceState;

public class CIntentionCellController
{
	public static CIntentionCellController getInstance()
	{
		return INSTANCE;
	}
	
	private static final CIntentionCellController INSTANCE = new CIntentionCellController();
	
	private static Long2ReferenceArrayMap<CIntentionCell> cells = new Long2ReferenceArrayMap<CIntentionCell>();
	private static Long2ReferenceArrayMap<CIntentionCell> cellsByCanvasId = new Long2ReferenceArrayMap<CIntentionCell>();

	public void populateState(IntentionalInterfaceState state)
	{
		for (CIntentionCell cell : cells.values())
		{
			state.addCellPacket(cell.getState());
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
}
