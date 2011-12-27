package kit.route.a.lot.common;


public class WayInfo {

    private boolean isArea;
    private boolean isBuilding;
    private boolean isStreet;
    private boolean isOther; // interesting to draw but now area, building or
                             // street (for example: barrier, railway)

    private int access;

    /* used in case of isStreet */// possible values:
    private int bicycle; // NO_BICYCLE, BICYCLE
    private int oneway; // NO_ONEWAY, ONEWAY, ONEWAY_OPPOSITE
    private int cycleway; // CYCLEWAY_*
    private int bridge; // BRIDGE
    private int tunnel; // TUNNEL
    private int lanes;
    private int surface;    // SURFACE_*

    private int segregated;

    private int type;

    private int layer;

    private Address address;

    public static final int NO_BICYCLE = 1;
    public static final int BICYCLE = 2;
    public static final int NO_ONEWAY = 1;
    public static final int ONEWAY = 2;
    public static final int ONEWAY_OPPOSITE = 3;
    public static final int BRIDGE = 1;
    public static final int TUNNEL = 1;

    public static final int SEGREGATED = 1;
    public static final int NO_SEGREGATED = 2;

    public static final int ACCESS_PRIVATE = 1;
    public static final int ACCESS_PERMISSIVE = 2;
    public static final int ACCESS_DESTINATION = 3;

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
    
    // types: for explanations see
    // http://wiki.openstreetmap.org/wiki/Map_Features

    public static final int AEROWAY = 1000;

    public static final int AMENITY_BAR = 1050;
    public static final int AMENITY_BBQ = 1051;
    public static final int AMENITY_BIERGARTEN = 1052;
    public static final int AMENITY_CAFE = 1053;
    public static final int AMENITY_DRINKING_WATER = 1054;
    public static final int AMENITY_FAST_FOOD = 1055;
    public static final int AMENITY_FOOD_COURT = 1056;
    public static final int AMENITY_ICE_CREAM = 1057;
    public static final int AMENITY_PUB = 1058;
    public static final int AMENITY_RESTAURANT = 1059;
    public static final int AMENITY_COLLEGE = 1060;
    public static final int AMENITY_KINDERGARTEN = 1061;
    public static final int AMENITY_LIBRARY = 1062;
    public static final int AMENITY_SCHOOL = 1063;
    public static final int AMENITY_UNIVERSITY = 1064;
    public static final int AMENITY_BICYCLE_PARKING = 1065;
    public static final int AMENITY_BICYCLE_RENTAL = 1066;
    public static final int AMENITY_CAR_RENTAL = 1067;
    public static final int AMENITY_CAR_SHARING = 1068;
    public static final int AMENITY_CAR_WASH = 1069;
    public static final int AMENITY_EV_CHARGING = 1070;
    public static final int AMENITY_FERRY_TERMINAL = 1071;
    public static final int AMENITY_FUEL = 1072;
    public static final int AMENITY_GRIT_BIN = 1073;
    public static final int AMENITY_PARKING = 1074;
    public static final int AMENITY_PARKING_ENTRANCE = 1075;
    public static final int AMENITY_PARKING_SPACE = 1076;
    public static final int AMENITY_TAXI = 1077;
    public static final int AMENITY_ATM = 1078;
    public static final int AMENITY_BANK = 1079;
    public static final int AMENITY_BUREAU_DE_CHANGE = 1080;
    public static final int AMENITY_DENTIST = 1081;
    public static final int AMENITY_DOCTORS = 1082;
    public static final int AMENITY_HOSPITAL = 1083;
    public static final int AMENITY_NURSING_HOME = 1084;
    public static final int AMENITY_PHARMACY = 1085;
    public static final int AMENITY_SOCIAL_FACILITY = 1086;
    public static final int AMENITY_VETERINARY = 1087;
    public static final int AMENITY_ARTS_CENTER = 1088;
    public static final int AMENITY_CINEMA = 1089;
    public static final int AMENITY_COMMUNITY_CENTRE = 1090;
    public static final int AMENITY_FOUNTAIN = 1091;
    public static final int AMENITY_NIGHTCLUB = 1092;
    public static final int AMENITY_SOCIAL_CENTRE = 1093;
    public static final int AMENITY_STRIPCLUB = 1094;
    public static final int AMENITY_STUDIO = 1095;
    public static final int AMENITY_THEATRE = 1096;
    public static final int AMENITY_BENCH = 1097;
    public static final int AMENITY_BROTHEL = 1098;
    public static final int AMENITY_CLOCK = 1099;
    public static final int AMENITY_COURTHOUSE = 1110;
    public static final int AMENITY_CREMATORIUM = 1111;
    public static final int AMENITY_EMBASSY = 1112;
    public static final int AMENITY_FIRE_STATION = 1113;
    public static final int AMENITY_GRAVE_YARD = 1114;
    public static final int AMENITY_HUNTING_STAND = 1115;
    public static final int AMENITY_MARKETPLACE = 1116;
    public static final int AMENITY_PLACE_OF_WORSHIP = 1117;
    public static final int AMENITY_POLICE = 1118;
    public static final int AMENITY_POST_BOX = 1119;
    public static final int AMENITY_POST_OFFICE = 1120;
    public static final int AMENITY_PRISON = 1121;
    public static final int AMENITY_PUBLIC_BUILDING = 1122;
    public static final int AMENITY_RECYCLING = 1123;
    public static final int AMENITY_SAUNA = 1124;
    public static final int AMENITY_SHELTER = 1125;
    public static final int AMENITY_TELEPHONE = 1126;
    public static final int AMENITY_TOILETS = 1127;
    public static final int AMENITY_TOWNHALL = 1128;
    public static final int AMENITY_VENDING_MACHINE = 1129;
    public static final int AMENITY_WASTE_BASKET = 1130;
    public static final int AMENITY_WASTE_DISPOSAL = 1131;
    public static final int AMENITY_WATERING_PLACE = 1132;

