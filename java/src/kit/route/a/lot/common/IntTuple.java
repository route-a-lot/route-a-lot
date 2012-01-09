package kit.route.a.lot.common;

public class IntTuple {
    // It's a real pain to programm w/o one, consider it a Flyweight or something.
    int first;

    int last;
    
    public IntTuple() { }

    public IntTuple(int first, int last) {
        this.first = first;
        this.last = last;
    }

    public int getFirst() {
        return first;
    }
    
    public void setFirst(int first) {
        this.first = first;
    }

    public int getLast() {
        return last;
    }
    
    public void setLast(int last) {
        this.last = last;
    }
    
}
