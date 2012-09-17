package nl.tucuxi.faceboard.client;

import java.util.HashMap;

import nl.tucuxi.faceboard.shared.ImageObject;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Image;

public class MyFaceBoard implements EntryPoint {

	private AbsolutePanel absolutePanel;
	private PickupDragController dragController;
	private TextBox txtbxFBusername;
	private TextBox txtbxRemovalCode;
	private ScrollPanel scrollPanel;
	private TextBox txtbxStatus;

	private FBDataSourceAsync fbdatasource = FBDataSource.Util.getInstance();

	@Override
	public void onModuleLoad() {

		RootPanel rootPanel = RootPanel.get();
		rootPanel.setStyleName("rootPanel");
		rootPanel.setSize("100%", "100%");

		txtbxStatus = new TextBox();
		txtbxStatus.setReadOnly(true);
		rootPanel.add(txtbxStatus, 10, 443);
		txtbxStatus.setSize("118px", "16px");

		absolutePanel = new AbsolutePanel();
		absolutePanel.setStyleName("absolutePanel");
		absolutePanel.setSize("1000px", "800px");
		ImageObject.setFieldSize(1000, 800);
		
		// Set the drag controller used to drag images.
		dragController = new PickupDragController(absolutePanel, true);
		
		scrollPanel = new ScrollPanel(absolutePanel);
		scrollPanel.setTouchScrollingDisabled(false);
		scrollPanel.setStyleName("scroll-panel");
		scrollPanel.setSize("600px", "400px");

		rootPanel.add(scrollPanel, 151, 65);
		ImageObject.setCorrections(165, 59); // Scrollpanel position + 3

		// create a DragController to manage drag-n-drop actions
		// note: This creates an implicit DropController for the boundary panel

		dragController.addDragHandler(new ImageDragHandler(fbdatasource,
				scrollPanel, txtbxStatus));

		Button addImageBtn = new Button("AddImage");
		addImageBtn.setText("Add facebook profile picture");

		addImageBtn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String image_url = "https://graph.facebook.com/";
				Image image = new Image(image_url + txtbxFBusername.getText()
						+ "/picture");
				image.setSize("50px", "50px");

				// Add image object on scrollable absolutepanel on corr 200,200
				ImageObject io = new ImageObject();
				io.generateKey();
				io.setX(200);
				io.setY(200);
				io.setUrl(image.getUrl());

				absolutePanel.add(image, io.getX(), io.getY());
				
				// Add the imageObject io the hashmap.
				ImageDragHandler.hm.put(image, io);
				
				// Store the new placed image in the datastore.
				AsyncCallback<Void> callback = new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Failed to insert image object, error:"
								+ caught);
					}

					@Override
					public void onSuccess(Void result) {
						Window.alert("Succesfully added the image to FaceBoard");
						txtbxStatus.setText("Done");
					}
				};
				txtbxStatus.setText("Putting image");
				fbdatasource.putImageObject(io, callback);
				
				// Makge the image draggable.
				dragController.makeDraggable(image);
			}
		});
		rootPanel.add(addImageBtn, 10, 65);
		addImageBtn.setSize("133px", "44px");

		Label lblFacebookUsername = new Label("Facebook username:");
		rootPanel.add(lblFacebookUsername, 10, 122);

		txtbxFBusername = new TextBox();
		txtbxFBusername.setText("jeroen.van.gangelen");
		rootPanel.add(txtbxFBusername, 10, 148);
		txtbxFBusername.setSize("117px", "16px");

		Button btnRemovePicture = new Button("Remove picture");
		
		// Async call for removing the picture
		btnRemovePicture.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Failed to remove image object, error:"
								+ caught);
					}

					@Override
					public void onSuccess(Boolean result) {
						if (result.booleanValue())
						{
							loadImages();
							Window.alert("Succesfully removed the image from FaceBoard");
						}
						else
							Window.alert("Incorrect confirmation code entered");
						txtbxStatus.setText("Done");
					}
				};
				txtbxStatus.setText("Removing image");
				fbdatasource.removeImageObject(ImageDragHandler.lastDragged, txtbxRemovalCode.getText(), callback);
			}
		});
		btnRemovePicture.setText("Remove picture");
		rootPanel.add(btnRemovePicture, 10, 328);
		btnRemovePicture.setSize("129px", "26px");

		txtbxRemovalCode = new TextBox();
		txtbxRemovalCode.setText("Removal code");
		rootPanel.add(txtbxRemovalCode, 10, 361);
		txtbxRemovalCode.setSize("118px", "16px");
		
		// Button to reload the images based on the locations in the datastore.
		Button btnReLoadLayout = new Button("Reload layout");
		
		btnReLoadLayout.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				loadImages();
				Window.alert("Reloaded images");
			}
			
		});
		rootPanel.add(btnReLoadLayout, 10, 208);
		btnReLoadLayout.setSize("128px", "22px");
		
		Label lblStatus = new Label("Status:");
		rootPanel.add(lblStatus, 10, 417);

		// Call load images during initial load of the app.
		loadImages();
	}

	public void loadImages() {

		// Fetch all imageObjects that need to be loaded from the datastore and place
		// them as Images on the absolutePanel
		AsyncCallback<ImageObject[]> callback = new AsyncCallback<ImageObject[]>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Unable to load images: "+caught);
			}

			@Override
			public void onSuccess(ImageObject[] result) {
				
				// Remove the pictures currently shown
				while(absolutePanel.getWidgetCount()>0)
					absolutePanel.remove(0);
				
				// Re-initialize the ImageObject hashmap.
				ImageDragHandler.hm = new HashMap<Image, ImageObject>();
				
				// Put all image objects on the panel
				for (ImageObject io : result) {
					Image image = new Image(io.getUrl());
					image.setSize("50px", "50px");
					absolutePanel.add(image, io.getX(), io.getY());
					ImageDragHandler.hm.put(image, io);
					dragController.makeDraggable(image);
				}
				txtbxStatus.setText("Done");
			}

		};
		txtbxStatus.setText("Loading images");
		fbdatasource.getImageObjects(callback);
	}
}
