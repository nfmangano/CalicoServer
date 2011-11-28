package calico.components;

import calico.*;
import calico.clients.*;
import calico.controllers.*;
import calico.networking.netstuff.*;

import it.unimi.dsi.fastutil.longs.*;

public class CCanvasBackupState extends BackupState
{
	public long uuid = 0L;
	private CalicoPacket[] packetlist = null;

	
	public CCanvasBackupState(long uuid, CalicoPacket[] packets)
	{
		this.uuid = uuid;
		this.packetlist = packets;
	}
	
	
	public boolean equals(CCanvasBackupState state)
	{
		// TODO: go thru all the fields and check if they are the same
		
		return false;
	}
	
	/**
	 * This will update the current state of the server from this current state to the value of newState
	 * @param newState
	 */
	public void updateToNewState(CCanvasBackupState newState)
	{
		// This should check to see if anything has changed
		
		newState.updateToThisState(this);
		
	}//updateToNewState
	
	
	/**
	 * Updates to the calling state NOT THE PASSED STATE
	 * @param currentState
	 */
	public void updateToThisState(CCanvasBackupState currentState)
	{
		
		// TODO: Maybe we should somehow lock the canvas, so that someone cant be editing it, whilst we are restoring

//		ClientManager.send(CalicoPacket.getPacket(NetworkCommand.CANVAS_CLEAR_FOR_SC, this.uuid));
		
		int size = ByteUtils.SIZE_OF_INT * 2;
		
		for (int i = 0; i < packetlist.length; i++)
		{
			size += packetlist[i].getBufferSize();
		}
		
		CalicoPacket canvasLoadPacket = new CalicoPacket(size);
		canvasLoadPacket.putInt(NetworkCommand.CANVAS_LOAD);
		canvasLoadPacket.putLong(this.uuid);
		canvasLoadPacket.putInt(packetlist.length);
		
		for (int i = 0;i < packetlist.length; i++)
		{
			canvasLoadPacket.putInt(packetlist[i].getBufferSize());
			canvasLoadPacket.putBytes(packetlist[i].getBuffer());
		}
		
		ClientManager.send(canvasLoadPacket);
		ProcessQueue.receive(NetworkCommand.CANVAS_LOAD, null, canvasLoadPacket);
		
		
		
//		// Clear out the canvas
//		CCanvasController.no_notify_clear_for_state_change(this.uuid);
//		
//		
//		for(int p=0;p<this.packetlist.length;p++)
//		{
//			this.packetlist[p].rewind();
//			
//			int command = this.packetlist[p].getInt();
//			
//			if(command==NetworkCommand.CANVAS_INFO)
//			{
//				// for now: we ignore this, because we cant change a canvas
//			}
//			else if(command==NetworkCommand.STROKE_START)
//			{
//				
//			}
//			else if(command==NetworkCommand.STROKE_APPEND)
//			{
//				
//			}
//			else if(command==NetworkCommand.STROKE_FINISH)
//			{
//				
//			}
//			else if(command==NetworkCommand.GROUP_START)
//			{
//				
//			}
//			else if(command==NetworkCommand.GROUP_APPEND_CLUSTER)
//			{
//				
//			}
//			else if(command==NetworkCommand.GROUP_FINISH)
//			{
//				
//			}
//			else
//			{
//				// dunno wtf happened here... we shouldnt hit this.
//				// if we do, then just ignore it
//			}
//			
//			// As long as its not the canvas_info, we should just send it along
//			if(command!=NetworkCommand.CANVAS_INFO)
//			{
//				// TODO: DEV ONLY, WE SHOULD PROBABLY ONLY WORK WITH THE COMMANDS WE KNOW
//				ProcessQueue.receive(command, null, this.packetlist[p]);
//				
//				ClientManager.send(this.packetlist[p]);
//			}
//			
//		}//for
//		
//		
//		
//		CCanvasController.state_change_complete(this.uuid);
		
	}
	
	
}
