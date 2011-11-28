package calico.uuid;

import calico.COptions;
import calico.CalicoServer;
import calico.networking.netstuff.*;

import it.unimi.dsi.fastutil.longs.*;

import org.apache.log4j.*;

public class UUIDAllocator
{
	private static LongArrayList uuidlist = new LongArrayList();
	
	private static long nextid = 0L;
	

	private static Logger logger = Logger.getLogger(UUIDAllocator.class.getName());//Logger.getLogger("server");
	
	/**
	 * This initializes the allocator and stuff
	 */
	public static void setup()
	{
		allocateMore();
	}
	
	public static void restoreUUIDAllocator(long startingID)
	{
		uuidlist.clear();
		uuidlist.trim();
		
		nextid = startingID+1;

		allocateMore();
		allocateMore();
		
	}
	
	public static int getUUIDPoolSize()
	{
		return uuidlist.size();
	}
	
	/**
	 * We need to replenish the uuid pool
	 */
	public static void allocateMore()
	{
		logger.debug("Allocating more UUIDs");
		for(int i=0;i<COptions.uuid.allocation_increment;i++)
		{
			uuidlist.add(++nextid);
		}
	}

	/**
	 * We need a new UUID
	 * @return new uuid
	 */
	public synchronized static long getUUID()
	{
		if(uuidlist.size()<=COptions.uuid.min_size)
		{
			allocateMore();
		}
		return uuidlist.removeLong(0);
	}

	/**
	 * This creates a new packet for the UUID allocation for a client
	 * @return
	 */
	public synchronized static CalicoPacket getClientUUIDBlock()
	{
		int size = ByteUtils.SIZE_OF_INT + ByteUtils.SIZE_OF_INT + (ByteUtils.SIZE_OF_LONG*COptions.uuid.block_size);
		
		CalicoPacket p = new CalicoPacket(NetworkCommand.UUID_BLOCK, size);
		p.putInt(COptions.uuid.block_size);// The number of UIDs to get
		for(int i=0;i<COptions.uuid.block_size;i++)
		{
			p.putLong(getUUID());
		}
		
		return p;
	}
		

}



