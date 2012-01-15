package kit.route.a.lot.map.infosupply;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Collection;
import java.util.List;
import java.awt.geom.Rectangle2D;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.MapElement;

public abstract class QuadTree {

    private Coordinates upLeft;
    private Coordinates bottomRight;
        
    public QuadTree(Coordinates upLeft, Coordinates bottomRight) {
        this.upLeft = upLeft;
        this.bottomRight = bottomRight;
    }

    protected abstract Collection<QTLeaf> getLeafs(Coordinates upLeft, Coordinates bottomRight);
    
    protected boolean isInBounds(Coordinates upLeft, Coordinates bottomRight) {
        Rectangle2D.Double thiss = new Rectangle2D.Double(this.upLeft.getLongitude(), this.bottomRight.getLatitude(),    
                this.bottomRight.getLongitude() - this.upLeft.getLongitude(),
                this.upLeft.getLatitude() - this.bottomRight.getLatitude());
        Rectangle2D.Double bounce = new Rectangle2D.Double(upLeft.getLongitude(), bottomRight.getLatitude(),    
                bottomRight.getLongitude() - upLeft.getLongitude(),
                upLeft.getLatitude() - bottomRight.getLatitude());
        return thiss.contains(bounce) || bounce.contains(thiss) || thiss.intersects(bounce);
    }
    
    
    protected void loadFromStream(DataInputStream stream) {
    }

    protected void saveToStream(DataOutputStream stream) {
    }

    protected abstract boolean addToOverlay(MapElement element);

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
    
    public abstract String print(int offset, List<Integer> last);
    public abstract int countElements();
}
