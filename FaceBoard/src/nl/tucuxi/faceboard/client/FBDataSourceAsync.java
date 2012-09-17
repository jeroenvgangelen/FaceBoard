package nl.tucuxi.faceboard.client;

import nl.tucuxi.faceboard.shared.ImageObject;

import com.google.gwt.user.client.rpc.AsyncCallback;
/*
 * Public interface with data access methods called asynchronously by the UI client.
 * 
 */

public interface FBDataSourceAsync {

	void putImageObject(ImageObject io, AsyncCallback<Void> callback);

	void updateImageObject(ImageObject io, AsyncCallback<Void> callback);

	void getImageObjects(AsyncCallback<ImageObject[]> callback);

	void removeImageObject(ImageObject io, String confirmationcode,
			AsyncCallback<Boolean> callback);

}
