
/**
Copyright (c) 2012, Jan Jacob, Yvonne Braun, Josua Stabenow
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * The names of the contributors may not be used to endorse or promote products
          derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
**/

package kit.ral.io;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import kit.ral.common.Coordinates;
import kit.ral.heightinfo.HashHeightmap;
import kit.ral.heightinfo.HeightTile;
import kit.ral.heightinfo.RAMHeightTile;


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
    private HashHeightmap heightmap;
    private int width;
    private int height;

    public SRTMLoaderProto(){
        this.width = 1201;
        this.height = 1201;
        this.heightmap = new HashHeightmap();
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
            tile = new RAMHeightTile(width, height, origin);
            
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
    public HashHeightmap getHeightMap(){
        return heightmap;
    }
    
    
}
