package kit.route.a.lot.common;

public abstract class Context {

    private int detailLevel;

    public Context(int detailLevel) {
        this.detailLevel = detailLevel;
    }

    public int getDetailLevel() {
        return detailLevel;
    }

}
