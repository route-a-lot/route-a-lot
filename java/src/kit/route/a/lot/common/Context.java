package kit.route.a.lot.common;

public abstract class Context {

    protected Coordinates topLeft;
    protected Coordinates bottomRight;

    public Context(Coordinates topLeft, Coordinates bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }

    public Coordinates getTopLeft() {
        return topLeft;
    }
    
    public Coordinates getBottomRight() {
        return bottomRight;
    } 
    
    public float getWidth() {
        return bottomRight.getLongitude() - topLeft.getLongitude();
    }
    
    public float getHeight() {
        return bottomRight.getLatitude() - topLeft.getLatitude();
    }

}
