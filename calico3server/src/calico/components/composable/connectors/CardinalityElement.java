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

public class CardinalityElement extends ComposableElement {

	private int type;
	private String text;
	private Font font;
	
	public CardinalityElement(long uuid, long cuuid, int type, String text, Font font) {
		super(uuid, cuuid);
		this.type = type;
		this.text = text;
		this.font = font;
	}

	
	public CalicoPacket getPacket(long uuid, long cuuid)
	{
		int packetSize = ByteUtils.SIZE_OF_INT 				//Command
					+ ByteUtils.SIZE_OF_INT 				//Element Type
					+ (2 * ByteUtils.SIZE_OF_LONG) 			//UUID & CUUID
					+ ByteUtils.SIZE_OF_INT					//Anchor Type
					+ CalicoPacket.getSizeOfString(text)	//Cardinality Text
					+ CalicoPacket.getSizeOfString(font.getName()) //Font name
					+ (2 * ByteUtils.SIZE_OF_INT);			//Font style and size
		
		CalicoPacket packet = new CalicoPacket(packetSize);
		
		packet.putInt(NetworkCommand.ELEMENT_ADD);
		packet.putInt(ComposableElement.TYPE_CARDINALITY);
		packet.putLong(uuid);
		packet.putLong(cuuid);
		packet.putInt(type);
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
