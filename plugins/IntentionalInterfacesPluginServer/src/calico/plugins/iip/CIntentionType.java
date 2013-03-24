package calico.plugins.iip;

import calico.networking.netstuff.CalicoPacket;

public class CIntentionType
{
	public static final int AVAILABLE_COLOR_COUNT = 9;

	private final long uuid;
	private String name;
	private int colorIndex;
	private String description;
	
	public static long noTagIntentionType = 0l;

	public CIntentionType(long uuid, String name, int colorIndex, String description)
	{
		this.uuid = uuid;
		this.name = name;
		this.colorIndex = colorIndex;
		this.description = description;
	}

	public long getId()
	{
		return uuid;
	}

	public String getName()
	{
		return name;
	}
	
	public String getDescription()
	{
		return description;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
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
		return CalicoPacket.getPacket(IntentionalInterfacesNetworkCommands.CIT_CREATE, uuid, name, colorIndex, description);
	}
}
