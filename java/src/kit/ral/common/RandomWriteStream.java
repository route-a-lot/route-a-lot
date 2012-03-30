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
