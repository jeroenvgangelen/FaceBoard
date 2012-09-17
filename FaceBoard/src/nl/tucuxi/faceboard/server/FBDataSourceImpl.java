package nl.tucuxi.faceboard.server;

import java.util.ArrayList;
import org.mortbay.log.Log;

import nl.tucuxi.faceboard.client.FBDataSource;
import nl.tucuxi.faceboard.shared.ImageObject;
import nl.tucuxi.faceboard.shared.RemovalKey;

import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import com.vercer.engine.persist.ObjectDatastore;
import com.vercer.engine.persist.annotation.AnnotationObjectDatastore;

/**
 * @author jeroen.v.gangelen Implementation class for a synchronously called
 *         datastore methods.
 * 
 */
public class FBDataSourceImpl extends RemoteServiceServlet implements
		FBDataSource {

	private static final long serialVersionUID = 1L;

	ObjectDatastore datastore = new AnnotationObjectDatastore();
	// create a complex object graph

	private RemovalKey confirmationcode = new RemovalKey();

	public FBDataSourceImpl() {
		
		// Retrieve confirmationcode object from datastore.
		// The value i.e. the code itself can be changed using the app engine admin by editing the nl_tucuxi_faceboard_shared_RemovalKey value.
		
		String mykey = "ConfirmationKey";
		confirmationcode = datastore.load(RemovalKey.class, mykey);
		if (confirmationcode == null) {
			confirmationcode = new RemovalKey();
			confirmationcode.setDskey(mykey);
			confirmationcode.setValue("DefaultPWG");
			datastore.store(confirmationcode);
		}

	}

	@Override
	public ImageObject[] getImageObjects() {

		Log.info("Getting objects");
		// Query the object graph for all ImageObject classes.
		QueryResultIterator<ImageObject> imageobjects = datastore.find()
				.type(ImageObject.class).returnResultsNow();
		ArrayList<ImageObject> al = new ArrayList<ImageObject>();

		// Add all the retrieved ImageObjects
		while (imageobjects.hasNext()) {
			al.add(imageobjects.next());
		}

		// Return the ImageObject array
		ImageObject[] ret = new ImageObject[al.size()];
		return al.toArray(ret);

	}

	@Override
	public void putImageObject(ImageObject io) {
		Log.info("Putting object with username" + io.getUrl());
		datastore.store(io);

	}

	@Override
	public void updateImageObject(ImageObject io) {
		ImageObject result = datastore.load(ImageObject.class, io.getKey());
		result.setX(io.getCorrectedX());
		result.setY(io.getCorrectedY());
		datastore.update(result);
	}

	@Override
	public boolean removeImageObject(ImageObject io, String confirmationcode) {
		Log.info("Removing object, code valid = "
				+ confirmationcode.equals(this.confirmationcode.getValue()));
		if (confirmationcode.equals(this.confirmationcode.getValue())) {
			ImageObject result = datastore.load(ImageObject.class, io.getKey());
			datastore.delete(result);
			datastore.disassociate(result);
			return true;
		} else {
			return false;
		}

	}

}
