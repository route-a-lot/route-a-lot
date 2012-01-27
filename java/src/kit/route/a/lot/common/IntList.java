package kit.route.a.lot.common;


public class IntList {
    protected int[] data = new int[8];
    protected transient int size = 0;

    public IntList(int[] data) {
        this.data = data;
        size = data.length;
    }
    
    public IntList() {
    }
    
    /** Return an element of the array */
    public int get(int index) throws ArrayIndexOutOfBoundsException {
      if (index >= size) {
        throw new ArrayIndexOutOfBoundsException(index);
      } else {
        return data[index];
      }
    }

    /** Add an int to the array, growing the array if necessary */
    public void add(int x) {
      if (data.length == size) {
        resize(data.length * 2);
      }
      data[size++] = x;
    }

    /** An internal method to change the allocated size of the array */
    protected void resize(int newsize) {
      int[] newdata = new int[newsize];
      System.arraycopy(data, 0, newdata, 0, size); 
      data = newdata;
    }
}
