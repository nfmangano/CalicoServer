package calico.components;

import it.unimi.dsi.fastutil.longs.Long2ReferenceArrayMap;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;

import calico.COptions;
import calico.controllers.CGroupController;
import calico.controllers.CStrokeController;
import calico.networking.netstuff.CalicoPacket;
import calico.networking.netstuff.NetworkCommand;
import edu.umd.cs.piccolo.nodes.PImage;

public class CList extends CGroup {

	public Long2ReferenceArrayMap<Boolean> groupCheckValues = new Long2ReferenceArrayMap<Boolean>();
	int iconWidth = 32, iconHeight = 32, iconWidthBuffer = 0;
	int iconXSpace = this.iconWidth + this.iconWidthBuffer*2;
	int widthBuffer = 5;
	
	private final boolean debugList = false;
	
	Image checkIcon, uncheckedIcon;	
	
	public CList(long uuid, long cuid, long puid) {
		super(uuid, cuid, puid);
		
		//override load signature of scrap
		networkLoadCommand = NetworkCommand.CLIST_LOAD;
	}
	
	@Override
	public void setChildGroups(long[] gplist) {
		super.setChildGroups(gplist);
		initializeCheckValues();
//		resetListElementPositions();
	}
	
	@Override
	public void move(int x, int y)
	{
		super.move(x, y);
		ArrayList<PImage> groupIcons = new ArrayList<PImage>();
		
		Rectangle bounds;
		for (PImage icon : groupIcons)
		{
			if (icon == null || icon.getBounds() == null)
				continue;
			bounds = icon.getBounds().getBounds();
			bounds.translate(x, y);
		}
	}
	
	@Override
	public void addChildGroup(long grpUUID, int x, int y) {
		if (CGroupController.exists(grpUUID) && !CGroupController.groups.get(grpUUID).isPermanent())
			return;
		
		super.addChildGroup(grpUUID, x, y);
		if (!groupCheckValues.containsKey(grpUUID))
			groupCheckValues.put(grpUUID, new Boolean(false));
		
		resetListElementPositions(grpUUID, x, y);
		
		recomputeBounds();
		recomputeValues();
		
	}
	
	@Override
	public void deleteChildGroup(long grpUUID) {
		super.deleteChildGroup(grpUUID);
		
		resetListElementPositions();
		
		recomputeBounds();
		recomputeValues();

	}
	
	@Override
	public void recomputeBounds()
	{
		resetListElementPositions();
		
		recomputeBoundsAroundElements();
		
		super.recomputeBounds();
	}

	public void recomputeBoundsAroundElements() {
		long[] children = getChildGroups();
		for (int i = 0; i < children.length; i++)
			if (!CGroupController.exists(children[i]) || CGroupController.groups.get(children[i]).getPathReference() == null)
				return;
		
		Rectangle bounds = getBoundsOfContents();
		
		Rectangle newBounds = bounds;
		if (text.length() < 1)
			newBounds = new Rectangle(bounds.x - widthBuffer - iconXSpace, bounds.y,
				bounds.width + widthBuffer*2 + iconXSpace, bounds.height);
		
		CGroupController.no_notify_make_rectangle(this.uuid, newBounds.x, newBounds.y, newBounds.width, newBounds.height);


	}
	
	@Override
	public void recomputeValues()
	{
		
		long[] childGroups = getChildGroups();

		for (int i = 0; i < childGroups.length; i++)
		{
			if (CGroupController.groups.get(childGroups[i]) instanceof CList)
			{
				CList innerList = ((CList)CGroupController.groups.get(childGroups[i]));
				if (innerList.getChildGroups().length > 0)
				{
					boolean containsUnchecked = false;
					long[] innerListGroups = innerList.getChildGroups();
					for (int j = 0; j < innerListGroups.length; j++)
						if (innerList.groupCheckValues.containsKey(innerListGroups[j])
								&& innerList.groupCheckValues.get(innerListGroups[j]).booleanValue() == false)
							containsUnchecked = true;
					
					
					if (containsUnchecked)
						setCheck(childGroups[i], false);
					else
						setCheck(childGroups[i], true);
				}
			}
		}
		super.recomputeValues();
	}
	
	public void setCheck(long guuid, boolean value)
	{
		if (!hasChildGroup(guuid))
			return;
		
		groupCheckValues.put(guuid, new Boolean(value));
	}
	
	public boolean isChecked(long guuid)
	{
		if (!groupCheckValues.containsKey(guuid)) { return false; }
		else
			return groupCheckValues.get(guuid).booleanValue();
	}
	
	@Override
	public boolean containsShape(Shape shape)
	{
		return this.containsPoint((int)shape.getBounds2D().getCenterX(), (int)shape.getBounds2D().getCenterY());
	}
	
	
	public void resetListElementPositions()
	{
		resetListElementPositions(false);
	}
	
