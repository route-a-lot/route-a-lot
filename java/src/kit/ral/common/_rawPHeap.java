package kit.ral.common;

import java.util.ArrayList;
import java.util.Comparator;


public class _rawPHeap<T> {
    /*
     * WARNING:
     *  Don't use this class directly unless you know what you are doing.
     */
    
    private T element;
    private ArrayList<_rawPHeap<T>> children = new ArrayList<_rawPHeap<T>>();
    private Comparator<T> comparator;
    
    public _rawPHeap(T object, Comparator<T> comp) {
        if (object == null || comp == null)
            throw new IllegalArgumentException("Please avoid null");
        element = object;
        comparator = comp;
    }
    
    public _rawPHeap<T> merge(_rawPHeap<T> b) {
        if (b == null)
            return this;
        if (comparator.compare(element, b.element) < 0) {
            children.add(b);
            return this;
        } else {
            b.children.add(this);
            return b;
        }
    }
    
    public T findMin() {
        return element;
    }
    
    public _rawPHeap<T> insert(T elem) {
        return merge(new _rawPHeap<T>(elem, comparator));
    }
    
    public _rawPHeap<T> deleteMin() {
        return mergePairs();
    }
    
    private _rawPHeap<T> mergePairs() {
        int size = children.size();
        if (size == 0)
            return null;
        if (size == 1)
            return children.get(0);
        int newSize = (size + 1) / 2;
        ArrayList<_rawPHeap<T>> temp = new ArrayList<_rawPHeap<T>>(newSize);
        for (int i = 0; i < size - 1; i +=2)
            temp.add(children.get(i).merge(children.get(i+1)));
        if (size % 2 == 1)
            // Don't forget the last one.
            temp.add(children.get(size - 1));
        _rawPHeap<T> result = temp.get(newSize - 1);
        for (int i = newSize - 2; i >= 0; i--)
            result = temp.get(i).merge(result);
        return result;
        //return heaps.remove().merge(heaps.remove()).merge(mergePairs(heaps));
    }

    public int getSize() {
        int size = 1;
        for (_rawPHeap<T> heap: children)
            size += heap.getSize();
        return size;
    }
    
    public String toString() {
        String result =  "[" + element.toString();
        for (_rawPHeap<T> child: children)
            result += child.toString();
        return result + "]";
    }
}
