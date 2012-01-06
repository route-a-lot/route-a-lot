package kit.route.a.lot.map.infosupply;

import java.io.InputStream;import java.io.OutputStream;import java.util.Collection; import java.awt.geom.Rectangle2D;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.MapElement;

public abstract class QuadTree {

    /** Attributes */
    /**
     * 
     */
    private Coordinates upLeft;
    /**
     * 
     */
    private Coordinates bottomRight;
    
    
    public QuadTree(Coordinates upLeft, Coordinates bottomRight) {
        this.upLeft = upLeft;
        this.bottomRight = bottomRight;
    }


    /**
     * Operation getLeafs
     * 
     * @param upLeft
     *            -
     * @param bottomRight
     *            -
     * @return Collection<QTLeaf>
     */
    protected abstract Collection<QTLeaf> getLeafs(Coordinates upLeft,
            Coordinates bottomRight);
    
    protected boolean isInBounds(Coordinates upLeft,
            Coordinates bottomRight) {
        Rectangle2D.Float thiss = new Rectangle2D.Float(this.upLeft.getLongitude(), this.bottomRight.getLatitude(),    
                this.bottomRight.getLongitude() - this.upLeft.getLongitude(),
                this.upLeft.getLatitude() - this.bottomRight.getLatitude());
        Rectangle2D.Float bounce = new Rectangle2D.Float(upLeft.getLongitude(), bottomRight.getLatitude(),    
                bottomRight.getLongitude() - upLeft.getLongitude(),
                upLeft.getLatitude() - bottomRight.getLatitude());
        return thiss.contains(bounce) || bounce.contains(thiss) || thiss.intersects(bounce);
    }
    
    

    /**
     * Operation loadFromStream
     * 
     * @param stream
     *            -
     * @return
     * @return
     */
    protected void loadFromStream(InputStream stream) {
    }

    /**
     * Operation saveToStream
     * 
     * @param stream
     *            -
     * @return
     * @return
     */
    protected void saveToStream(OutputStream stream) {
    }

    /**
     * Operation addToOverlay
     * 
     * @param element
     *            -
     * @return
     * @return
     */
    protected abstract boolean addToOverlay(MapElement element);

    /**
     * Operation addToBaseLayer
     * 
     * @param element
     *            -
     * @return
     * @return
     */
    protected abstract boolean addToBaseLayer(MapElement element);


    
    public Coordinates getUpLeft() {
        return upLeft;
    }


    
    public void setUpLeft(Coordinates upLeft) {
        this.upLeft = upLeft;
    }


    
    public Coordinates getBottomRight() {
        return bottomRight;
    }


    
    public void setBottomRight(Coordinates bottomRight) {
        this.bottomRight = bottomRight;
    }
}
