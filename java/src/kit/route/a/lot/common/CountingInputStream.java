package kit.route.a.lot.common;

import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;

public final class CountingInputStream extends FilterInputStream
{

  private long pos = 0;

  private long mark = 0;

  public CountingInputStream(DataInputStream in)
  {
    super(in);
  }

  public synchronized long getPosition()
  {
    return pos;
  }

  @Override
  public synchronized int read()
    throws IOException
  {
    int b = super.read();
    if (b >= 0)
      pos += 1;
    return b;
  }

  @Override
  public synchronized int read(byte[] b, int off, int len)
    throws IOException
  {
    int n = super.read(b, off, len);
    if (n > 0)
      pos += n;
    return n;
  }

  @Override
  public synchronized long skip(long skip) throws IOException
  {
    long n = super.skip(skip);
    if (n > 0)
      pos += n;
    return n;
  }

  @Override
  public synchronized void mark(int readlimit)
  {
    super.mark(readlimit);
    mark = pos;
  }

  @Override
  public synchronized void reset() throws IOException
  {
    if (!markSupported())
      throw new IOException("Mark not supported.");
    super.reset();
    pos = mark;
  }
  
  public float readFloat() throws IOException {
      return ((DataInputStream) this.in).readFloat();
  }
  
  public int readInt() throws IOException {
      return ((DataInputStream) this.in).readInt();  
  }
  
  public boolean readBoolean() throws IOException {
      return ((DataInputStream) this.in).readBoolean();  
  }
  
  public String readUTF() throws IOException {
      return ((DataInputStream) this.in).readUTF(); 
  }
}
