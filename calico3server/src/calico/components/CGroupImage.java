package calico.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import calico.COptions;
import calico.controllers.CImageController;
import calico.networking.netstuff.CalicoPacket;
import calico.networking.netstuff.NetworkCommand;
import edu.umd.cs.piccolo.util.PAffineTransform;

public class CGroupImage extends CGroup {

	private String imgURL;
	private Image image;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CGroupImage(long uuid, long cuid, long puid, String img, int x, int y, int imageWidth, int imageHeight) {
		super(uuid, cuid,puid, true);
		imgURL = img;
		setImage(img);
		Rectangle bounds = new Rectangle(x, y, imageWidth, imageHeight);
		setShapeToRoundedRectangle(bounds, 0);
	}
	
	@Override
	public CalicoPacket[] getUpdatePackets(long uuid, long cuid, long puid, int dx, int dy, boolean captureChildren)
	{
		//TODO: Figure out why that + 10 is down there. It very likely has to do with the buffer
		Rectangle bounds = this.getRawPolygon().getBounds();
		String urlToImage = CImageController.getImageURL(this.uuid);
		String imageLocalPath = CImageController.getImageLocalPath(this.uuid);
		CalicoPacket packet = CalicoPacket.getPacket(NetworkCommand.GROUP_IMAGE_LOAD,
						uuid,
						cuid,
						puid,
						urlToImage,
						COptions.admin.serversocket.getLocalPort(),
						imageLocalPath,
//						this.imgURL,
						bounds.x + dx,	//going to be honest here, I'm not sure exactly why the + 10's are necessary, but they are
						bounds.y + dy, // 
						bounds.width,
						bounds.height,
						this.isPermanent,
						captureChildren,
						this.rotation,
						this.scaleX,
						this.scaleY);
//		packet.putImage(this.image);
		
		return new CalicoPacket[]{packet};
	}
	
	public void setImage(String imgURL) {
		
		if (!CImageController.imageExists(uuid))
			CImageController.download_image_no_exception(uuid, imgURL);		
		
		//There is no reason to load the image on the server.
		/*try
		{
			image = ImageIO.read(new File(CImageController.getImagePath(uuid)));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}*/
//		image = Toolkit.getDefaultToolkit().createImage(CImageController.getImagePath(uuid));

	}
	
	@Override
	public CalicoPacket[] getUpdatePackets(boolean captureChildren)
	{
		return getUpdatePackets(this.uuid, this.cuid, this.puid, 0, 0, captureChildren);
	}
	
	@Override
	protected void render_internal(Graphics2D g) {
		//    super.render_internal(g);

		final Graphics2D g2 = g;
		// g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_ON);
		PAffineTransform piccoloTransform = getPTransform();
		AffineTransform old = g2.getTransform();
		g2.setTransform(piccoloTransform);
		g2.setColor(Color.BLACK);
		Rectangle bounds = this.getRawPolygon().getBounds();
		Composite temp = g2.getComposite();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		Image img = null;
		try {
			img = ImageIO.read(new File(CImageController.getImagePath(uuid)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		g2.drawImage(img, bounds.x, bounds.y, bounds.width, bounds.height,
				null);
		g2.setComposite(temp);
		g2.setTransform(old);
	}

	@Override
	public int get_signature()
	{
		return 0;
	}
	
}
