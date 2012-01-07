package kit.route.a.lot.common;


public class WayInfo {

    private boolean isArea;
    private boolean isBuilding;
    private boolean isStreet;
    private boolean isOther; // interesting to draw but not an area, building or street (for example: barrier, railway)

    private int access;

    /* used in case of isStreet */// possible values: (TODO)
    private int bicycle; // NO_BICYCLE, BICYCLE
    private int oneway; // NO_ONEWAY, ONEWAY, ONEWAY_OPPOSITE
    private int cycleway; // CYCLEWAY_*
    private int bridge; // BRIDGE
    private int tunnel; // TUNNEL
    private int lanes;
    private int surface; // SURFACE_*

    private int segregated;

    private int type;

    private int layer;

    private Address address;

    public static final int BICYCLE_NO = 1;
    public static final int BICYCLE_YES = 2;
    public static final int BICYCLE_OFFICIAL = 3;
    public static final int BICYCLE_DISMOUNT = 4;
    public static final int BICYCLE_DESTINATION = 5;
    public static final int ONEWAY_NO = 1;
    public static final int ONEWAY_YES = 2;
    public static final int ONEWAY_OPPOSITE = 3;
    public static final int BRIDGE = 1;
    public static final int TUNNEL = 1;

    public static final int SEGREGATED_YES = 1;
    public static final int SEGREGATED_NO = 2;

    
    public static final int ACCESS_PRIVATE = 1;
    public static final int ACCESS_DESTINATION = 3;
    public static final int ACCESS_YES = 4;
    public static final int ACCESS_NO = 4;
    public static final int ACCESS_FORESTRY = 4;
    public static final int ACCESS_AGRICULTURAL = 4;

    public static enum Access {
        YES, FORESTRY, AGRICULTURAL, PRIVATE, DESTINATION, NO
    }

    public static final int SURFACE_PAVED = 1;
    public static final int SURFACE_UNPAVED = 2;
    public static final int SURFACE_ASPHALT = 3;
    public static final int SURFACE_GRAVEL = 4;
    public static final int SURFACE_GROUND = 5;
    public static final int SURFACE_GRASS = 6;
    public static final int SURFACE_DIRT = 7;
    public static final int SURFACE_COBBLESTONE = 8;
    public static final int SURFACE_PAVING_STONES = 9;
    public static final int SURFACE_CONCRETE = 10;
    public static final int SURFACE_SAND = 11;
    public static final int SURFACE_COMPACTED = 12;

    


    public boolean isArea() {
        return isArea;
    }


    public void setArea(boolean isArea) {
        this.isArea = isArea;
    }


    public boolean isBuilding() {
        return isBuilding;
    }


    public void setBuilding(boolean isBuilding) {
        this.isBuilding = isBuilding;
    }


    public boolean isStreet() {
        return isStreet;
    }


    public void setStreet(boolean isStreet) {
        this.isStreet = isStreet;
    }


    public boolean isOther() {
        return isOther;
    }


    public void setOther(boolean isOther) {
        this.isOther = isOther;
    }


    public int getAccess() {
        return access;
    }


    public void setAccess(int access) {
        this.access = access;
    }


    public int getBicycle() {
        return bicycle;
    }


    public void setBicycle(int bicycle) {
        this.bicycle = bicycle;
    }


    public int getOneway() {
        return oneway;
    }


    public void setOneway(int oneway) {
        this.oneway = oneway;
    }


    public int getCycleway() {
        return cycleway;
    }


    public void setCycleway(int cycleway) {
        this.cycleway = cycleway;
    }


    public int getBridge() {
        return bridge;
    }


    public void setBridge(int bridge) {
        this.bridge = bridge;
    }


    public int getTunnel() {
        return tunnel;
    }


    public void setTunnel(int tunnel) {
        this.tunnel = tunnel;
    }


    public int getLanes() {
        return lanes;
    }


    public void setLanes(int lanes) {
        this.lanes = lanes;
    }


    public int getSurface() {
        return surface;
    }


    public void setSurface(int surface) {
        this.surface = surface;
    }


    public int getSegregated() {
        return segregated;
    }


    public void setSegregated(int segregated) {
        this.segregated = segregated;
    }


    public int getType() {
        return type;
    }


    public void setType(int type) {
        this.type = type;
    }


    public int getLayer() {
        return layer;
    }


    public void setLayer(int layer) {
        this.layer = layer;
    }


    public Address getAddress() {
        return address;
    }


    public void setAddress(Address address) {
        this.address = address;
    }

}
