package kit.ral.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Flushable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;


public class RandomAccessStream implements DataInput, DataOutput, Closeable, Flushable {

    private RandomAccessFile raf;
    private DataOutputStream outStream;
    private DataInputStream inStream;
    
    private DataInput in;
    private DataOutput out;
    
    public RandomAccessStream(File file) throws FileNotFoundException {
        raf = new RandomAccessFile(file, "rw");
        outStream = new DataOutputStream(new BufferedOutputStream(
                        Channels.newOutputStream(raf.getChannel())));
        inStream = new DataInputStream(new BufferedInputStream(
                        Channels.newInputStream(raf.getChannel())));
        setRandomAccess(false);
    }
    
    public void setRandomAccess(boolean enable) {
        in = enable ? raf : inStream;
        out = enable ? raf : outStream;
    }

    @Override
    public void write(int v) throws IOException {
        out.write(v);
    }

    @Override
    public void write(byte[] b) throws IOException {
        out.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }

    @Override
    public void writeBoolean(boolean b) throws IOException {
        out.writeBoolean(b);
    }

    @Override
    public void writeByte(int b) throws IOException {
        out.writeByte(b);
    }

    @Override
    public void writeBytes(String s) throws IOException {
        out.writeBytes(s);
    }

    @Override
    public void writeChar(int v) throws IOException {
        out.writeChar(v);
    }

    @Override
    public void writeChars(String s) throws IOException {
        out.writeChars(s);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        out.writeDouble(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        out.writeFloat(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        out.writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        out.writeLong(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        out.writeShort(v);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        out.writeUTF(s);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return in.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return in.readByte();
    }

    @Override
    public char readChar() throws IOException {
        return in.readChar();
    }

    @Override
    public double readDouble() throws IOException {
        return in.readDouble();
    }

    @Override
    public float readFloat() throws IOException {
        return in.readFloat();
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        in.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        in.readFully(b, off, len);
    }

    @Override
    public int readInt() throws IOException {
        return in.readInt();
    }

    @Override
    public String readLine() throws IOException {
        return in.readLine();
    }

    @Override
    public long readLong() throws IOException {
        return in.readLong();
    }

    @Override
    public short readShort() throws IOException {
        return in.readShort();
    }

    @Override
    public String readUTF() throws IOException {
        return in.readUTF();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return in.readUnsignedByte();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return in.readUnsignedShort();
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return in.skipBytes(n);
    }
    
    @Override
    public void close() throws IOException {      
        outStream.close();
        inStream.close();
        raf.close();
    }

    @Override
    public void flush() throws IOException {
        outStream.flush();
    }
    
    public long getPosition() throws IOException {
        return raf.getFilePointer();
    }
    
    public void setPosition(long pos) throws IOException {
        raf.seek(pos);
    }
    
    
    

}
