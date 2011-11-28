package calico.clients;

import calico.events.CalicoEventHandler;
import calico.events.CalicoEventListener;
import calico.networking.netstuff.CalicoPacket;
import calico.networking.netstuff.NetworkCommand;

public class ClientConsistencyListener implements CalicoEventListener {

	private static ClientConsistencyListener instance;
	
	public static boolean ignoreConsistencyCheck = false;
	
	public ClientConsistencyListener()
	{
		CalicoEventHandler.getInstance().addListener(NetworkCommand.GROUP_MOVE_START, this, CalicoEventHandler.PASSIVE_LISTENER);
		CalicoEventHandler.getInstance().addListener(NetworkCommand.GROUP_MOVE_END, this, CalicoEventHandler.PASSIVE_LISTENER);
		CalicoEventHandler.getInstance().addListener(NetworkCommand.ERASE_START, this, CalicoEventHandler.PASSIVE_LISTENER);
		CalicoEventHandler.getInstance().addListener(NetworkCommand.ERASE_END, this, CalicoEventHandler.PASSIVE_LISTENER);
		
//		System.out.println("~~~~~~~~~~~~ Instanciated Consisteny listener!! ~~~~~~~~~~~~");
	}
	
	public static ClientConsistencyListener getInstance()
	{
		if (instance == null)
			 instance = new ClientConsistencyListener();
		
		return instance;
	}
	
	@Override
	public void handleCalicoEvent(int event, CalicoPacket p, Client client) {
		
		switch (event)
		{
			case NetworkCommand.GROUP_MOVE_START:
				ignoreConsistencyCheck = true;
				break;
			case NetworkCommand.GROUP_MOVE_END:
				ignoreConsistencyCheck = false;
				break;
			case NetworkCommand.ERASE_START:
				ignoreConsistencyCheck = true;
				break;
			case NetworkCommand.ERASE_END:
				ignoreConsistencyCheck = false;
				break;
			default:
				break;
		}

	}

}
