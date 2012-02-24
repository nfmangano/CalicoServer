package calico.plugins.iip.controllers;

import it.unimi.dsi.fastutil.longs.Long2ReferenceArrayMap;
import calico.plugins.iip.CCanvasLink;
import calico.plugins.iip.IntentionalInterfaceState;

public class CCanvasLinkController
{
	public static CCanvasLinkController getInstance()
	{
		return INSTANCE;
	}
	
	private static final CCanvasLinkController INSTANCE = new CCanvasLinkController();
	
	private static Long2ReferenceArrayMap<CCanvasLink> links = new Long2ReferenceArrayMap<CCanvasLink>();

	public void populateState(IntentionalInterfaceState state)
	{
		for (CCanvasLink link : links.values())
		{
			state.addLinkPacket(link.getState());
		}
	}

	public void addLink(CCanvasLink link)
	{
		links.put(link.getId(), link);
	}
	
	public CCanvasLink getLinkById(long uuid)
	{
		return links.get(uuid);
	}

	public void removeLinkById(long uuid)
	{
		links.remove(uuid);
	}
}