    public static final int BARRIER = 1150;

    public static final int BUILDING = 1200;

    public static final int CRAFT = 1250;

    public static final int CYCLEWAY_LANE = 1350;
    public static final int CYCLEWAY_OPPOSITE = 1351;
    public static final int CYCLEWAY_OPPOSITE_LANE = 1352;
    public static final int CYCLEWAY_OPPOSITE_TRACK = 1353;
    public static final int CYCLEWAY_TRACK = 1354;

    public static final int EMERGENCY = 1400;

    public static final int GEOLOGICAL = 1150;

    public static final int HIGHWAY_BRIDLEWAY = 1200;
    public static final int HIGHWAY_BYWAY = 1201;
    public static final int HIGHWAY_CYCLEWAY = 1202;
    public static final int HIGHWAY_FOOTWAY = 1203;
    public static final int HIGHWAY_LIVING_STREET = 1204;
    public static final int HIGHWAY_MOTORWAY = 1205; // i.e. Autobahn, ...
    public static final int HIGHWAY_MOTORWAY_LINK = 1206;
    public static final int HIGHWAY_PATH = 1207;
    public static final int HIGHWAY_PEDESTRIAN = 1208;
    public static final int HIGHWAY_PRIMARY = 1209;
    public static final int HIGHWAY_PRIMARY_LINK = 1210;
    public static final int HIGHWAY_RACEWAY = 1211;
    public static final int HIGHWAY_RESIDENTIAL = 1212;
    public static final int HIGHWAY_ROAD = 1213;
    public static final int HIGHWAY_SECONDARY = 1214;
    public static final int HIGHWAY_SECONDARY_LINK = 1215;
    public static final int HIGHWAY_SERVICE = 1216;
    public static final int HIGHWAY_STEPS = 1217;
    public static final int HIGHWAY_STEPS_LARGE = 1218;
    public static final int HIGHWAY_TERTIARY = 1219;
    public static final int HIGHWAY_TERTIARY_LINK = 1220;
    public static final int HIGHWAY_TRACK = 1221;
    public static final int HIGHWAY_TRUNK = 1222;
    public static final int HIGHWAY_TRUNK_LINK = 1223;
    public static final int HIGHWAY_UNCLASSIFIED = 1224;

    public static final int HIGHWAY_GIVE_WAY = 1250;
    public static final int HIGHWAY_MINI_ROUNDABOUT = 1251;
    public static final int HIGHWAY_MOTORWAY_JUNCTION = 1252;
    public static final int HIGHWAY_ROUNDABOUT = 1253;
    public static final int HIGHWAY_STOP = 1254;
    public static final int HIGHWAY_TRAFFIC_SIGNALS = 1255;
    public static final int HIGHWAY_BUS_STOP = 1256;
    public static final int HIGHWAY_CROSSING = 1257;
    public static final int HIGHWAY_EMERGENCY_ACCESS_POINT = 1258;
    public static final int HIGHWAY_FORD = 1259;
    public static final int HIGHWAY_SPEED_CAMERA = 1260;
    public static final int HIGHWAY_SERVICES = 1261;
    public static final int HIGHWAY_TURNING_CIRCLE = 1262;