	public void resetListElementPositions(boolean setLocationToFirstElement)
	{
		resetListElementPositions(setLocationToFirstElement, 0l, 0, 0);
	}
	
	public void resetListElementPositions(long guuid, int g_x, int g_y)
	{
		resetListElementPositions(false, guuid, g_x, g_y);
	}
	
	public void resetListElementPositions(boolean setLocationToFirstElement, long guuid, int g_x, int g_y) {
		
		int moveToX, moveToY, deltaX, deltaY, elementSpacing = 5;
		
		
		int yOffset = /*elementSpacing + *///0;
				(this.text.length() > 0) ? COptions.group.text_padding + new Double(COptions.group.padding*2).intValue() : 0;
		int widestWidth = 0;
		int x, y;
		
		if (setLocationToFirstElement && getChildGroups().length > 0)
		{
			long firstchild = getChildGroups()[0];
			x = CGroupController.groups.get(firstchild).getPathReference().getBounds().x - widthBuffer - iconXSpace ; //  bounds.x; // + widthBuffer / 2;
			y = CGroupController.groups.get(firstchild).getPathReference().getBounds().y - yOffset; //bounds.y; // + elementSpacing / 2;
		}
		else 
		{
			if (getPathReference() == null)
				return;
			
			x = this.getPathReference().getBounds().x + COptions.group.padding;
			y = this.getPathReference().getBounds().y + COptions.group.padding;
		}
		
		Rectangle bounds; 
		
		long[] listElements = getChildGroups();
		
		listElements = insertGroupByYPosition(listElements, guuid, g_x, g_y);
		
		if (listElements.length < 1 || CGroupController.groups.get(listElements[0]) == null || CGroupController.groups.get(listElements[0]).getPathReference() == null)
			return;
		
		if (debugList)
		{
			Rectangle lb = getPathReference().getBounds().getBounds();
			System.out.printf("List (%d)/\n\t bounds: (%d,%d,%d,%d)", this.uuid, lb.x, lb.y, lb.width, lb.height);
			System.out.println("");
		}
		for (int i = 0; i < listElements.length; i++)
		{
			if (!CGroupController.exists(listElements[i]))
				continue;
			
			//destination
			moveToX = x + widthBuffer + iconXSpace;
			moveToY = y;
			
			//figure out offset
			bounds = CGroupController.groups.get(listElements[i]).getPathReference().getBounds();
			deltaX = moveToX - bounds.x;
			deltaY = moveToY - bounds.y;
			
			if (debugList)
			{
				System.out.printf("\t%d: %d, %d, %d, %d", i, moveToX, moveToY + yOffset, bounds.width, bounds.height);
				System.out.println("");
			}
			
			CGroupController.no_notify_move(listElements[i], deltaX, deltaY + yOffset);
			yOffset += bounds.height + elementSpacing;
			
			//check for the widest width
			if (bounds.width > widestWidth)
				widestWidth = bounds.width;
		}
		if (listElements.length == 0)
		{
			widestWidth = 100;
			yOffset = 50;
		}

	}
	
	/**
	 * This method assumes that the given array is already ordered by their Y position
	 * 
	 * @param listElements
	 * @param guuid
	 * @param gX
	 * @param gY
	 * @return
	 */
	private long[] insertGroupByYPosition(long[] listElements, long guuid,
			int gX, int gY) {
		
		if (!CGroupController.exists(guuid))
			return listElements;
		
		long[] retList = listElements.clone(); 
		
		for (int i = 0; i < retList.length; i++)
			if (retList[i] == guuid)
				retList = ArrayUtils.removeElement(retList, guuid);
				

		for (int i = 0; i < retList.length; i++)
		{
			if (CGroupController.groups.get(retList[i]).getMidPoint().getY() > gY)
			{
				retList = ArrayUtils.add(retList, i, guuid);
				break;
			}
		}
		
		if (retList.length > 0)
		{
			if (CGroupController.groups.get(retList[retList.length-1]).getMidPoint().getY() < gY)
				retList = ArrayUtils.add(retList, guuid);
		}
		
		return retList;
	}
	
