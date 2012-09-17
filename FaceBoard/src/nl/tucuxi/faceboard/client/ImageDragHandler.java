package nl.tucuxi.faceboard.client;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import java.util.HashMap;

import nl.tucuxi.faceboard.shared.ImageObject;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;

public class ImageDragHandler implements DragHandler {

	static HashMap<Image, ImageObject> hm = new HashMap<Image, ImageObject>();
	private FBDataSourceAsync fbdatasource;
	private ScrollPanel scrollPanel;
	private TextBox statusBox;
	static ImageObject lastDragged;
	
	public ImageDragHandler(FBDataSourceAsync fbdatasource, ScrollPanel scrollpanel, TextBox status) {
		this.fbdatasource=fbdatasource;
		this.scrollPanel=scrollpanel;
		this.statusBox=status;
	}

	/* (non-Javadoc)
	 * @see com.allen_sauer.gwt.dnd.client.DragHandler#onDragEnd(com.allen_sauer.gwt.dnd.client.DragEndEvent)
	 * As soon as a drag of an image is finished, this method is invoked.
	 */
	@Override
	public void onDragEnd(DragEndEvent event) {
		statusBox.setText("Updating image position");
		ImageObject io = hm.get(event.getSource());
		
		// mark the ImageObject as the lastdragged object
		lastDragged = io;
		
		// Get scroll correction 
		io.setX(event.getContext().desiredDraggableX+scrollPanel.getHorizontalScrollPosition());
		io.setY(event.getContext().desiredDraggableY+scrollPanel.getVerticalScrollPosition());

		// Specify what needs to be done after callback
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) { 
				Window.alert("Failed to update position after drag event , error:" + caught);
			}

			@Override
			public void onSuccess(Void result) {
				statusBox.setText("Done");
			}
		}; 
		
		// Update image object location details
		fbdatasource.updateImageObject(io, callback);

	}

	@Override
	public void onDragStart(DragStartEvent event) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPreviewDragStart(DragStartEvent event)
			throws VetoDragException {
		// TODO Auto-generated method stub

	}

}
