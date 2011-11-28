package calico.clients;

import java.net.*;


public class Client 
{
	private InetAddress address;
	private int port;

	private int clientid = -1;
	private byte[] mac;

	public Client(InetAddress address, int port, int clientid)
	{
		this.port = port;
		this.address = address;
		this.clientid = clientid;
		
//		this.mac = address.getAddress();
//		NetworkInterface ni;
//		try {
//			ni = NetworkInterface.getByInetAddress(this.address);
//			
//			if (ni != null)
//			{
////				this.mac = ni.getHardwareAddress();
//				this.mac = address.getAddress();
//			}
//		} catch (SocketException e) {
//			e.printStackTrace();
//		}
	}
	public Client(InetAddress address, int port)
	{
		this(address, port, -1);
	}
	
	public int getClientID()
	{
		return this.clientid;
	}
	
	public InetAddress getAddress()
	{
		return this.address;
	}
	public int getPort()
	{
		return this.port;
	}
	
	public String toString()
	{
		return "<"+this.clientid+"><"+this.address.getHostAddress()+":"+this.port+">";
	}
	
	public String getUsername()
	{
		return ClientManager.get_client_username(this.clientid);
	}

	public boolean equals(Client c)
	{
		if( c.getAddress().equals(this.address) && c.getPort()==this.port)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public void updateID(int clientid2) {
		clientid = clientid2;
		
	}
	
//	public boolean sameMacAs(Client c)
//	{
//		if (c.mac.length == this.mac.length)
//		{
//			for (int i = 0; i < this.mac.length; i++)
//				if (c.mac[i] != this.mac[i])
//					return false;
//			return true;
//		}
//		
//		return false;
//	}

}//client


