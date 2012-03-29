package kit.ral.common.util;

import java.awt.GraphicsEnvironment;
import java.nio.MappedByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.List;

public class Util {

    private static ArrayDeque<Long> timers = new ArrayDeque<Long>();

    private Util() {
    }

    public static void startTimer() {
        timers.push(System.nanoTime());
    }
    
    /**
     * Returns the current timers value in milliseconds.
     */
    public static long getTimer() {
        return (System.nanoTime() - timers.getFirst());
    }

    public static String stopTimer() {
        return StringUtil.formatNanoSeconds(System.nanoTime() - timers.pop());
    }

    public static int RGBToInt(float[] rgb) {
        byte[] argb = { (byte) 0xFF, (byte) (rgb[0] * 0xFF), (byte) (rgb[1] * 0xFF), (byte) (rgb[2] * 0xFF) };
        return (argb[0] << 24) + ((argb[1] & 0xFF) << 16) + ((argb[2] & 0xFF) << 8) + (argb[3] & 0xFF);
    }

    public static int countNonNullElements(Object[] elements) {
        int size = 0;
        for (Object element : elements) {
            if (element != null) {
                size++;
            }
        }
        return size;
    }

    /**
     * If list1 and list2 share an element at their end this element is returned.
     * @param list1
     * @param list2
     * @return the shared element if existent, null otherwise
     */
    public static <T> T getSharedElementAtEnd(List<T> list1, List<T> list2) {
        if (list1 == null || list2 == null || list1.size() == 0 || list2.size() == 0) {
            return null;
        }
        T element = list1.get(list1.size() - 1);
        return ((element != null) && element.equals(list2.get(list2.size() - 1))) ? element : null;
    }

    public static void printMemoryInformation() {
        System.out.println("Total used memory: "
                + StringUtil.humanReadableByteCount(Runtime.getRuntime().totalMemory(), false));
        System.out.println("Maximal available memory: "
                + StringUtil.humanReadableByteCount(Runtime.getRuntime().maxMemory(), false));
        System.out.println("Unused memory: "
                + StringUtil.humanReadableByteCount(Runtime.getRuntime().freeMemory(), false));
    }
    
    public static void printGPUMemoryInformation() {
        int mem = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().getAvailableAcceleratedMemory();
        System.out.println("Available graphics memory (including accessible RAM): "
                + StringUtil.humanReadableByteCount(mem, false));
    }
    
    public static String readUTFString(MappedByteBuffer mmap) {
        byte[] encodedString = new byte[mmap.getShort()];
        mmap.get(encodedString);
        return new String(encodedString, Charset.forName("UTF-8"));
    }
}