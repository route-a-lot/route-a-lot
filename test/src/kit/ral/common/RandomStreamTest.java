
/**
Copyright (c) 2012, Josua Stabenow
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
