package nl.tucuxi.faceboard.client;

import nl.tucuxi.faceboard.shared.ImageObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/*
 * Remote interface with data access methods implemented by the server class.
 * 
 */

@RemoteServiceRelativePath("FBDataSource")
public interface FBDataSource extends RemoteService {
	/**
	 * Utility class for simplifying access to the instance of async service.
	 * @throws Throwable 
	 */
	
	
	public static class Util {
		private static FBDataSourceAsync instance;
		public static FBDataSourceAsync getInstance(){
			if (instance == null) {
				instance = GWT.create(FBDataSource.class);
			}
			return instance;
		}
	}

	public ImageObject[] getImageObjects();

	public void putImageObject(ImageObject io);
	
	public boolean removeImageObject(ImageObject io, String confirmationcode);

	public void updateImageObject(ImageObject io);
}