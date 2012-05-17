
/**
Copyright (c) 2012, Jan Jacob
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
