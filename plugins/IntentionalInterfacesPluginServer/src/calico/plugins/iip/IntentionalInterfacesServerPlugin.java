package calico.plugins.iip;

import java.util.Set;

import calico.clients.Client;
import calico.clients.ClientManager;
import calico.controllers.CCanvasController;
import calico.events.CalicoEventHandler;
import calico.events.CalicoEventListener;
import calico.networking.netstuff.CalicoPacket;
import calico.networking.netstuff.NetworkCommand;
import calico.plugins.AbstractCalicoPlugin;
import calico.plugins.CalicoPluginManager;
import calico.plugins.CalicoStateElement;
import calico.plugins.iip.controllers.CCanvasLinkController;
import calico.plugins.iip.controllers.CIntentionCellController;
import calico.plugins.iip.graph.layout.CIntentionLayout;
import calico.uuid.UUIDAllocator;

public class IntentionalInterfacesServerPlugin extends AbstractCalicoPlugin implements CalicoEventListener, CalicoStateElement
{
	private final IntentionalInterfaceState state = new IntentionalInterfaceState();

	public IntentionalInterfacesServerPlugin()
	{
		PluginInfo.name = "Intentional Interfaces";
	}

	public void onPluginStart()
	{
		CalicoEventHandler.getInstance().addListener(NetworkCommand.CANVAS_CLEAR, this, CalicoEventHandler.PASSIVE_LISTENER);
		CalicoEventHandler.getInstance().addListener(NetworkCommand.CANVAS_CREATE, this, CalicoEventHandler.PASSIVE_LISTENER);
		CalicoEventHandler.getInstance().addListener(NetworkCommand.CANVAS_DELETE, this, CalicoEventHandler.PASSIVE_LISTENER);

		for (Integer event : this.getNetworkCommands())
		{
			System.out.println("IntentionalInterfacesPlugin: attempting to listen for " + event.intValue());
			CalicoEventHandler.getInstance().addListener(event.intValue(), this, CalicoEventHandler.ACTION_PERFORMER_LISTENER);
		}

		// create the default intention types
		CIntentionCellController.getInstance().createIntentionType(UUIDAllocator.getUUID(), "New Perspective");
		CIntentionCellController.getInstance().createIntentionType(UUIDAllocator.getUUID(), "New Alternative");
		CIntentionCellController.getInstance().createIntentionType(UUIDAllocator.getUUID(), "New Idea");
		CIntentionCellController.getInstance().createIntentionType(UUIDAllocator.getUUID(), "Design Inside");

		CalicoPluginManager.registerCalicoStateExtension(this);

		for (long canvasId : CCanvasController.canvases.keySet())
		{
			createIntentionCell(canvasId);
		}
	}

	@Override
	public void handleCalicoEvent(int event, CalicoPacket p, Client c)
	{
		if (IntentionalInterfacesNetworkCommands.Command.isInDomain(event))
		{
			switch (IntentionalInterfacesNetworkCommands.Command.forId(event))
			{
				case CIC_CREATE:
					CIC_CREATE(p, c);
					break;
				case CIC_MOVE:
					CIC_MOVE(p, c);
					break;
				case CIC_SET_TITLE:
					CIC_SET_TITLE(p, c, true);
					break;
				case CIC_TAG:
					CIC_TAG(p, c);
					break;
				case CIC_UNTAG:
					CIC_UNTAG(p, c, true);
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
				case CLINK_MOVE_ANCHOR:
					CLINK_MOVE_ANCHOR(p, c);
					break;
				case CLINK_LABEL:
					CLINK_LABEL(p, c);
					break;
				case CLINK_DELETE:
					CLINK_DELETE(p, c, true);
					break;
			}
		}
		else
		{
			p.rewind();
			p.getInt();
			long canvasId = p.getLong();

			switch (event)
			{
				case NetworkCommand.CANVAS_CLEAR:
					CANVAS_CLEAR(p, c);
					break;
				case NetworkCommand.CANVAS_CREATE:
					createIntentionCell(canvasId);
					break;
				case NetworkCommand.CANVAS_DELETE:
					CIntentionCellController.getInstance().removeCellById(CIntentionCellController.getInstance().getCellByCanvasId(canvasId).getId());
					break;
			}
		}
	}

