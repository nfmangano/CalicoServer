package calico.components;

import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import calico.COptions;
import calico.controllers.CImageController;
import calico.networking.netstuff.CalicoPacket;
import calico.networking.netstuff.NetworkCommand;

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
	public int get_signature()
	{
		return 0;
	}
	
}
