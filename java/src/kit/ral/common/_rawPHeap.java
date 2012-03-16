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
        for (int i = 0; i < size - 1; i +=2)
            children.set(i / 2, children.get(i).merge(children.get(i+1)));
        int newSize = (size + 1) / 2;
        // size % 2 == 1 => We have (the last) one missing and newSize is one too big
        _rawPHeap<T> result = size % 2 == 1 ? children.get(size - 1) : children.get(newSize - 1);
        for (int i = newSize - 2; i >= 0; i--)
            result = children.get(i).merge(result);
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
