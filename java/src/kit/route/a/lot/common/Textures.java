package kit.route.a.lot.common;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.BufferUtil;

public class Textures
{	
    public static boolean enabled = false;
    public static void makeRGBTexture(GL gl, BufferedImage img, int target)
    {
      ByteBuffer dest = null;
      switch (img.getType())
      {
        case BufferedImage.TYPE_INT_RGB: // TODO
        {
          int[] data = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
          dest = ByteBuffer.allocateDirect(data.length * BufferUtil.SIZEOF_INT);
          dest.order(ByteOrder.nativeOrder());
          dest.asIntBuffer().put(data, 0, data.length);
          break;
        }
        default:
          throw new RuntimeException("Unsupported image type " + img.getType());
      }
      gl.glTexImage2D(target, 0, GL.GL_RGB, img.getWidth(), img.getHeight(),
              0, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE, dest);
    }

    public static int genTexture(GL gl)
    {
      final int[] tmp = new int[1];
      gl.glGenTextures(1, tmp, 0);
      return tmp[0];
    }
    
    public static void delTexture(GL gl, int tex)
    {
      gl.glDeleteTextures(1, new int[]{tex}, 0);
    }
}
