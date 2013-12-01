
/**
Copyright (c) 2012, Matthias Grundmann, Josua Stabenow
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * The names of the contributors may not be used to endorse or promote products
          derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
**/

package kit.ral.common.util;

import java.awt.*;
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