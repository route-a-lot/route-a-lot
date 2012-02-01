package kit.route.a.lot.common;

import org.apache.log4j.Logger;

public class Util {
    
    private static Logger logger = Logger.getLogger(Util.class);
    
    /*private static int[][] permutations(int p) {
        // Try not to use this
        // Steinhaus–Johnson–Trotter algorithm'
        if (p == 1) {
            return new int[][] {{1}};
        }
        int[][] result = new int[fak(p)][p];
        int y = 0;
        for (int[] permutation: permutations(p-1)) {
            for (int i = 0; i <= p; i++) {
                result[y++] = insert(permutation, p, i);
            }
        }
        return result;
    }*/
    
    public static int[] permutation(int p, int n) {
        // returns the nth permutation of all integers below p (and above 0).
        if (p < 0) {
            logger.warn("Can't calculate parmutations for " + p + ", returning null.");
            return null;
        }
        if (p == 1) {
            return new int[] {1};
        } else {
            return insert(permutation(p-1, n / p), p, n % p);
        }
    }
    
    public static int fak(int i) {
        if (i == 1) {
            return 1;
        } else {
            return i * fak(i-1);
        }
    }
    
    private static int[] insert(int[] array, int element, int pos) {
        int[] result = new int[array.length + 1];
        for (int i = 0; i < pos; i++) {
            result[i] = array[i];
        }
        result[pos] = element;
        for (int i = pos; i < array.length; i++) {
            result[i+1] = array[i];
        }
        return result;
    }

    /**
     * Removes a file extension from a file name.
     * @param s the filename
     * @return the filename without the file extension
     */
    public static String removeExtension(String s) {
        String separator = System.getProperty("file.separator");
        String filename;
        // Remove the path upto the filename.
        int lastSeparatorIndex = s.lastIndexOf(separator);
        if (lastSeparatorIndex == -1) {
            filename = s;
        } else {
            filename = s.substring(lastSeparatorIndex + 1);
        }
        // Remove the extension.
        int extensionIndex = filename.lastIndexOf(".");
        if (extensionIndex == -1)
            return filename;
        return filename.substring(0, extensionIndex);
    }
    
    
    
    
    
    public static void getFaceNormal(float[] norm, float[] pa, float[] pb, float[] pc) {
        float[] v1 = new float[] {pb[0]-pa[0], pb[1]-pa[1], pb[2]-pa[2]};
        float[] v2 = new float[] {pc[0]-pa[0], pc[1]-pa[1], pc[2]-pa[2]};
        crossProduct(norm, v1, v2);
        normalize(norm);
    }
    
    private static void crossProduct(float[] c, float[] a, float[] b) {  
        c[0] = a[1]*b[2] - b[1]*a[2];
        c[1] = a[2]*b[0] - b[2]*a[0];
        c[2] = a[0]*b[1] - b[0]*a[1];
    }
    
    private static void normalize(float[] vec) {
        float length = (float) Math.sqrt(vec[0]*vec[0]+vec[1]*vec[1]+vec[2]*vec[2]);
        for (int a = 0; a < 3; a++) {
            vec[a] /= length;
        }
    }
    
    public static float mapFloat(float value, float bottom, float top, float newbottom, float newtop) {
        value = clip(value, bottom, top);
        return (value - bottom) * (newtop / top) + newbottom;
    }
    
    public static float clip(float value, float bottom, float top) {
        return (value < bottom) ? bottom : (value > top) ? top : value;
    }
    
    public static int clip(int value, int bottom, int top) {
        return (value < bottom) ? bottom : (value > top) ? top : value;
    }
    
    public static double getDistance(Coordinates pos1, Coordinates pos2) {
        return Math.sqrt(Math.pow(pos1.getLatitude() - pos2.getLatitude(), 2)
                       + Math.pow(pos1.getLongitude() - pos2.getLongitude(), 2));
    }


}
