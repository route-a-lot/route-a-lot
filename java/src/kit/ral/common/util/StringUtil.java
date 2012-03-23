package kit.ral.common.util;

import java.text.Collator;
import java.util.Locale;


public class StringUtil {

    private static final char[] ALPHABET = new char[] {
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private static final Collator COLLATOR = Collator.getInstance(Locale.GERMAN);
    static {
        COLLATOR.setStrength(Collator.PRIMARY);
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

    // copied from
    // http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    
    /**
     * Normalizes a string, i.e. converts the string to standard 26-characters lower case alphabet.
     */
    public static String normalize(String str) {
        if (str == null) {
            return null;
        }
        
        // divide string
        str = str.replaceAll("ß", "ss").replaceAll("'|\"|\\.", "");
        
        char[] strChars = new char[str.length()];
        str.getChars(0, str.length(), strChars, 0);
        // normalize string
        nextCharacter:
        for (int i = 0; i < strChars.length; i++) {
            for (char letter : ALPHABET) {
                if (COLLATOR.compare(String.valueOf(strChars[i]), String.valueOf(letter)) == 0) {
                    strChars[i] = letter;
                    continue nextCharacter;
                }
            }
            // encode special characters
            strChars[i] = '*';
        }
        // recombine string
        StringBuilder builder = new StringBuilder();
        for (char strChar : strChars) {
            builder.append(strChar);
        }
        return builder.toString();
    }
    
    /**
     * Returns the position of the given letter (A-Z) in the alphabet.
     * This works case-independently. If the given character is not a
     * common letter, 0 is returned.
     * @param ch a character
     * @return a value from range [1..26], or 0 on error
     */
    public static int getCharIndex(char ch) {
        int index = Character.getNumericValue(ch) - 9;
        if (index < 1 || index > 26) {
            index = 0;
        }
        return index;
    }
    
}
