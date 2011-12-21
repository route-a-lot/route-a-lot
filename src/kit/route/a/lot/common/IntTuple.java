package kit.route.a.lot.common;

public class IntTuple {
    // It's a real pain to programm w/o one, consider it a Flyweight or something.
    int first;
    int last;

    public IntTuple(int first, int last) {
        this.first = first;
        this.last = last;
    }

    public int getFirst() {
        return first;
    }

    public int getLast() {
        return last;
    }
}
