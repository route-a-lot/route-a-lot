package kit.route.a.lot.common;

import kit.route.a.lot.map.Node;


public class Util {
    private static int[][] permutations(int p) {
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
    }
    
    public static int[] permutation(int p, int n) {
        // returns the nth permutation of all integers below p (and above 0).
        if (p == 0) {
            return new int[] {1};
        } else {
            return insert(permutation(p-1, n / p), p, n % p);
        }
    }
    
    public static int fak (int i) {
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
        for (int i = pos; i < result.length; i++) {
            result[i+1] = array[i];
        }
        return result;
    }

}
