package calico.plugins.iip;

import calico.networking.netstuff.CalicoPacket;

public class CIntentionType
{
	public static final int AVAILABLE_COLOR_COUNT = 9;

	private final long uuid;
	private String name;
	private int colorIndex;
	
	public static long noTagIntentionType = 0l;

	public CIntentionType(long uuid, String name, int colorIndex)
	{
		this.uuid = uuid;
		this.name = name;
		this.colorIndex = colorIndex;
	}

	public long getId()
	{
		return uuid;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getColorIndex()
	{
		return colorIndex;
	}

	public void setColorIndex(int colorIndex)
	{
		this.colorIndex = colorIndex;
	}

	public CalicoPacket getState()
	{
		return CalicoPacket.getPacket(IntentionalInterfacesNetworkCommands.CIT_CREATE, uuid, name, colorIndex);
	}
}
