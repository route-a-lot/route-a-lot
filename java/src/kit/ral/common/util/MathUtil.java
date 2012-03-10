package kit.ral.common.util;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;

import org.apache.log4j.Logger;


public class MathUtil {

    private static Logger logger = Logger.getLogger(MathUtil.class);

    private MathUtil() {
    }

    // DECLARED FUNCTIONS

    public static int[] permutation(int p, long n) {
        // returns the nth permutation of all integers below p (and above 0).
        if (p < 0) {
            logger.warn("Can't calculate parmutations for " + p + ", returning null.");
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
        Line2D.Float edge =
                new Line2D.Float(start.getLongitude(), start.getLatitude(), end.getLongitude(), end.getLatitude());
        // coord.sys. begins in upper left corner
        Rectangle2D.Float box =
                new Rectangle2D.Float(bounds.getLeft(), bounds.getTop(), bounds.getWidth(), bounds.getHeight());
        return box.contains(start.getLongitude(), start.getLatitude())
                || box.contains(end.getLongitude(), end.getLatitude()) || box.intersectsLine(edge);
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
