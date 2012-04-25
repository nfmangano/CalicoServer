package calico.plugins.iip;

import calico.networking.netstuff.CalicoPacket;

public class CIntentionType
{
	public static final int[] AVAILABLE_COLORS = new int[] { 0xC4FF5E, 0xFFF024, 0x29FFE2, 0x52DCFF, 0xF896FF, 0xFFBDC1, 0xFFFDBA, 0xC2E4FF, 0xEED9FF };

	private final long uuid;
	private String name;
	private int colorIndex;

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
