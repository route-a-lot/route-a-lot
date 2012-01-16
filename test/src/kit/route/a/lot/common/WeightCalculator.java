//package kit.route.a.lot.common;

import java.lang.Math;
//import kit.route.a.lot.controller.State;
//import kit.route.a.lot.heightinfo.Heightmap;

public class WeightCalculator {
  	private static WeightCalculator instance;
    
    	protected WeightCalculator() { }
    
    	public static WeightCalculator getInstance() {
        	if (instance == null) {
            		instance = new WeightCalculator();
        	}
        	return instance;
    	}

    	/**
     	* Operation calcWeight
     	* 
     	* @param fromID
     	*            -
     	* @param toID
     	*            -
     	* @return int
     	*/
	
	/*
    	public int calcWeightWithHeight(int fromID, int toID) {
        	Coordinates from = State.getInstance().getLoadedMapInfo().getNodePosition(fromID);
        	Coordinates to = State.getInstance().getLoadedMapInfo().getNodePosition(toID);
        	Heightmap heightmap = State.getInstance().getHeightMap();
        	// Pythagoras.
        	return (int) Math.sqrt(
                	Math.pow((from.getLatitude() - to.getLatitude()), 2) + 
                    	Math.pow((from.getLongitude() - to.getLongitude()), 2) + 
                    	Math.pow((State.getInstance().getHeightMalus() * (heightmap.getHeight(from) - heightmap.getHeight(to))), 2));
    }//calcWeightWithHeight
	*/

	/*
    	public int calcWeight(int fromID, int toID) {
        	Coordinates from = State.getInstance().getLoadedMapInfo().getNodePosition(fromID);
        Coordinates to = State.getInstance().getLoadedMapInfo().getNodePosition(toID);
        
        //Kugelkoordinaten berechnet distance in km
        return (int) Math.floor((6378.388 * Math.acos(Math.sin(from.getLatitude()) * Math.sin(to.getLatitude()) + Math.cos(from.getLatitude()) * Math.cos(to.getLatitude()) * Math.cos(to.getLongitude() - from.getLongitude()))));

    }//end CalcWeight
    */
    
	/*

    	public int calcWeight(Selection edge) {
        	return calcWeight(edge.getFrom(), edge.getTo());
    	}//end CalcWeight Selection
	*/
	
	public double calcWeightWithUTM(double lat1, double lon1, double lat2, double lon2){
	
	double entf = 0.0;	
	Converter converter = Converter.getInstance();
	int[] utmDatenStart = converter.utmConverter(lat1, lon1);
	int[] utmDatenEnd = converter.utmConverter(lat2, lon2);
	entf =  Math.sqrt(
                        Math.pow(utmDatenStart[0] - utmDatenEnd[0], 2) + 
                        Math.pow(utmDatenStart[1] - utmDatenEnd[1], 2) );
	System.out.println(entf);
		return entf;
	}// end calcWeightWithUTM

}