	private void createIntentionCell(long canvasId)
	{
		CIntentionCell cell = new CIntentionCell(UUIDAllocator.getUUID(), canvasId, false);
		CIntentionCellController.getInstance().addCell(cell);

		CalicoPacket p = cell.getCreatePacket();
		forward(p);
	}

	private static void CIC_CREATE(CalicoPacket p, Client c)
	{
		layoutGraph();
	}

	private static void CANVAS_CLEAR(CalicoPacket p, Client c)
	{
		p.rewind();
		p.getInt(); // pop the command id
		long canvasId = p.getLong();

		System.out.println("Clearing canvas " + canvasId);

		for (Long linkId : CCanvasLinkController.getInstance().getLinkIdsForCanvas(canvasId))
		{
			CalicoPacket packet = new CalicoPacket();
			packet.putInt(IntentionalInterfacesNetworkCommands.CLINK_DELETE);
			packet.putLong(linkId);

			packet.rewind();
			CLINK_DELETE(packet, null, false);
		}

		CIntentionCell cell = CIntentionCellController.getInstance().getCellByCanvasId(canvasId);

		for (CIntentionType intentionType : CIntentionCellController.getInstance().getActiveIntentionTypes())
		{
			if (cell.hasIntentionType(intentionType.getId()))
			{
				CalicoPacket packet = new CalicoPacket();
				packet.putInt(IntentionalInterfacesNetworkCommands.CIC_UNTAG);
				packet.putLong(cell.getId());
				packet.putLong(intentionType.getId());

				CIC_UNTAG(packet, null, false);
			}
		}

		if (cell.hasUserTitle())
		{
			CalicoPacket resetTitle = new CalicoPacket();
			resetTitle.putInt(IntentionalInterfacesNetworkCommands.CIC_SET_TITLE);
			resetTitle.putLong(cell.getId());
			resetTitle.putString(CIntentionCell.DEFAULT_TITLE);
			CIC_SET_TITLE(resetTitle, null, false);
		}

		System.out.println("Done clearing canvas " + canvasId);
	}

	private static void CIC_MOVE(CalicoPacket p, Client c)
	{
		p.rewind();
		IntentionalInterfacesNetworkCommands.Command.CIC_MOVE.verify(p);

		long uuid = p.getLong();
		CIntentionCell cell = CIntentionCellController.getInstance().getCellById(uuid);

		int x = p.getInt();
		int y = p.getInt();
		cell.setLocation(x, y);

		forward(p, c);
	}

