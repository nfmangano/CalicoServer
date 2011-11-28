package calico.networking;

import it.unimi.dsi.fastutil.objects.*;

import java.io.IOException;
import java.net.*;

import calico.networking.netstuff.*;
import calico.*;

public class UDPReceiveQueue implements Runnable
{
	public static ObjectArrayList<CalicoPacket> receiveQueue = new ObjectArrayList<CalicoPacket>();
	
	private byte[] packetData = new byte[2048];
	private DatagramSocket socket = null;
	
	public UDPReceiveQueue()
	{
		try
		{
			socket = new DatagramSocket( new InetSocketAddress(COptions.listen.host, COptions.listen.port) );
		}
		catch (SocketException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void send(DatagramPacket packet)
	{
		try
		{
			this.socket.send(packet);
		}
		catch(IOException e){}
	}
	
	public void run()
	{
		try
		{
			while(true)
			{
			
				DatagramPacket receivePacket = new DatagramPacket(this.packetData, this.packetData.length);
				if (socket == null)
					continue;
				
                this.socket.receive(receivePacket);
                
                int psize = ByteUtils.readInt(this.packetData, 0);
                
                CalicoPacket packet = new CalicoPacket(this.packetData, ByteUtils.SIZE_OF_INT, psize);
                UDPPacketHandler.receive(packet, (InetSocketAddress) receivePacket.getSocketAddress());
                //CalicoServer.logger.debug("UDP RECEIVE: "+packet.toString());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			try
			{
				Thread.sleep(20L);
			}
			catch(Exception e2)
			{
				
			}
			run();
		}
	}
}