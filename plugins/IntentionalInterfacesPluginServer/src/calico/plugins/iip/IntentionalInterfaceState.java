package calico.plugins.iip;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

import calico.networking.netstuff.CalicoPacket;

public class IntentionalInterfaceState
{
	private final List<CalicoPacket> packets = new ObjectArrayList<CalicoPacket>();
	
	private final List<CalicoPacket> cellPackets = new ObjectArrayList<CalicoPacket>();
	private final List<CalicoPacket> linkPackets = new ObjectArrayList<CalicoPacket>();
	
	public void reset()
	{
		packets.clear();
		cellPackets.clear();
		linkPackets.clear();
	}
	
	public void addCellPacket(CalicoPacket packet)
	{
		cellPackets.add(packet);
	}
	
	public void addLinkPacket(CalicoPacket packet)
	{
		linkPackets.add(packet);
	}
	
	public void setTopologyPacket(CalicoPacket packet)
	{
		packets.add(packet);
	}
	
	public void setClusterGraphPacket(CalicoPacket packet)
	{
		packets.add(packet);
	}
	
	public CalicoPacket[] getAllPackets()
	{
		packets.addAll(cellPackets);
		packets.addAll(linkPackets);
		packets.add(CalicoPacket.getPacket(IntentionalInterfacesNetworkCommands.CIC_UPDATE_FINISHED));
		
		return packets.toArray(new CalicoPacket[0]);
	}
}