	private static void CIC_SET_TITLE(CalicoPacket p, Client c, boolean forward)
	{
		p.rewind();
		IntentionalInterfacesNetworkCommands.Command.CIC_SET_TITLE.verify(p);

		long uuid = p.getLong();
		CIntentionCell cell = CIntentionCellController.getInstance().getCellById(uuid);

		cell.setTitle(p.getString());

		if (forward)
		{
			forward(p, c);
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

		forward(p, c);
	}

	private static void CIC_UNTAG(CalicoPacket p, Client c, boolean forward)
	{
		p.rewind();
		IntentionalInterfacesNetworkCommands.Command.CIC_UNTAG.verify(p);

		long uuid = p.getLong();
		long typeId = p.getLong();

		CIntentionCell cell = CIntentionCellController.getInstance().getCellById(uuid);
		cell.removeIntentionType(typeId);

		if (forward)
		{
			forward(p, c);
		}
	}

	private static void CIC_DELETE(CalicoPacket p, Client c)
	{
		p.rewind();
		IntentionalInterfacesNetworkCommands.Command.CIC_DELETE.verify(p);

		long uuid = p.getLong();
		CIntentionCellController.getInstance().removeCellById(uuid);

		layoutGraph();

		forward(p, c);
	}

	private static void CIT_CREATE(CalicoPacket p, Client c)
	{
		p.rewind();
		IntentionalInterfacesNetworkCommands.Command.CIT_CREATE.verify(p);

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

		forward(p, c);
	}

	private static void CIT_SET_COLOR(CalicoPacket p, Client c)
	{
		p.rewind();
		IntentionalInterfacesNetworkCommands.Command.CIT_SET_COLOR.verify(p);

		long uuid = p.getLong();
		int color = p.getInt();
		CIntentionCellController.getInstance().setIntentionTypeColor(uuid, color);

		forward(p, c);
	}

	private static void CIT_DELETE(CalicoPacket p, Client c)
	{
		p.rewind();
		IntentionalInterfacesNetworkCommands.Command.CIT_DELETE.verify(p);

		long uuid = p.getLong();

		CIntentionCellController.getInstance().removeIntentionType(uuid);

		forward(p, c);
	}

	private static CCanvasLinkAnchor unpackAnchor(long link_uuid, CalicoPacket p)
	{
		long uuid = p.getLong();
		long canvas_uuid = p.getLong();
		CCanvasLinkAnchor.Type type = CCanvasLinkAnchor.Type.values()[p.getInt()];
		int x = p.getInt();
		int y = p.getInt();
		long group_uuid = p.getLong();
		return new CCanvasLinkAnchor(uuid, link_uuid, canvas_uuid, type, x, y, group_uuid);
	}

	private static void CLINK_CREATE(CalicoPacket p, Client c)
	{
		p.rewind();
		IntentionalInterfacesNetworkCommands.Command.CLINK_CREATE.verify(p);

		long uuid = p.getLong();
		CCanvasLinkAnchor anchorA = unpackAnchor(uuid, p);
		CCanvasLinkAnchor anchorB = unpackAnchor(uuid, p);
		CCanvasLink link = new CCanvasLink(uuid, anchorA, anchorB);
		CCanvasLinkController.getInstance().addLink(link);

		layoutGraph();

		forward(p, c);
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

		forward(p, c);
	}

	private static void CLINK_LABEL(CalicoPacket p, Client c)
	{
		p.rewind();
		IntentionalInterfacesNetworkCommands.Command.CLINK_LABEL.verify(p);

		long uuid = p.getLong();
		CCanvasLink link = CCanvasLinkController.getInstance().getLinkById(uuid);
		link.setLabel(p.getString());

		forward(p, c);
	}

	private static void CLINK_DELETE(CalicoPacket p, Client c, boolean forward)
	{
		p.rewind();
		IntentionalInterfacesNetworkCommands.Command.CLINK_DELETE.verify(p);

		long uuid = p.getLong();
		CCanvasLinkController.getInstance().removeLinkById(uuid);

		layoutGraph();

		if (forward)
		{
			forward(p, c);
		}
	}

	private static void forward(CalicoPacket p)
	{
		forward(p, null);
	}

	private static void forward(CalicoPacket p, Client c)
	{
		if (c == null)
		{
			ClientManager.send(p);
		}
		else
		{
			ClientManager.send_except(c, p);
		}
	}

	private static void layoutGraph()
	{
		CIntentionLayout.getInstance().populateLayout();
		Set<Long> movedCells = CIntentionLayout.getInstance().layoutGraph();

		for (CIntentionCell cell : CIntentionCellController.getInstance().getAllCells())
		{
			if (movedCells.contains(cell.getCanvasId()))
			{
				CalicoPacket p = new CalicoPacket();
				p.putInt(IntentionalInterfacesNetworkCommands.CIC_MOVE);
				p.putLong(cell.getId());
				p.putInt(cell.getLocation().x);
				p.putInt(cell.getLocation().y);
				forward(p);
			}
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