package kit.ral.common;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

public class RandomReadStream extends DataInputStream {

    private File file;
    private FileChannel channel;
    
    public RandomReadStream(File file, FileInputStream fileStream) throws IOException {
        super(new ClearableBufferedInputStream(fileStream));
        this.channel = fileStream.getChannel();
        this.file = file;
    }
    
    public File getFileDescriptor() {
        return file;
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
