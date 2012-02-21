package calico.plugins.iip.controllers;

import it.unimi.dsi.fastutil.longs.Long2ReferenceArrayMap;
import calico.plugins.iip.CCanvasLink;
import calico.plugins.iip.CCanvasLinkAnchor;
import calico.plugins.iip.IntentionalInterfaceState;

public class CCanvasLinkController
{
	public static CCanvasLinkController getInstance()
	{
		return INSTANCE;
	}
	
	private static final CCanvasLinkController INSTANCE = new CCanvasLinkController();
	
	private static Long2ReferenceArrayMap<CCanvasLink> links = new Long2ReferenceArrayMap<CCanvasLink>();
	private static Long2ReferenceArrayMap<CCanvasLinkAnchor> linkAnchors = new Long2ReferenceArrayMap<CCanvasLinkAnchor>();

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
		
		addLinkAnchor(link.getAnchorA());
		addLinkAnchor(link.getAnchorB());
	}
	
	private void addLinkAnchor(CCanvasLinkAnchor anchor)
	{
		linkAnchors.put(anchor.getId(), anchor);
	}
	
	public CCanvasLink getLinkById(long uuid)
	{
		return links.get(uuid);
	}

	public void removeLinkById(long uuid)
	{
		CCanvasLink link = links.remove(uuid);
		linkAnchors.remove(link.getAnchorA().getId());
		linkAnchors.remove(link.getAnchorB().getId());
	}
	
	public void moveLinkAnchor(long anchor_uuid, long canvas_uuid, int x, int y)
	{
		CCanvasLinkAnchor anchor = linkAnchors.get(anchor_uuid);
		anchor.move(canvas_uuid, x, y);
	}
}
