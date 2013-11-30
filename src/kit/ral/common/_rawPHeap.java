
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
        return comparator.compare(element, b.element) < 0 ? add(b) : b.add(this);
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
    }
    
    private _rawPHeap<T> add(_rawPHeap<T> b) {
        children.add(b);
        return this;
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
