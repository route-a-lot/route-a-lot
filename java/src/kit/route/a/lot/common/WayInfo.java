package kit.route.a.lot.common;


public class WayInfo {
    
    private byte[] data = new byte[11];

//    private byte elementType;
//    
//    private byte access;
//
//    /* used in case of isStreet */// possible values: (TODO)
//    private byte bicycle; // NO_BICYCLE, BICYCLE
//    private byte oneway; // NO_ONEWAY, ONEWAY, ONEWAY_OPPOSITE
//    private byte cycleway; // CYCLEWAY_*
//    private byte bridge; // BRIDGE
//    private byte tunnel; // TUNNEL
//    private byte lanes;
//    private byte surface; // SURFACE_*
//
//    private byte segregated;
//
    private int type;
//
//    private byte layer;

    private Address address;

    private static byte ELEMENT_TYPE_STREET;
    private static byte ELEMENT_TYPE_AREA;
    private static byte ELEMENT_TYPE_BUILDING;
    private static byte ELEMENT_TYPE_OTHER;

    public static final byte BICYCLE_NO = 1;
    public static final byte BICYCLE_YES = 2;
    public static final byte BICYCLE_OFFICIAL = 3;
    public static final byte BICYCLE_DISMOUNT = 4;
    public static final byte BICYCLE_DESTINATION = 5;
    public static final byte ONEWAY_NO = 1;
    public static final byte ONEWAY_YES = 2;
    public static final byte ONEWAY_OPPOSITE = 3;
    public static final byte BRIDGE = 1;
    public static final byte TUNNEL = 1;

    public static final byte SEGREGATED_YES = 1;
    public static final byte SEGREGATED_NO = 2;

    
    public static final byte ACCESS_PRIVATE = 1;
    public static final byte ACCESS_DESTINATION = 3;
    public static final byte ACCESS_YES = 4;
    public static final byte ACCESS_NO = 4;
    public static final byte ACCESS_FORESTRY = 4;
    public static final byte ACCESS_AGRICULTURAL = 4;

    public static enum Access {
        YES, FORESTRY, AGRICULTURAL, PRIVATE, DESTINATION, NO
    }

    public static final byte SURFACE_PAVED = 1;
    public static final byte SURFACE_UNPAVED = 2;
    public static final byte SURFACE_ASPHALT = 3;
    public static final byte SURFACE_GRAVEL = 4;
    public static final byte SURFACE_GROUND = 5;
    public static final byte SURFACE_GRASS = 6;
    public static final byte SURFACE_DIRT = 7;
    public static final byte SURFACE_COBBLESTONE = 8;
    public static final byte SURFACE_PAVING_STONES = 9;
    public static final byte SURFACE_CONCRETE = 10;
    public static final byte SURFACE_SAND = 11;
    public static final byte SURFACE_COMPACTED = 12;

    


    public boolean isArea() {
        return data[0] == ELEMENT_TYPE_AREA;
    }


    public void setArea(boolean isArea) {
        data[0] = ELEMENT_TYPE_AREA;
    }


    public boolean isBuilding() {
        return data[0] == ELEMENT_TYPE_BUILDING;
    }


    public void setBuilding(boolean isBuilding) {
        data[0] = ELEMENT_TYPE_BUILDING;
    }


    public boolean isStreet() {
        return data[0] == ELEMENT_TYPE_STREET;
    }


    public void setStreet(boolean isStreet) {
        data[0] = ELEMENT_TYPE_STREET;
    }


    public boolean isOther() {
        return data[0] == ELEMENT_TYPE_OTHER;
    }


    public void setOther(boolean isOther) {
        data[0] = ELEMENT_TYPE_OTHER;
    }


    public byte getAccess() {
        return data[1];
    }


    public void setAccess(byte access) {
        data[1] = access;
    }


    public byte getBicycle() {
        return data[2];
    }


    public void setBicycle(byte bicycle) {
        data[2] = bicycle;
    }


    public byte getOneway() {
        return data[3];
    }


    public void setOneway(byte oneway) {
        data[3] = oneway;
    }


    public byte getCycleway() {
        return data[4];
    }


    public void setCycleway(byte cycleway) {
        data[4] = cycleway;
    }


    public byte getBridge() {
        return data[5];
    }


    public void setBridge(byte bridge) {
        data[5] = bridge;
    }


    public byte getTunnel() {
        return data[6];
    }


    public void setTunnel(byte tunnel) {
        data[6] = tunnel;
    }


    public byte getLanes() {
        return data[7];
    }


    public void setLanes(byte lanes) {
        data[7] = lanes;
    }


    public byte getSurface() {
        return data[8];
    }


    public void setSurface(byte surface) {
        data[8] = surface;
    }


    public byte getSegregated() {
        return data[9];
    }


    public void setSegregated(byte segregated) {
        data[9] = segregated;
    }


    public int getType() {
        return type;
    }


    public void setType(int type) {
        this.type = type;
    }


    public byte getLayer() {
        return data[10];
    }


    public void setLayer(byte layer) {
        data[10] = layer;
    }


    public Address getAddress() {
        return address;
    }


    public void setAddress(Address address) {
        this.address = address;
    }

}
