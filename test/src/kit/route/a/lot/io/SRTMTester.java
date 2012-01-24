package kit.route.a.lot.io;

import java.io.File;

import kit.route.a.lot.common.Coordinates;


public class SRTMTester {

    /**
     * @param args
     */
    public static void main(String[] args) {
        SRTMLoaderProto loader = new SRTMLoaderProto();
        File datei = new File("test/resources/Daten");
        loader.load(datei);
        //// System.out.println("test: "+test);    



/*--------------------------Test1-------------------------------------------*/
        Coordinates coordinates = new Coordinates(49.0030802f,8.3672265f);
        // System.out.println("Höhe an der Stelle: lat: "+ coordinates.getLatitude()                       + " lon: " + coordinates.getLongitude()+" : "+
        //                        loader.getHeightMap().getHeight(coordinates) );
/*----------------------------Test2------------------------------------------*/
  coordinates = new Coordinates(49.0055222f,8.4034558f);
        // System.out.println("Höhe an der Stelle: lat: "+ coordinates.getLatitude()                       + " lon: " + coordinates.getLongitude()+" : "+
        //                        loader.getHeightMap().getHeight(coordinates) );
/*-----------------------------Test3------------------------------------------*/
  coordinates = new Coordinates(48.9980814f,8.44314f);
        // System.out.println("Höhe an der Stelle: lat: "+ coordinates.getLatitude()                       + " lon: " + coordinates.getLongitude()+" : "+
        //                        loader.getHeightMap().getHeight(coordinates) );





    }

}
