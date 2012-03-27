package kit.ral.common.util;

import java.awt.geom.Rectangle2D;

import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;

import org.apache.log4j.Logger;


public class MathUtil {

    private static final float ONE_SIXTH = 1f / 6;
    private static Logger logger = Logger.getLogger(MathUtil.class);

    private MathUtil() {
    }

    // DECLARED FUNCTIONS

    /**
     * Accepts an array of five control heights (evenly distributed)
     * and thereof creates bicubic splines.
     * Returns the value at ratio x in the central spline.
     */
    public static float getSplineValue(float[] values, float x) {

        // create deltas
        float[] deltaY = new float[5];
        for (int i = 0; i < 4; i++) {
            deltaY[i] = values[i + 1] - values[i];
        }       
        // create vectors
        float[] r = new float[5];
        r[0] = 0;
        r[4] = 0;
        for (int i = 1; i < 4; i++) {
            r[i] = 3 * (deltaY[i] - deltaY[i - 1]);
        }
        
        // calculate moments
        float[] delta = new float[5];
        float[] sigma = new float[5];
        // > create vectors
        delta[0] = 2;
        sigma[0] = r[0];
        for (int i = 1; i < 5; i++) {
            delta[i] = 2 - 0.25f / delta[i - 1];
            sigma[i] = r[i] - 0.5f * sigma[i - 1] / delta[i - 1];
        }
        // > calculate moments
        float moment2, moment3;
        float x4 = (sigma[4] / delta[4]);
        moment3 = (sigma[3] - 0.5f * x4) / delta[3];
        moment2 = (sigma[2] - 0.5f * moment3) / delta[2];
        
        // calculate x
        return values[2] + (deltaY[2] - ONE_SIXTH * (2 * moment2 + moment3)) * x
        + 0.5f * moment2 * x * x + ONE_SIXTH * (moment3 - moment2) * x * x * x;
    }
    
    public static int[] permutation(int p, long n) {
        // returns the nth permutation of all integers below p (and above 0).
        if (p < 0) {
            logger.warn("Can't calculate permutations for " + p + ", returning null.");
            return null;
        }
        if (p == 1) {
            return new int[] { 1 };
        } else {
            return insert(permutation(p - 1, n / p), p, (int) n % p);
        }
    }

    public static long fak(int i) {
        if (i <= 1) {
            return 1;
        } else {
            return i * fak(i - 1);
        }
    }

    public static float clip(float value, float bottom, float top) {
        return (value < bottom) ? bottom : (value > top) ? top : value;
    }

    public static int clip(int value, int bottom, int top) {
        return (value < bottom) ? bottom : (value > top) ? top : value;
    }

    public static float interpolate(float bottom, float top, float ratio) {
        return bottom + (top - bottom) * ratio;
    }

    public static boolean isLineInBounds(Coordinates start, Coordinates end, Bounds bounds) {
        Rectangle2D.Float box = new Rectangle2D.Float(
                bounds.getLeft(), bounds.getTop(),
                bounds.getWidth(), bounds.getHeight());
        return box.intersectsLine(start.getLongitude(), start.getLatitude(),
                                  end.getLongitude(), end.getLatitude());
        // TODO pos -> neg (e.g. -180° -> 180°)
    }


    // HELPER FUNCTIONS

    private static int[] insert(int[] array, int element, int pos) {
        int[] result = new int[array.length + 1];
        for (int i = 0; i < pos; i++) {
            result[i] = array[i];
        }
        result[pos] = element;
        for (int i = pos; i < array.length; i++) {
            result[i + 1] = array[i];
        }
        return result;
    }

}
