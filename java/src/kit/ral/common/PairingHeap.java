package kit.ral.common;

import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;


public class PairingHeap<T> implements Comparable<PairingHeap<T>> {
    
    /*
     * Supports multiple elements with the same key, but no delete() or decreaseKey().
     */
    
    private Comparator<T> comparator;
    private _rawPHeap<T> heap;
    
    public PairingHeap(Comparator<T> comp) {
        if (comp == null) 
            throw new IllegalArgumentException("Please avoid null");
        comparator = comp;
    }
    
    public PairingHeap(T object, Comparator<T> comp) {
        this(comp);
        add(object);
    }

    public boolean addAll(Collection<? extends T> elements) {
        for (T element: elements)
            add(element);
        return true;
    }

    public void clear() {
        heap = null;
    }

    public boolean isEmpty() {
        return (heap == null);
    }

    public int size() {
        //Note: this is slow.
        return heap.getSize();
    }

    public Object[] toArray() {
        // Note: this is very slow.
        int size = size();
        Object[] result = new Object[size];
        for (int i = 0; i < size; i++) {
            result[i] = heap.findMin();
            heap = heap.deleteMin();
        }
        return result;
    }

    public boolean add(T arg0) {
        if (arg0 == null) 
            throw new IllegalArgumentException("Please avoid null");
        heap = isEmpty() ? new _rawPHeap<T>(arg0, comparator) : heap.insert(arg0);
        return true;
    }

    public T element() {
        if (isEmpty())
            throw new NoSuchElementException();
        return heap.findMin();
    }

    public boolean offer(T arg0) {
        return add(arg0);
    }

    public T peek() {
        return isEmpty() ? null : element();
    }

    public T poll() {
        T result = peek();
        heap = heap.deleteMin();
        return result;
    }

    public T remove() {
        if (isEmpty())
            throw new NoSuchElementException();
        return poll();
    }

    @Override
    public int compareTo(PairingHeap<T> o) {
        // Or is it the other way around?
        return (o == null || o.isEmpty() || isEmpty()) ? 0 : comparator.compare(heap.findMin(), o.heap.findMin());
    }
    
    public void merge(PairingHeap<T> b) {
        heap = isEmpty() ? b.heap : heap.merge(b.heap);
    }
    
    public String toString() {
        return isEmpty() ? "[]" : heap.toString();
    }
}
