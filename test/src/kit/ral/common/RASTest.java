package kit.ral.common;

import java.io.File;
import java.io.IOException;


public class RASTest {

    public static void main (String[] args) {
        try {
            File file = new File("./test.txt");
            file.createNewFile();
            RandomAccessStream stream = new RandomAccessStream(file);
            for(int i = 0; i < 20; i++) {
                stream.writeInt(i);
            }
            stream.setRandomAccess(true);
            long mark = stream.getPosition();         
            System.out.println(mark);
            stream.writeChars("abcdefghijklmnop");
            long mark2 = stream.getPosition();
            stream.setPosition(mark);
            stream.setRandomAccess(false);
            stream.writeChars("ABC");
            stream.setRandomAccess(true);
            stream.writeChars("DEF");
            stream.writeLongAtPosition(12345, 0);
            stream.close();
            stream = null;
            String out = "";
            RandomAccessStream stream2 = new RandomAccessStream(file);  
            for(int i = 0; i < 20; i++) {
                out += "" + stream2.readInt() + ", ";
            }
            for(int i = 0; i < 16; i++) {
                out += stream2.readChar();
            }
            stream2.setRandomAccess(true);
            stream2.setPosition(0);
            stream2.setRandomAccess(false);
            out += ":: " + stream2.readLong();
            
            System.out.println(out);
        } catch (IOException e) {           
            e.printStackTrace();
        }
    }
    
}
