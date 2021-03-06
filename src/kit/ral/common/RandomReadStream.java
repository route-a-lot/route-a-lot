
/**
Copyright (c) 2012, Matthias Grundmann, Josua Stabenow
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

import java.io.*;
import java.nio.channels.FileChannel;

public class RandomReadStream extends DataInputStream {

    private File file;
    private FileChannel channel;
    
    public RandomReadStream(File file, FileInputStream fileStream) throws IOException {
        super(new ClearableBufferedInputStream(fileStream));
        this.channel = fileStream.getChannel();
        this.file = file;
    }
    
    public File getFile() {
        return file;
    }   
    
    public FileChannel getChannel() {
        return channel;
    }
    
    @Override
    public void close() throws IOException {      
        channel.close();
        super.close();
    }
    
    public long getPosition() throws IOException {
        return channel.position()
                - ((ClearableBufferedInputStream) in).countBufferedBytes();
    }
    
    public void setPosition(long pos) throws IOException {
        ((ClearableBufferedInputStream) in).clear();
        channel.position(pos);
    }

    public RandomReadStream openForReading() throws IOException {
        return new RandomReadStream(file, new FileInputStream(file));
    }
 
}


class ClearableBufferedInputStream extends BufferedInputStream {    
    
    public ClearableBufferedInputStream(InputStream in) {
        super(in);      
    }   
    
    public void clear() {
        count = 0;
        pos = 0;
    }   
    
    public long countBufferedBytes() {
        return count - pos;
    }
}