	public Rectangle[] getCheckIconBounds()
	{
		int moveToY, elementSpacing = 5, widthBuffer = 5;
		int iconXSpace = this.iconWidth + this.iconWidthBuffer*2;
		
		int yOffset = elementSpacing + 0;
		int x, y;
		
		long[] listElements = getChildGroups();
		
		if (listElements == null || listElements.length == 0)
			return new Rectangle[] { };
		
		long firstchild = listElements[0];
		if (!CGroupController.exists(firstchild))
			return new Rectangle[] { };
		
		x = CGroupController.groups.get(firstchild).getPathReference().getBounds().x - widthBuffer - iconXSpace; //  bounds.x; // + widthBuffer / 2;
		y = CGroupController.groups.get(firstchild).getPathReference().getBounds().y - yOffset; //bounds.y; // + elementSpacing / 2;
		
		Rectangle bounds;
		
		
		Rectangle[] checkMarkBounds = new Rectangle[listElements.length];
		
		if (listElements.length < 1 || CGroupController.groups.get(listElements[0]) == null || CGroupController.groups.get(listElements[0]).getPathReference() == null)
			return null;
		
		for (int i = 0; i < listElements.length; i++)
		{
			if (!CGroupController.exists(listElements[i]))
				continue;
			
			//destination
			moveToY = y;
			
			//figure out offset
			bounds = CGroupController.groups.get(listElements[i]).getPathReference().getBounds();

			checkMarkBounds[i] = new Rectangle(x + iconWidthBuffer, moveToY + yOffset + bounds.height/2 - iconHeight/2, this.iconWidth, this.iconHeight);
			yOffset += bounds.height + elementSpacing;
		}
		
		return checkMarkBounds;
	}	
	
	private void initializeCheckValues()
	{
		long[] childGroups = getChildGroups().clone();
		for (int i = 0; i < childGroups.length; i++)
		{
			if (!groupCheckValues.containsKey(childGroups[i]))
			{
				groupCheckValues.put(childGroups[i], new Boolean(false));
			}
		}
	}
	
	/**
	 * Serialize this activity node in a packet
	 */
	@Override
	public CalicoPacket[] getUpdatePackets(long uuid, long cuid, long puid,
			int dx, int dy, boolean captureChildren) {
		
		//Creates the packet for saving this CGroup
		CalicoPacket packet = super.getUpdatePackets(uuid, cuid, puid, dx, dy,
				captureChildren)[0];

		long[] keySet = groupCheckValues.keySet().toLongArray();
		packet.putInt(keySet.length);
		for (int i = 0; i < keySet.length; i++)
		{
			packet.putLong(keySet[i]);
			packet.putBoolean(groupCheckValues.get(keySet[i]));
		}

		return new CalicoPacket[] { packet };
	}
	
	public long getGroupCheckMarkAtPoint(Point p)
	{
		Rectangle[] checkMarks = getCheckIconBounds();
		long[] children = getChildGroups();
		if (checkMarks == null || children == null)
			return 0l;
		
		for (int i = 0; i < checkMarks.length; i++)
			if (checkMarks[i].contains(p))
				return children[i];
		
		return 0l;
	}
	
	private long[] orderByYAxis(long[] listItems)
	{
		int[] yValues = new int[listItems.length];
		
		//copy Y values to array to sort
		for (int i=0;i<listItems.length;i++)
		{
			if (CGroupController.exists(listItems[i]))
				yValues[i] = (int)CGroupController.groups.get(listItems[i]).getMidPoint().getY();
		}
		
		//sort the y values
		java.util.Arrays.sort(yValues);
		
		//match the y values back to their Groups and return the sorted array
		long[] sortedElementList = new long[listItems.length];
		for (int i=0;i<listItems.length;i++)
		{
			for (int j=0;j<listItems.length;j++)
			{
				if (CGroupController.exists(listItems[j]))
				{
					if ((int)CGroupController.groups.get(listItems[j]).getMidPoint().getY() == yValues[i])
					{
						sortedElementList[i] = listItems[j];
						//This line needed in case multiple groups have the same y value
						listItems[j] = -1;
						break;
					}
				}
			}
		}
		
		return sortedElementList;
	}
	
	@Override
	public boolean canParentChild(long child, int x,  int y)
	{
		if (child == 0l || child == this.uuid)
			return false;
		
		long potentialParent_new_parent = 0l;
		long child_parent = 0l;
		
		potentialParent_new_parent = getParentUUID();
		
		if (CStrokeController.strokes.containsKey(child))
		{
			return false;
		}
		else if (CGroupController.groups.containsKey(child))
		{
			if (!CGroupController.groups.get(child).isPermanent())
				return false;
			
			//must contain center of mass
			Point2D center = CGroupController.groups.get(child).getMidPoint();
			if (!this.containsPoint(x, y))
				return false;
			
			child_parent = CGroupController.groups.get(child).getParentUUID();
		}
		
		if (CGroupController.group_is_ancestor_of(child, this.uuid))
			return false;
		
		if (child_parent == 0l)
			return true;
		
		return potentialParent_new_parent == child_parent;
	}
	
	@Override
	public long[] getChildGroups()
	{
		return orderByYAxis(super.getChildGroups());
	}
	
}
