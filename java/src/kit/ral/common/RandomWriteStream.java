
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

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class RandomWriteStream extends DataOutputStream {

    private File file;
    private FileChannel channel;
    
    public RandomWriteStream(File file, FileOutputStream fileStream) throws IOException {
        super(new BufferedOutputStream(fileStream));
        this.channel = fileStream.getChannel();
        this.file = file;
    }
    
    public File getFileDescriptor() {
        return file;
    }   
    
    @Override
    public void close() throws IOException {    
        super.close();  
        channel.close();      
    }
    
    public long getPosition() throws IOException {
        flush();
        return channel.position();
    }
    
    public void setPosition(long pos) throws IOException {
        flush();
        channel.position(pos);
    }
    
    public void skipBytes(long n) throws IOException {
        flush();
        channel.position(channel.position() + n);
    }
    
    public void writeLongToPosition(long value, long pos) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocateDirect(8).putLong(value);
        buffer.rewind();
        channel.write(buffer, pos);
    }

    public RandomReadStream openForReading() throws IOException {
        return new RandomReadStream(file, new FileInputStream(file));
    }

}
