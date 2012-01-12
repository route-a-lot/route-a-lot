package kit.route.a.lot.common;


public class Coordinates {

	private double longitude;
    	private double latitude;
    
    	public Coordinates(double lon, double lat) {
        	this.longitude = lon;
        	this.latitude = lat;
    	}
    
    	public Coordinates() { }
    	//brauchen wir floats Konstruktor
    	public Coordinates(float lat, float lon) {
        	this.latitude = lat;
        	this.longitude = lon;
    	}
   
    	public double getLatitude() {
        	return latitude;
    	}
    
    	public void setLatitude(double latitude) {
        	this.latitude = latitude;
    	}
   
    	public double getLongitude() {
        	return longitude;
    	}
    
    	public void setLongitude(double longitude) {
        	this.longitude = longitude;
    	}
}//end class
