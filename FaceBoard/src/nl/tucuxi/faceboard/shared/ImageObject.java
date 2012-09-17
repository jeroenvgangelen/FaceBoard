package nl.tucuxi.faceboard.shared;

import com.vercer.engine.persist.annotation.Key;

public class ImageObject implements com.google.gwt.user.client.rpc.IsSerializable {

	private int x;
	private int y;
	private String url;
	@Key public String key;
	
	private static int xcorrection;
	private int x_correction;
	private static int ycorrection;
	private int y_correction;
	
	private static int maxx;
	private int max_x;
	private static int maxy;
	private int max_y;
	
	public static void setCorrections(int x, int y)
	{
		xcorrection = x;
		ycorrection = y;
	}
	
	public static void setFieldSize(int x, int y) {
		maxx=x;
		maxy=y;
		
	}
	
	public void generateKey()
	{
		key=""+System.currentTimeMillis()+Math.random();
	}
	
	public String getKey()
	{
		return key;
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.max_x=maxx;
		this.x_correction=xcorrection;
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.max_y=maxy;
		this.y_correction=ycorrection;
		this.y = y;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public int getCorrectedX()
	{
		int cor_x = getX() - x_correction;
		if (cor_x <0) cor_x=0;
		if (cor_x >max_x) cor_x=max_x;
		return cor_x;
		
	}
	
	public int getCorrectedY()
	{
		int cor_y = getY() - y_correction;
		if (cor_y <0) 
			cor_y=0;
		if (cor_y >max_y) 
			cor_y=max_y;
		return cor_y;
	}



	
}