    public static final int HIGHWAY_IGNORED = 1299;

    public static final int TRAFFIC_CALMING = 1300;

    public static final int SERVICE = 1350;

    public static final int HISTORIC = 1400;

    public static final int LANDUSE_GRASS = 1450;
    public static final int LANDUSE_FOREST = 1451;
    public static final int LANDUSE_RESIDENTIAL = 1452;
    public static final int LANDUSE_FARM = 1453;
    public static final int LANDUSE_FARMLAND = 1454;
    public static final int LANDUSE_MEADOW = 1455;
    public static final int LANDUSE_RESERVOIR = 1456;
    public static final int LANDUSE_INDUSTRIAL = 1457;
    public static final int LANDUSE_FARMYARD = 1458;
    public static final int LANDUSE_CEMETERY = 1459;

    public static final int LEISURE_COMMON = 1550;
    public static final int LEISURE_DANCE = 1551;
    public static final int LEISURE_DOG_PARK = 1552;
    public static final int LEISURE_FISHING = 1553;
    public static final int LEISURE_GARDEN = 1554;
    public static final int LEISURE_GOLF_COURSE = 1555;
    public static final int LEISURE_ICE_RINK = 1556;
    public static final int LEISURE_MARINA = 1557;
    public static final int LEISURE_MINIATURE_GOLF = 1558;
    public static final int LEISURE_NATURE_RESERVE = 1559;
    public static final int LEISURE_PARK = 1560;
    public static final int LEISURE_PITCH = 1561;
    public static final int LEISURE_PLAYGROUND = 1562;
    public static final int LEISURE_SLIPWAY = 1563;
    public static final int LEISURE_SPORTS_CENTRE = 1564;
    public static final int LEISURE_STADIUM = 1565;
    public static final int LEISURE_SWIMMING_POOL = 1566;
    public static final int LEISURE_TRACK = 1567;
    public static final int LEISURE_WATER_PARK = 1568;

    public static final int MAN_MADE = 1600;

    public static final int MILITARY = 1630;

    public static final int NATURAL_BAY = 1650;
    public static final int NATURAL_BEACH = 1651;
    public static final int NATURAL_CLIFF = 1652;
    public static final int NATURAL_COASTLINE = 1653;
    public static final int NATURAL_GLACIER = 1654;
    public static final int NATURAL_HEATH = 1655;
    public static final int NATURAL_LAND = 1656;        // deprecated, but still used
    public static final int NATURAL_MARSH = 1657;        // deprecated, but still used
    public static final int NATURAL_PEAK = 1658;
    public static final int NATURAL_SAND = 1659;
    public static final int NATURAL_SCRUB = 1660;
    public static final int NATURAL_SPRING = 1661;
    public static final int NATURAL_STONE = 1662;
    public static final int NATURAL_TREE = 1663;
    public static final int NATURAL_VOLCANO = 1664;
    public static final int NATURAL_WATER = 16565;
    public static final int NATURAL_WETLAND = 1666;
    public static final int NATURAL_WOOD = 1667;

    public static final int OFFICE = 1680;

    public static final int POWER = 1700;

    public static final int PUBLIC_TRANSPORT = 1750;

    public static final int RAILWAY_RAIL = 1751;
    public static final int RAILWAY_SUBWAY = 1752;
    public static final int RAILWAY_TRAM = 1753;

    public static final int SHOP = 1800;

    public static final int TOURISM = 1900;

    public static final int TRACKTYPE = 1950;

    public static final int WATERWAY_CANAL = 1970;
    public static final int WATERWAY_DAM = 1971;
    public static final int WATERWAY_DITCH = 1972;
    public static final int WATERWAY_DOCK = 1973;
    public static final int WATERWAY_DRAIN = 1974;
    public static final int WATERWAY_RIVER = 1975;
    public static final int WATERWAY_RIVERBANK = 1976;
    public static final int WATERWAY_STREAM = 1977;
    public static final int WATERWAY_WATERFALL = 1978;

    public static final int NON_PHYSICAL = 2000;


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
