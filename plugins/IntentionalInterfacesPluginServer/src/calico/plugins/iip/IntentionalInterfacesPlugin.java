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
		}

		// create the intention cells
		for (CCanvas canvas : CCanvasController.canvases.values())
		{
			CIntentionCell cell = new CIntentionCell(UUIDAllocator.getUUID(), canvas, false);
			CIntentionCellController.getInstance().addCell(cell);
		}

		// create the intention types
		CIntentionCellController.getInstance().createIntentionType(UUIDAllocator.getUUID(), "New Perspective");
		CIntentionCellController.getInstance().createIntentionType(UUIDAllocator.getUUID(), "New Alternative");
		CIntentionCellController.getInstance().createIntentionType(UUIDAllocator.getUUID(), "New Idea");
		CIntentionCellController.getInstance().createIntentionType(UUIDAllocator.getUUID(), "Design Inside");

		CalicoPluginManager.registerCalicoStateExtension(this);
	}

	@Override
	public void handleCalicoEvent(int event, CalicoPacket p, Client c)
	{
		switch (IntentionalInterfacesNetworkCommands.Command.forId(event))
		{
			case CIC_CREATE:
				throw new UnsupportedOperationException(
						"A client has attempted to dynamically construct a CIntentionCell. The current policy only allows the server to create CIC's on IIP plugin init.");
			case CIC_MOVE:
				CIC_MOVE(p, c);
				break;
			case CIC_SET_TITLE:
				CIC_SET_TITLE(p, c);
				break;
			case CIC_TAG:
				CIC_TAG(p, c);
				break;
			case CIC_UNTAG:
				CIC_UNTAG(p, c);
				break;
			case CIC_DELETE:
				CIC_DELETE(p, c);
				break;
			case CIT_CREATE:
				CIT_CREATE(p, c);
				break;
			case CIT_RENAME:
				CIT_RENAME(p, c);
				break;
			case CIT_SET_COLOR:
				CIT_SET_COLOR(p, c);
				break;
			case CIT_DELETE:
				CIT_DELETE(p, c);
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
			case CLINK_LABEL:
				CLINK_LABEL(p, c);
				break;
			case CLINK_DELETE:
				CLINK_DELETE(p, c);
				break;
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

	private static void CIC_SET_TITLE(CalicoPacket p, Client c)
	{
		p.rewind();
		IntentionalInterfacesNetworkCommands.Command.CIC_SET_TITLE.verify(p);

		long uuid = p.getLong();
		CIntentionCell cell = CIntentionCellController.getInstance().getCellById(uuid);

		cell.setTitle(p.getString());

		if (c != null)
		{
			ClientManager.send_except(c, p);
		}
	}

	private static void CIC_TAG(CalicoPacket p, Client c)
	{
		p.rewind();
		IntentionalInterfacesNetworkCommands.Command.CIC_TAG.verify(p);

		long uuid = p.getLong();
		long typeId = p.getLong();

		CIntentionCell cell = CIntentionCellController.getInstance().getCellById(uuid);
		cell.addIntentionType(typeId);

		if (c != null)
		{
			ClientManager.send_except(c, p);
		}
	}

	private static void CIC_UNTAG(CalicoPacket p, Client c)
	{
		p.rewind();
		IntentionalInterfacesNetworkCommands.Command.CIC_UNTAG.verify(p);

		long uuid = p.getLong();
		long typeId = p.getLong();

		CIntentionCell cell = CIntentionCellController.getInstance().getCellById(uuid);
		cell.removeIntentionType(typeId);

		ClientManager.send_except(c, p);
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

	private static void CIT_CREATE(CalicoPacket p, Client c)
	{
		p.rewind();
		IntentionalInterfacesNetworkCommands.Command.CIC_CREATE.verify(p);

		long uuid = p.getLong();
		String name = p.getString();

		CIntentionType type = CIntentionCellController.getInstance().createIntentionType(uuid, name);

		p.putInt(type.getColorIndex());

		ClientManager.send(p);
	}

	private static void CIT_RENAME(CalicoPacket p, Client c)
	{
		p.rewind();
		IntentionalInterfacesNetworkCommands.Command.CIT_RENAME.verify(p);

		long uuid = p.getLong();
		String name = p.getString();
		CIntentionCellController.getInstance().renameIntentionType(uuid, name);

		if (c != null)
		{
			ClientManager.send_except(c, p);
		}
	}

	private static void CIT_SET_COLOR(CalicoPacket p, Client c)
	{
		p.rewind();
		IntentionalInterfacesNetworkCommands.Command.CIT_SET_COLOR.verify(p);

		long uuid = p.getLong();
		int color = p.getInt();
		CIntentionCellController.getInstance().setIntentionTypeColor(uuid, color);

		if (c != null)
		{
			ClientManager.send_except(c, p);
		}
	}

	private static void CIT_DELETE(CalicoPacket p, Client c)
	{
		p.rewind();
		IntentionalInterfacesNetworkCommands.Command.CIC_DELETE.verify(p);

		long uuid = p.getLong();

		CIntentionCellController.getInstance().removeIntentionType(uuid);

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

	private static void CLINK_LABEL(CalicoPacket p, Client c)
	{
		p.rewind();
		IntentionalInterfacesNetworkCommands.Command.CLINK_LABEL.verify(p);

		long uuid = p.getLong();
		CCanvasLink link = CCanvasLinkController.getInstance().getLinkById(uuid);
		link.setLabel(p.getString());

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