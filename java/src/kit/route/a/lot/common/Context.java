package kit.route.a.lot.common;

public abstract class Context {

    protected int zoomlevel;
    protected Coordinates topLeft;
    protected Coordinates bottomRight;

    public Context(Coordinates topLeft, Coordinates bottomRight, int zoomlevel) {
        this.zoomlevel = zoomlevel;
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
    
    public int getZoomlevel() {
        return zoomlevel;
    }

}
