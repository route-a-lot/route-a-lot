package kit.ral.common;

import java.util.Comparator;
import java.util.LinkedList;


public class _rawPHeap<T> {
    /*
     * WARNING:
     *  Don't use this class directly unless you know what you are doing.
     */
    
    private T element;
    private LinkedList<_rawPHeap<T>> children = new LinkedList<_rawPHeap<T>>();
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
        return mergePairs(children);
    }
    
    private _rawPHeap<T> mergePairs(LinkedList<_rawPHeap<T>> heaps) {
        int size = children.size();
        if (size == 0)
            return null;
        if (size == 1)
            return children.get(0);
        // Seems legit
        return heaps.remove().merge(heaps.remove()).merge(mergePairs(heaps));
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
