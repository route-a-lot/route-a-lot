package kit.route.a.lot.io;

import kit.route.a.lot.heightinfo.HeightTile;
import kit.route.a.lot.common.Coordinates;
import java.io.File;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.EOFException;




public class SRTMLoader implements HeightLoader {
    
        int count = 0;
        int input;
        int width = 1201;
        int height = 1201;
        int lat = 0;
        int lon = 0;

        HeightTile tile;
        FileInputStream in;
        DataInputStream bin;
        Coordinates origin;

        @Override
        public void load(File file) {
        
            File[] dateien = file.listFiles();
            
            for(int k = 0; k < dateien.length; k++){
                String[] arr = dateien[k].getName().split("");
                Integer hunderter;
                Integer zehner;
                Integer einer;
                /* origin auslesen */
                for(int l = 0; l < arr.length; l++) {
                    if(arr[l].equals("N")) {
                        zehner = Integer.valueOf(arr[l+1]);
                        einer = Integer.valueOf(arr[l+2]);
                        lat = zehner.intValue()*10 + einer.intValue();
                    } else if (arr[l].equals("E")) {
                        hunderter = Integer.valueOf(arr[l+1]);
                        zehner = Integer.valueOf(arr[l+2]);
                        einer = Integer.valueOf(arr[l+3]);
                        lon = hunderter.intValue()*100+zehner.intValue()*10+
                                  einer.intValue();
                    }
                }//end for origin
              origin = new Coordinates((float)lat, (float)lon);
              tile = new HeightTile(width, height, origin);
              
              try {    
                  in = new FileInputStream(dateien[k]);
                  bin = new DataInputStream(in);

                  for(int i = 0; i < height; i++){ 
                      for(int j = 0; j < width; j++){
                          tile.setHeight(i,j,bin.readShort());    
                      }//for width
                  }//for height
                  bin.close();
                  in.close();
              } catch (EOFException eof) {
                  System.out.println(eof);
              } catch (IOException e) {
                  System.out.println(e);
              }
          
      }//end for dateien
    }
}
