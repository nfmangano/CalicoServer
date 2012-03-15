package calico;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import calico.clients.Client;
import calico.networking.netstuff.CalicoPacket;

public class CanvasThread extends Thread {
	
	private static ArrayBlockingQueue<CanvasPacket> packetQueue;
	
	private int sleepCount;
	private long canvasid;
	
	public CanvasThread(long canvasid) throws IOException
	{
		super("CanvasThread-"+canvasid);
		packetQueue = new ArrayBlockingQueue<CanvasPacket>(4096);
		
		this.canvasid = canvasid;
		sleepCount = 0;
		
		start();
	}
	
	public void addPacketToQueue(int command,Client client,CalicoPacket packet)
	{
		packetQueue.offer(new CanvasPacket(command, client, packet));
	}
	
	public void run()
	{
		while(true)
		{
			if (!packetQueue.isEmpty())
			{
				sleepCount = 0;
				try
				{
					CanvasPacket packet = packetQueue.poll();
					if (packet != null)
					{
						ProcessQueue.receive(packet.command, packet.client, packet.packet);
					}
				}
				//Catch possible concurrency issue.
				catch(Exception e)
				{
					e.printStackTrace();
					//If there is an error, we kill the thread, otherwise there may be an infinite loop of errors.
					//Of course this means none of the packets in the queue will be processed, but it is better than infinite loop.
					return;
				}
			
			}
			else
			{
				sleepCount++;
				
				//Kill the thread if no packets are queued for 5 seconds. 
				if (sleepCount >= COptions.canvas.max_sleep_count)
				{
					synchronized(CalicoServer.canvasThreads)
					{
						CalicoServer.canvasThreads.remove(canvasid);
					}
					return;
				}
				
				// CPU Limiter (This prevents the CPU from going bonkers)
				try {
					Thread.sleep(COptions.canvas.sleeptime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class CanvasPacket{
		
		public CalicoPacket packet;
		public Integer command;
		public Client client;
		
		public CanvasPacket(int command, Client client, CalicoPacket packet)
		{
			this.packet = packet;
			this.command = command;
			this.client = client;
		}		
	}
}
