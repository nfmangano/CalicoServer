package calico.plugins.iip.graph.layout;

import java.util.ArrayList;
import java.util.List;

class CIntentionRing
{
	private final int index;
	private final List<Long> canvasIds = new ArrayList<Long>();
	
	public CIntentionRing(int index)
	{
		this.index = index;
	}
	
	public int getIndex()
	{
		return index;
	}

	void clear()
	{
		canvasIds.clear();
	}
	
	void addCanvas(long canvasId)
	{
		canvasIds.add(canvasId);
	}
	
	int size()
	{
		return canvasIds.size();
	}
}
