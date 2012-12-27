package calico.controllers;

import calico.COptions;
import calico.clients.Client;
import calico.clients.ClientManager;
import calico.components.CCanvas;
import calico.events.CalicoEventHandler;
import calico.events.CalicoEventListener;
import calico.networking.netstuff.CalicoPacket;
import calico.networking.netstuff.NetworkCommand;
import calico.uuid.UUIDAllocator;

public class CGridController {
	
	private CGridController instance =  new CGridController();
	private static boolean isActive = false;
	
	public CGridController getInstance()
	{
		return instance;
	}
	
	private CGridController()
	{
		CalicoEventHandler.getInstance().addListener(NetworkCommand.CANVAS_LIST, listener, CalicoEventHandler.ACTION_PERFORMER_LISTENER);
	}

	private static void initializeGridCanvases()
	{
		for(int i=0;i<COptions.GridRows;i++)
		{	  	
			for(int y=0;y<COptions.GridCols;y++)
			{
				// Make the canvas
				CCanvas can = new CCanvas(UUIDAllocator.getUUID());
				// Add to the main list
				CCanvasController.canvases.put(can.getUUID(), can);
			
			}
		}
	}
	
	public static void initialize()
	{
		isActive = true;
		initializeGridCanvases();
	}
	
	public static int getCanvasRow(long cuid)
	{
		//Formula: Floor(Index / NumColumns)
		return (int) Math.floor(CCanvasController.canvases.get(cuid).getIndex() / COptions.GridCols);
	}
	
	public static int getCanvasColumn(long cuid)
	{
		//Formula: Index - Row * NumColumns
		return CCanvasController.canvases.get(cuid).getIndex() - getCanvasRow(cuid) * COptions.GridCols;
	}
	
	public static String getCanvasCoord(long cuid)
	{
		int x = getCanvasRow(cuid);
		int y = getCanvasColumn(cuid);
		return (Character.valueOf( (char) (x+65)) ).toString()+""+y;
	}
	
	private static CalicoEventListener listener = new CalicoEventListener() {		
		@Override
		public void handleCalicoEvent(int event, CalicoPacket p, Client client) {
			if (event == NetworkCommand.CANVAS_LIST)
				GRID_SIZE(p, client);
			
		}
	};
	
	public static void GRID_SIZE(CalicoPacket notused,Client client)
	{
		if (!isActive)
			return;
		
		CalicoPacket p = new CalicoPacket();
		p.putInt(NetworkCommand.GRID_SIZE);
		p.putInt(COptions.GridRows);
		p.putInt(COptions.GridCols);
		ClientManager.send(client,p);
		
		// Load up the sessions?
		CSessionController.sendSessionList();
		
	}
	
}
