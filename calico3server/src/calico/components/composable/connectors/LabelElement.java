package calico.components.composable.connectors;

import java.awt.Font;
import java.awt.Point;
import java.awt.Polygon;

import calico.components.CConnector;
import calico.components.composable.ComposableElement;
import calico.controllers.CConnectorController;
import calico.networking.netstuff.ByteUtils;
import calico.networking.netstuff.CalicoPacket;
import calico.networking.netstuff.NetworkCommand;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

public class LabelElement extends ComposableElement{

	private String text;
	private Font font;
	
	public LabelElement(long uuid, long cuuid, String text, Font font) {
		super(uuid, cuuid);
		this.text = text;
		this.font = font;
	}

	
	public CalicoPacket getPacket(long uuid, long cuuid)
	{
		int packetSize = ByteUtils.SIZE_OF_INT 				//Command
				+ ByteUtils.SIZE_OF_INT 				//Element Type
				+ (2 * ByteUtils.SIZE_OF_LONG) 			//UUID & CUUID
				+ CalicoPacket.getSizeOfString(text)	//Label Text
				+ CalicoPacket.getSizeOfString(font.getName()) //Font name
				+ (2 * ByteUtils.SIZE_OF_INT);			//Font style and size
	
		CalicoPacket packet = new CalicoPacket(packetSize);
		
		packet.putInt(NetworkCommand.ELEMENT_ADD);
		packet.putInt(ComposableElement.TYPE_LABEL);
		packet.putLong(uuid);
		packet.putLong(cuuid);
		packet.putString(text);
		packet.putString(font.getName());
		packet.putInt(font.getStyle());
		packet.putInt(font.getSize());
		
		return packet;
	}
	
	public CalicoPacket getPacket()
	{
		return getPacket(this.uuid, this.cuuid);
	}
}
