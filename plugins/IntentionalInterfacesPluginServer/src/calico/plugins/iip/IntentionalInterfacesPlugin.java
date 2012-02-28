package calico.plugins.iip;

import calico.clients.Client;
import calico.clients.ClientManager;
import calico.components.CCanvas;
import calico.controllers.CCanvasController;
import calico.events.CalicoEventHandler;
import calico.events.CalicoEventListener;
import calico.networking.netstuff.CalicoPacket;
import calico.plugins.AbstractCalicoPlugin;
import calico.plugins.CalicoPluginManager;
import calico.plugins.CalicoStateElement;
import calico.plugins.iip.controllers.CCanvasLinkController;
import calico.plugins.iip.controllers.CIntentionCellController;
import calico.uuid.UUIDAllocator;

public class IntentionalInterfacesPlugin extends AbstractCalicoPlugin implements CalicoEventListener, CalicoStateElement
{
	private final IntentionalInterfaceState state = new IntentionalInterfaceState();

	public IntentionalInterfacesPlugin()
	{
		PluginInfo.name = "Intentional Interfaces";
	}

	public void onPluginStart()
	{
		// register for palette events
		for (Integer event : this.getNetworkCommands())
		{
			System.out.println("IntentionalInterfacesPlugin: attempting to listen for " + event.intValue());
			CalicoEventHandler.getInstance().addListener(event.intValue(), this, CalicoEventHandler.ACTION_PERFORMER_LISTENER);
			// LongSet canvasKeys = CCanvasController.canvases.keySet();
			// for (Long canvaskey : canvasKeys)
			// {
			// CalicoEventHandler.getInstance().addListener(event.intValue(),
			// CCanvasController.canvases.get(canvaskey.longValue()),
			// CalicoEventHandler.PASSIVE_LISTENER);
			// }
		}
		 
		for (CCanvas canvas : CCanvasController.canvases.values())
		{ 
			int x = canvas.getGridX() * 100;
			int y = canvas.getGridY() * 100;
			CIntentionCell cell = new CIntentionCell(UUIDAllocator.getUUID(), canvas.getUUID(), false, x, y);
			CIntentionCellController.getInstance().addCell(cell);
		}

		CalicoPluginManager.registerCalicoStateExtension(this);
	}

	@Override
	public void handleCalicoEvent(int event, CalicoPacket p, Client c)
	{
		switch (IntentionalInterfacesNetworkCommands.Command.forId(event))
		{
			case CIC_CREATE:
				CIC_CREATE(p, c);
				break;
			case CIC_MOVE:
				CIC_MOVE(p, c);
				break;
			case CIC_DELETE:
				CIC_DELETE(p, c);
				break;
			case CLINK_CREATE:
				CLINK_CREATE(p, c);
				break;
			case CLINK_RETYPE:
				CLINK_RETYPE(p, c);
				break;
			case CLINK_MOVE_ANCHOR:
				CLINK_MOVE_ANCHOR(p, c);
				break;
			case CLINK_DELETE:
				CLINK_DELETE(p, c);
				break;
		}
	}

	private static void CIC_CREATE(CalicoPacket p, Client c)
	{
		p.rewind();
		IntentionalInterfacesNetworkCommands.Command.CIC_CREATE.verify(p);

		long uuid = p.getLong();
		long canvas_uuid = p.getLong();
		boolean inUse = p.getBoolean();
		int x = p.getInt();
		int y = p.getInt();

		CIntentionCell cell = new CIntentionCell(uuid, canvas_uuid, inUse, x, y);
		CIntentionCellController.getInstance().addCell(cell);

		if (c != null)
		{
			ClientManager.send_except(c, p);
		}
	}

