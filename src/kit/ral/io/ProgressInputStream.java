
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

package kit.ral.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import kit.ral.common.Progress;

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
    
    @Override
    public void close() throws IOException {
        p.finish();
        super.close();
    }
}
