package kit.route.a.lot.io;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.heightinfo.HeightTile;
import kit.route.a.lot.heightinfo.Heightmap;


public class SRTMLoaderProto {
    /**
     * Operation load
     * 
     * @param file
     *            -
     * @return
     * @return
     **/
    /*zum testen*/
    private Heightmap heightmap;
    private int width;
    private int height;

    public SRTMLoaderProto(){
        this.width = 1201;
        this.height = 1201;
        this.heightmap = new Heightmap();
    }
    
    //--------------laod-----------------------------------------------
    public void load(File file) {
        
        File[] dateien = file.listFiles();
        HeightTile tile;
        FileInputStream in;
        DataInputStream bin;
        Coordinates origin;
        int lat = 0;
        int lon = 0;

        for(int k = 0; k < dateien.length; k++){
            String[] arr = dateien[k].getName().split("");
            Integer hunderter;
            Integer zehner;
            Integer einer;
            /* origin auslesen */
            for(int l = 0; l < arr.length; l++){
                if(arr[l].equals("N")){
                    zehner = Integer.valueOf(arr[l+1]);
                    einer = Integer.valueOf(arr[l+2]);
                    lat = zehner.intValue()*10 + einer.intValue();
                } else if (arr[l].equals("E")){
                    hunderter = Integer.valueOf(arr[l+1]);
                    zehner = Integer.valueOf(arr[l+2]);
                    einer = Integer.valueOf(arr[l+3]);
                    lon = hunderter.intValue()*100+zehner.intValue()*10+einer.intValue();
                }
            
            }//end for origin
            // System.out.println("lon: " + lon);
            // System.out.println("lat: " + lat);
            origin = new Coordinates((float)lat, (float)lon);
            tile = new HeightTile(width, height, origin);
            
            try{    
                in = new FileInputStream(dateien[k]);
                bin = new DataInputStream(in);

                for(int i = 0; i < height; i++){ 
                    for(int j = 0; j < width; j++){
                        tile.setHeight(i,j,bin.readShort());
                    }//for width
                }//for height
                bin.close();
                in.close();
            } catch (EOFException eof){
                eof.printStackTrace();
            } catch (IOException e){
                // System.out.println(e);
            }
            /*in HeightMap einfügen*/
            heightmap.addHeightTile(tile);
            test(tile);
        }//end for dateien
    }//end load
    
    /*------------Test------------------------------------*/
    private void test(HeightTile tile){
        File datei = new File("test/resources/Daten/tileInhalt.txt");
        String output = "";
        try{
            BufferedWriter out = new BufferedWriter(new FileWriter(datei));
    
            for(int i = 0; i < height; i++){
                for(int j = 0; j < width; j++){
                    output = output +" "+ tile.getHeight(i,j);
                }
                out.write(output);
                out.newLine();
                output = "";
            }
            out.close();            
        } catch(IOException e) {
            // System.out.println("Fehler beim auslesen");
        }
    }//end test
    
    /*-----------TestHeighMap---------------------------------*/
    public Heightmap getHeightMap(){
        return heightmap;
    }
    
    
}
