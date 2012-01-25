package kit.route.a.lot.common;

import java.awt.Image;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;

import net.java.games.jogl.GL;
import net.java.games.jogl.GLU;
import net.java.games.jogl.util.BufferUtils;

public class Textures
{	
   
    public static void makeRGBTexture(GL gl, GLU glu, BufferedImage img, int target, boolean mipmapped)
    {
      ByteBuffer dest = null;
      switch (img.getType())
      {
        case BufferedImage.TYPE_INT_RGB:
        {
          int[] data = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
          dest = ByteBuffer.allocateDirect(data.length * BufferUtils.SIZEOF_INT);
          dest.order(ByteOrder.nativeOrder());
          dest.asIntBuffer().put(data, 0, data.length);
          break;
        }
        default:
          throw new RuntimeException("Unsupported image type " + img.getType());
      }
      
      if (mipmapped)
      {
        glu.gluBuild2DMipmaps(target, GL.GL_RGB8, img.getWidth(),
                img.getHeight(), GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, dest);
      }
      else
      {
        gl.glTexImage2D(target, 0, GL.GL_RGB, img.getWidth(),
                img.getHeight(), 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, dest);
      }
    }

    public static int genTexture(GL gl)
    {
      final int[] tmp = new int[1];
      gl.glGenTextures(1, tmp);
      return tmp[0];
    }
    
    public static void delTexture(GL gl, int tex)
    {
      gl.glDeleteTextures(1, new int[]{tex});
    }
}
