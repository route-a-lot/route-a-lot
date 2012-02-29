package kit.route.a.lot.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import kit.route.a.lot.common.Progress;

public class ProgressInputStream extends FilterInputStream {

    private static long BYTES_PER_UPDATE = 2000000; // ~2MB
    private long progress = 0, size;
    private Progress p;
    
    public ProgressInputStream(InputStream stream, Progress p, long size) {
        super(stream);
        this.p = p;
        this.size = size;
    }
    
    public int read() throws IOException {
        progress += 4;
        checkProgress();
        return super.read();
    }

    public int read(byte[] b) throws IOException {
        progress += b.length;
        checkProgress();
        return super.read(b);
    }
    
    public int read(byte[] b, int off, int len) throws IOException {
        progress += len;
        checkProgress();
        return super.read(b, off, len);
    }
    
    public long skip(long n) throws IOException {
        progress += n;
        checkProgress();
        return super.skip(n);
    }
    
    private void checkProgress() {
        if (progress > BYTES_PER_UPDATE) {
            p.addProgress(progress / (double) size);
            progress = 0;
        }
    }
}
