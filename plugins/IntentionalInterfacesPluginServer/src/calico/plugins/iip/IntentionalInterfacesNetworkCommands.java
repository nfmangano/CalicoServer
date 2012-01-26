package calico.plugins.iip;

import calico.networking.netstuff.CalicoPacket;

public enum IntentionalInterfacesNetworkCommands
{
	/**
	 * Create a new CIntentionCell
	 */
	CIC_CREATE,
	/**
	 * Move a CIntentionCell's (x,y) position
	 */
	CIC_MOVE,
	/**
	 * Delete a CIntentionCell
	 */
	CIC_DELETE,
	/**
	 * Create a new CCanvasLink
	 */
	CLINK_CREATE,
	/**
	 * Change the type of a CCanvasLink
	 */
	CLINK_RETYPE,
	/**
	 * Move one enpoint of a CCanvasLink
	 */
	CLINK_MOVE,
	/**
	 * Delete a CCanvasLink
	 */
	CLINK_DELETE;
	
	public final int id;

	private IntentionalInterfacesNetworkCommands()
	{
		this.id = ordinal() + OFFSET;
	}
	
	public boolean verify(CalicoPacket p)
	{
		return forId(p.getInt()) == this;
	}
	
	private static final int OFFSET = 2300;
	
	public static IntentionalInterfacesNetworkCommands forId(int id)
	{
		return IntentionalInterfacesNetworkCommands.values()[id - OFFSET];
	}
}
