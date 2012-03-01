package kit.route.a.lot.common;

import java.util.List;

import org.apache.log4j.Logger;

public class Util {
    
    private static Logger logger = Logger.getLogger(Util.class);
    private static long timer = 0;
    
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
    
    public static int[] permutation(int p, long n) {
        // returns the nth permutation of all integers below p (and above 0).
        if (p < 0) {
            logger.warn("Can't calculate parmutations for " + p + ", returning null.");
            return null;
        }
        if (p == 1) {
            return new int[] {1};
        } else {
            return insert(permutation(p-1, n / p), p, (int) n % p);
        }
    }
    
    public static long fak(int i) {
        if (i <= 1) {
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
    
    public static float clip(float value, float bottom, float top) {
        return (value < bottom) ? bottom : (value > top) ? top : value;
    }
    
    public static int clip(int value, int bottom, int top) {
        return (value < bottom) ? bottom : (value > top) ? top : value;
    }
    
    public static float interpolate(float bottom, float top, float ratio) {
        return bottom + (top - bottom) * ratio;
    }
    
    public static void startTimer() {
        timer  = System.nanoTime();
    }
    
    public static String stopTimer() {
        return formatNanoSeconds(System.nanoTime() - timer);
    }
    
    public static int RGBToInt(float[] rgb) {
        byte[] argb = {(byte)0xFF, (byte)(rgb[0]*0xFF), (byte)(rgb[1]*0xFF), (byte)(rgb[2]*0xFF)};
        return (argb[0] << 24) + ((argb[1] & 0xFF) << 16) + ((argb[2] & 0xFF) << 8) + (argb[3] & 0xFF);
    }
    
    
    public static String formatSecondsRegular(int seconds) {
        int sec = seconds % 60;
        int min = seconds / 60 % 60;
        int h = seconds / 3600;
        return ((h != 0) ? h + " h " : "") + ((min != 0) ? min + " min" : sec + " s");
    }
    
    public static String formatNanoSeconds(long nanos) {
        String result;
        if (nanos > 60000000000L) {
            result = String.format("%1$1.2f min", nanos / 60000000000d);
        } else if (nanos > 1000000000L) {
            result = String.format("%1$1.2f s", nanos / 1000000000d);
        } else if (nanos > 1000000L) {
            result = String.format("%1$1.2f ms", nanos / 1000000d);
        } else if (nanos > 1000L) {
            result = String.format("%1$1.2f μs", nanos / 1000d);
        } else {
            result = nanos + " ns";
        }   
        return result;
    }
    
    public static String formatSeconds(long dSeconds, boolean exact) {
        int iSeconds = (int) dSeconds;
        int seconds = iSeconds % 60;
        int minutes = iSeconds / 60 % 60;
        int hours = iSeconds / 3600 % 24;
        int days = iSeconds / 86400;
        String sSeconds = seconds + "s";
        if (days == 0 && hours == 0 && minutes == 0) {
            return sSeconds;
        }
        String sMinutes = minutes + "min " + sSeconds;
        if (days == 0 && hours == 0) {
            return sMinutes;
        }
        String sHours = hours + "h " + (exact ? sMinutes : minutes + "min ");
        if (days == 0) {
            return sHours;
        }
        return days + "d " + (exact ? sHours : hours + "h ");
    }
    
    public static  void printMemoryInformation() {
        System.out.println("Total used memory: " + humanReadableByteCount(Runtime.getRuntime().totalMemory(), false));
        System.out.println("Maximal available memory: " + humanReadableByteCount(Runtime.getRuntime().maxMemory(), false));
        System.out.println("Unused memory: " + humanReadableByteCount(Runtime.getRuntime().freeMemory(), false));
    }
    
    // copied from http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
    private static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
    
    /**
     * If list1 and list2 share an element at their end this element is returned.
     * Otherwise null is returned.
     * 
     * @param list1
     * @param list2
     * @return the shared element if existent, null otherwise
     */
    public static <T> T getSharedElementAtEnd(List<T> list1, List<T> list2) {
        for (int i = 0; i < list1.size(); i += list1.size() - 1) {
            for (int j = 0; j < list2.size(); j += list2.size() - 1) {
                if (list1.get(i).equals(list2.get(j))) {
                    return list1.get(i);
                }
            }
        }
        return null;
    }
    
}
