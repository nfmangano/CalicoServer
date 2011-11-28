package calico.utils;

public class TickerTask
{
	public static final int TASKTYPE_TIME = 1 << 0;
	public static final int TASKTYPE_TICK = 1 << 1;
	
	/**
	 * This is the timestamp after which the task should run
	 */
	public long run_after = 0L;
	
	public int run_ontick = 66;
	public int tasktype = TASKTYPE_TIME;
	
	

	/**
	 * This runs the task
	 * @return true to KEEPALIVE
	 */
	public boolean runtask()
	{
		return false;
	}
	
}
