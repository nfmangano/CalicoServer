package calico.plugins.iip;

import java.util.concurrent.atomic.AtomicInteger;

import calico.networking.netstuff.CalicoPacket;

public class CCanvasLink
{
	private static final AtomicInteger INDEX_COUNTER = new AtomicInteger();
	
	private long uuid;
	private int index; // not visible to clients

	private CCanvasLinkAnchor anchorA;
	private CCanvasLinkAnchor anchorB;
	
	private String label;

	public CCanvasLink(long uuid, CCanvasLinkAnchor anchorA, CCanvasLinkAnchor anchorB)
	{
		this.uuid = uuid;
		this.index = INDEX_COUNTER.getAndIncrement();
		this.anchorA = anchorA;
		this.anchorB = anchorB;
		this.label = "";
	}
	
	public long getId()
	{
		return uuid;
	}
	
	public int getIndex()
	{
		return index;
	}

	public CCanvasLinkAnchor getAnchorA()
	{
		return anchorA;
	}
	
	public CCanvasLinkAnchor getAnchorB()
	{
		return anchorB;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public void setLabel(String label)
	{
		this.label = label;
	}

	public CalicoPacket getState()
	{
		return CalicoPacket.getPacket(
				IntentionalInterfacesNetworkCommands.CLINK_CREATE,
				uuid,
				anchorA.getId(),
				anchorA.getCanvasId(),
				anchorA.getType().ordinal(),
				anchorA.getPoint().x,
				anchorA.getPoint().y,
				anchorA.getGroupId(),
				anchorB.getId(),
				anchorB.getCanvasId(),
				anchorB.getType().ordinal(),
				anchorB.getPoint().x,
				anchorB.getPoint().y,
				anchorB.getGroupId(),
				label
		);
	}
}