	private static void CIC_MOVE(CalicoPacket p, Client c)
	{
		p.rewind();
		IntentionalInterfacesNetworkCommands.Command.CIC_MOVE.verify(p);

		long uuid = p.getLong();
		CIntentionCell cell = CIntentionCellController.getInstance().getCellById(uuid);

		cell.setInUse(p.getBoolean());
		
		int x = p.getInt();
		int y = p.getInt();
		cell.setLocation(x, y);

		if (c != null)
		{
			ClientManager.send_except(c, p);
		}
	}

	private static void CIC_DELETE(CalicoPacket p, Client c)
	{
		p.rewind();
		IntentionalInterfacesNetworkCommands.Command.CIC_DELETE.verify(p);

		long uuid = p.getLong();
		CIntentionCellController.getInstance().removeCellById(uuid);

		if (c != null)
		{
			ClientManager.send_except(c, p);
		}
	}

	private static CCanvasLinkAnchor unpackAnchor(CalicoPacket p)
	{
		long uuid = p.getLong();
		long canvas_uuid = p.getLong();
		CCanvasLinkAnchor.Type type = CCanvasLinkAnchor.Type.values()[p.getInt()];
		int x = p.getInt();
		int y = p.getInt();
		long group_uuid = p.getLong();
		return new CCanvasLinkAnchor(uuid, canvas_uuid, type, x, y, group_uuid);
	}

	private static void CLINK_CREATE(CalicoPacket p, Client c)
	{
		p.rewind();
		IntentionalInterfacesNetworkCommands.Command.CLINK_CREATE.verify(p);

		long uuid = p.getLong();
		CCanvasLink.LinkType type = CCanvasLink.LinkType.values()[p.getInt()];
		CCanvasLinkAnchor anchorA = unpackAnchor(p);
		CCanvasLinkAnchor anchorB = unpackAnchor(p);
		CCanvasLink link = new CCanvasLink(uuid, type, anchorA, anchorB);
		CCanvasLinkController.getInstance().addLink(link);

		if (c != null)
		{
			ClientManager.send_except(c, p);
		}
	}

	private static void CLINK_RETYPE(CalicoPacket p, Client c)
	{
		p.rewind();
		IntentionalInterfacesNetworkCommands.Command.CLINK_RETYPE.verify(p);

		long uuid = p.getLong();
		CCanvasLink link = CCanvasLinkController.getInstance().getLinkById(uuid);

		CCanvasLink.LinkType type = CCanvasLink.LinkType.values()[p.getInt()];
		link.setLinkType(type);

		if (c != null)
		{
			ClientManager.send_except(c, p);
		}
	}

	private static void CLINK_MOVE_ANCHOR(CalicoPacket p, Client c)
	{
		p.rewind();
		IntentionalInterfacesNetworkCommands.Command.CLINK_MOVE_ANCHOR.verify(p);

		long anchor_uuid = p.getLong();
		long canvas_uuid = p.getLong();
		CCanvasLinkAnchor.Type type = CCanvasLinkAnchor.Type.values()[p.getInt()];
		int x = p.getInt();
		int y = p.getInt();

		CCanvasLinkController.getInstance().moveLinkAnchor(anchor_uuid, canvas_uuid, type, x, y);
				
		if (c != null)
		{
			ClientManager.send_except(c, p);
		}
	}

	private static void CLINK_DELETE(CalicoPacket p, Client c)
	{
		p.rewind();
		IntentionalInterfacesNetworkCommands.Command.CLINK_DELETE.verify(p);

		long uuid = p.getLong();
		CCanvasLinkController.getInstance().removeLinkById(uuid);

		if (c != null)
		{
			ClientManager.send_except(c, p);
		}
	}

	@Override
	public CalicoPacket[] getCalicoStateElementUpdatePackets()
	{
		state.reset();
		CIntentionCellController.getInstance().populateState(state);
		CCanvasLinkController.getInstance().populateState(state);

		return state.getAllPackets();
	}

	public Class<?> getNetworkCommandsClass()
	{
		return IntentionalInterfacesNetworkCommands.class;
	}
}