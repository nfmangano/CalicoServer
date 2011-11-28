package calico.plugins.events.scraps;

import calico.plugins.events.CalicoEvent;

public class ScrapReload extends CalicoEvent
{
	
	
	private long uuid = 0L;
	
	public ScrapReload(long uuid)
	{
		this.uuid = uuid;
	}
	
	@Override
	public void execute() throws Exception
	{
		debug("Executing ScrapReload");
	}
}
