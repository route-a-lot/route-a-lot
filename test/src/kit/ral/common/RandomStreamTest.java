package kit.ral.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class RandomStreamTest {

    public static void main (String[] args) {
        try {
            File file = new File("./test.txt");
            file.createNewFile();
            RandomWriteStream stream = new RandomWriteStream(file, new FileOutputStream(file));
            for(int i = 0; i < 20; i++) {
                stream.writeLong(i);
            }
            long mark = stream.getPosition();         
            System.out.println(mark);
            stream.writeChars("abcdefghijklmnop");
            stream.setPosition(mark);
            stream.writeChars("ABC");
            stream.writeChars("DEF");
            stream.setPosition(64);
            stream.writeLongToPosition(12345, 0);
           
            String out = "";
            RandomReadStream stream2 = stream.openForReading();
            stream.close();
            stream = null;
            // = new RandomReadStream(new FileInputStream(file));  
            for(int i = 0; i < 20; i++) {
                out += "" + stream2.readLong() + ", ";
            }
            for(int i = 0; i < 16; i++) {
                out += stream2.readChar();
            }
            stream2.setPosition(64);
            out += ":: " + stream2.readLong();
            out += ":: " + stream2.getPosition();
            System.out.println(out);
        } catch (IOException e) {           
            e.printStackTrace();
        }
    }
    
}
