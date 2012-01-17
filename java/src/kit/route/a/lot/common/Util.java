package kit.route.a.lot.common;


public class Util {
    public int[][] permutations(int p) {
        // Steinhaus–Johnson–Trotter algorithm
        if (p == 0) {
            return new int[][] {{0}};
        }
        int[][] result = new int[fak(p)][p];
        boolean toggle = false;
        int y = 0;
        for (int[] permutation: permutations(p-1)) {
            if (toggle) {
                for (int i = 0; i <= p; i++) {
                    result[y++] = insert(permutation, p, i);
                }
            } else {
                for (int i = p; i >= 0; i--) {
                    result[y++] = insert(permutation, p, i);
                }
            }
            toggle ^= true;
        }
        return result;
    }
    
    public int fak (int i) {
        if (i == 1) {
            return 1;
        } else {
            return i * fak(i-1);
        }
    }
    
    private int[] insert(int[] array, int element, int pos) {
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
