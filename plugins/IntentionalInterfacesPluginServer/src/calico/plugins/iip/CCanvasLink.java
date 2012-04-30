package calico.plugins.iip;

import calico.networking.netstuff.CalicoPacket;

public class CCanvasLink
{
	private long uuid;

	private CCanvasLinkAnchor anchorA;
	private CCanvasLinkAnchor anchorB;
	
	private String label;

	public CCanvasLink(long uuid, CCanvasLinkAnchor anchorA, CCanvasLinkAnchor anchorB)
	{
		this.uuid = uuid;
		this.anchorA = anchorA;
		this.anchorB = anchorB;
		this.label = "";
	}
	
	public long getId()
	{
		return uuid;
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
