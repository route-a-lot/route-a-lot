package kit.ral.common.description;


public class OSMType {

    // types: for explanations see
    // http://wiki.openstreetmap.org/wiki/Map_Features

    public static final int AEROWAY = 1000;

    public static final int AMENITY_BAR = 1050,
                            AMENITY_BBQ = 1051,
                            AMENITY_BIERGARTEN = 1052,
                            AMENITY_CAFE = 1053,
                            AMENITY_DRINKING_WATER = 1054,
                            AMENITY_FAST_FOOD = 1055,
                            AMENITY_FOOD_COURT = 1056,
                            AMENITY_ICE_CREAM = 1057,
                            AMENITY_PUB = 1058,
                            AMENITY_RESTAURANT = 1059,
                            AMENITY_COLLEGE = 1060,
                            AMENITY_KINDERGARTEN = 1061,
                            AMENITY_LIBRARY = 1062,
                            AMENITY_SCHOOL = 1063,
                            AMENITY_UNIVERSITY = 1064,
                            AMENITY_BICYCLE_PARKING = 1065,
                            AMENITY_BICYCLE_RENTAL = 1066,
                            AMENITY_CAR_RENTAL = 1067,
                            AMENITY_CAR_SHARING = 1068,
                            AMENITY_CAR_WASH = 1069,
                            AMENITY_EV_CHARGING = 1070,
                            AMENITY_FERRY_TERMINAL = 1071,
                            AMENITY_FUEL = 1072,
                            AMENITY_GRIT_BIN = 1073,
                            AMENITY_PARKING = 1074,
                            AMENITY_PARKING_ENTRANCE = 1075,
                            AMENITY_PARKING_SPACE = 1076,
                            AMENITY_TAXI = 1077,
                            AMENITY_ATM = 1078,
                            AMENITY_BANK = 1079,
                            AMENITY_BUREAU_DE_CHANGE = 1080,
                            AMENITY_DENTIST = 1081,
                            AMENITY_DOCTORS = 1082,
                            AMENITY_HOSPITAL = 1083,
                            AMENITY_NURSING_HOME = 1084,
                            AMENITY_PHARMACY = 1085,
                            AMENITY_SOCIAL_FACILITY = 1086,
                            AMENITY_VETERINARY = 1087,
                            AMENITY_ARTS_CENTRE = 1088,
                            AMENITY_CINEMA = 1089,
                            AMENITY_COMMUNITY_CENTRE = 1090,
                            AMENITY_FOUNTAIN = 1091,
                            AMENITY_NIGHTCLUB = 1092,
                            AMENITY_SOCIAL_CENTRE = 1093,
                            AMENITY_STRIPCLUB = 1094,
                            AMENITY_STUDIO = 1095,
                            AMENITY_THEATRE = 1096,
                            AMENITY_BENCH = 1097,
                            AMENITY_BROTHEL = 1098,
                            AMENITY_CLOCK = 1099,
                            AMENITY_COURTHOUSE = 1110,
                            AMENITY_CREMATORIUM = 1111,
                            AMENITY_EMBASSY = 1112,
                            AMENITY_FIRE_STATION = 1113,
                            AMENITY_GRAVE_YARD = 1114,
                            AMENITY_HUNTING_STAND = 1115,
                            AMENITY_MARKETPLACE = 1116,
                            AMENITY_PLACE_OF_WORSHIP = 1117,
                            AMENITY_POLICE = 1118,
                            AMENITY_POST_BOX = 1119,
                            AMENITY_POST_OFFICE = 1120,
                            AMENITY_PRISON = 1121,
                            AMENITY_PUBLIC_BUILDING = 1122,
                            AMENITY_RECYCLING = 1123,
                            AMENITY_SAUNA = 1124,
                            AMENITY_SHELTER = 1125,
                            AMENITY_TELEPHONE = 1126,
                            AMENITY_TOILETS = 1127,
                            AMENITY_TOWNHALL = 1128,
                            AMENITY_VENDING_MACHINE = 1129,
                            AMENITY_WASTE_BASKET = 1130,
                            AMENITY_WASTE_DISPOSAL = 1131,
                            AMENITY_WATERING_PLACE = 1132;

    public static final int BARRIER = 1150;

    public static final int BUILDING = 1200;

    public static final int CRAFT = 1250;

    public static final int CYCLEWAY_LANE = 1350,
                            CYCLEWAY_OPPOSITE = 1351,
                            CYCLEWAY_OPPOSITE_LANE = 1352,
                            CYCLEWAY_OPPOSITE_TRACK = 1353,
                            CYCLEWAY_TRACK = 1354;

    public static final int EMERGENCY = 1400;

    public static final int GEOLOGICAL = 1150;

    public static final int HIGHWAY_BRIDLEWAY = 1200,
                            HIGHWAY_BYWAY = 1201,
                            HIGHWAY_CYCLEWAY = 1202,
                            HIGHWAY_FOOTWAY = 1203,
                            HIGHWAY_LIVING_STREET = 1204,
                            HIGHWAY_MOTORWAY = 1205, // i.e. Autobahn, ...
                            HIGHWAY_MOTORWAY_LINK = 1206,
                            HIGHWAY_PATH = 1207,
                            HIGHWAY_PEDESTRIAN = 1208,
                            HIGHWAY_PRIMARY = 1209,
                            HIGHWAY_PRIMARY_LINK = 1210,
                            HIGHWAY_RACEWAY = 1211,
                            HIGHWAY_RESIDENTIAL = 1212,
                            HIGHWAY_ROAD = 1213,
                            HIGHWAY_SECONDARY = 1214,
                            HIGHWAY_SECONDARY_LINK = 1215,
                            HIGHWAY_SERVICE = 1216,
                            HIGHWAY_STEPS = 1217,
                            HIGHWAY_STEPS_LARGE = 1218,
                            HIGHWAY_TERTIARY = 1219,
                            HIGHWAY_TERTIARY_LINK = 1220,
                            HIGHWAY_TRACK = 1221,
                            HIGHWAY_TRUNK = 1222,
                            HIGHWAY_TRUNK_LINK = 1223,
                            HIGHWAY_UNCLASSIFIED = 1224,

                            HIGHWAY_GIVE_WAY = 1250,
                            HIGHWAY_MINI_ROUNDABOUT = 1251,
                            HIGHWAY_MOTORWAY_JUNCTION = 1252,
                            HIGHWAY_ROUNDABOUT = 1253,
                            HIGHWAY_STOP = 1254,
                            HIGHWAY_TRAFFIC_SIGNALS = 1255,
                            HIGHWAY_BUS_STOP = 1256,
                            HIGHWAY_CROSSING = 1257,
                            HIGHWAY_EMERGENCY_ACCESS_POINT = 1258,
                            HIGHWAY_FORD = 1259,
                            HIGHWAY_SPEED_CAMERA = 1260,
                            HIGHWAY_SERVICES = 1261,
                            HIGHWAY_TURNING_CIRCLE = 1262;

    public static final int HIGHWAY_IGNORED = 1299;

    public static final int TRAFFIC_CALMING = 1300;

    public static final int SERVICE = 1350;

    public static final int HISTORIC = 1400,
                            HISTORIC_CASTLE = 1406,
                            HISTORIC_MONUMENT = 1410;

    public static final int LANDUSE_GRASS = 1450,
                            LANDUSE_FOREST = 1451,
                            LANDUSE_RESIDENTIAL = 1452,
                            LANDUSE_FARM = 1453,
                            LANDUSE_FARMLAND = 1454,
                            LANDUSE_MEADOW = 1455,
                            LANDUSE_RESERVOIR = 1456,
                            LANDUSE_INDUSTRIAL = 1457,
                            LANDUSE_FARMYARD = 1458,
                            LANDUSE_CEMETERY = 1459;

    public static final int LEISURE_COMMON = 1550,
                            LEISURE_DANCE = 1551,
                            LEISURE_DOG_PARK = 1552,
                            LEISURE_FISHING = 1553,
                            LEISURE_GARDEN = 1554,
                            LEISURE_GOLF_COURSE = 1555,
                            LEISURE_ICE_RINK = 1556,
                            LEISURE_MARINA = 1557,
                            LEISURE_MINIATURE_GOLF = 1558,
                            LEISURE_NATURE_RESERVE = 1559,
                            LEISURE_PARK = 1560,
                            LEISURE_PITCH = 1561,
                            LEISURE_PLAYGROUND = 1562,
                            LEISURE_SLIPWAY = 1563,
                            LEISURE_SPORTS_CENTRE = 1564,
                            LEISURE_STADIUM = 1565,
                            LEISURE_SWIMMING_POOL = 1566,
                            LEISURE_TRACK = 1567,
                            LEISURE_WATER_PARK = 1568;

    public static final int MAN_MADE = 1600;

    public static final int MILITARY = 1630;

    public static final int NATURAL_BAY = 1650,
                            NATURAL_BEACH = 1651,
                            NATURAL_CLIFF = 1652,
                            NATURAL_COASTLINE = 1653,
                            NATURAL_GLACIER = 1654,
                            NATURAL_HEATH = 1655,
                            NATURAL_LAND = 1656, // deprecated, but still used
                            NATURAL_MARSH = 1657, // deprecated, but still used
                            NATURAL_PEAK = 1658,
                            NATURAL_SAND = 1659,
                            NATURAL_SCRUB = 1660,
                            NATURAL_SPRING = 1661,
                            NATURAL_STONE = 1662,
                            NATURAL_TREE = 1663,
                            NATURAL_VOLCANO = 1664,
                            NATURAL_WATER = 16565,
                            NATURAL_WETLAND = 1666,
                            NATURAL_WOOD = 1667;

    public static final int OFFICE = 1680;

    public static final int POWER = 1700;

    public static final int PUBLIC_TRANSPORT = 1750;

    public static final int RAILWAY_LEVEL_CROSSING = 1754,
                            RAILWAY_LIGHT_RAIL = 1755,
                            RAILWAY_RAIL = 1756,
                            RAILWAY_SUBWAY = 1762,
                            RAILWAY_TRAM = 1765;

    public static final int SHOP_ALCOHOL = 1801,
                            SHOP_ANIME = 1802,
                            SHOP_ART = 1803,
                            SHOP_BABY_GOODS = 1804,
                            SHOP_BAKERY = 1805,
                            SHOP_BATHROOM_FURNISHING = 1806,
                            SHOP_BEAUTY = 1807,
                            SHOP_BED = 1808,
                            SHOP_BEVERAGES = 1809,
                            SHOP_BICYCLE = 1810,
                            SHOP_BOOKS = 1811,
                            SHOP_BOUTIQUE = 1812,
                            SHOP_BUTCHER = 1813,
                            SHOP_CAR = 1814,
                            SHOP_CAR_REPAIR = 1815,
                            SHOP_CAR_PARTS = 18016,
                            SHOP_CARPET = 1817,
                            SHOP_CHARITY = 1818,
                            SHOP_CHEMIST = 1819,
                            SHOP_CLOTHES = 1820,
                            SHOP_COMPUTER = 1821,
                            SHOP_CONFECTIONARY = 1822,
                            SHOP_CONVENIENCE = 1823,
                            SHOP_COPYSHOP = 1824,
                            SHOP_CURTAIN = 1825,
                            SHOP_DELI = 1826,
                            SHOP_DEPARTMENT_STORE = 1827,
                            SHOP_DIVE = 1828,
                            SHOP_DRY_CLEANING = 1829,
                            SHOP_DO_IT_YOURSELF = 1830,
                            SHOP_ELECTRONICS = 1831,
                            SHOP_EROTIC = 1832,
                            SHOP_FABRIC = 1833,
                            SHOP_FARM = 1834,
                            SHOP_FISH = 1835,
                            SHOP_FLORIST = 1836,
                            SHOP_FRAME = 1837,
                            SHOP_FURNACE = 1838,
                            SHOP_FUNERAL_DIRECTORS = 1839,
                            SHOP_FURNITURE = 1840,
                            SHOP_GARDEN_CENTRE = 1841,
                            SHOP_GAS = 1842,
                            SHOP_GENERAL = 1843,
                            SHOP_GIFT = 1844,
                            SHOP_GLACERY = 1845,
                            SHOP_GREENGROCER = 1846,
                            SHOP_HAIRDRESSER = 1847,
                            SHOP_HARDWARE = 1848,
                            SHOP_HEARING_AIDS = 1849,
                            SHOP_HERBALIST = 1850,
                            SHOP_HIFI = 1851,
                            SHOP_HUNTING = 1852,
                            SHOP_INTERIOR_DECORATION = 1853,
                            SHOP_JEWELRY = 1854,
                            SHOP_KIOSK = 1855,
                            SHOP_KITCHEN = 1856,
                            SHOP_LAUNDRY = 1857,
                            SHOP_MALL = 1858,
                            SHOP_MASSAGE = 1859,
                            SHOP_MOBILE_PHONE = 1860,
                            SHOP_MONEY_LENDER = 1861,
                            SHOP_MOTORCYCLE = 1862,
                            SHOP_MUSICAL_INSTRUMENT = 1863,
                            SHOP_NEWSAGENT = 1864,
                            SHOP_OPTICIAN = 1865,
                            SHOP_ORGANIC = 1866,
                            SHOP_OUTDOOR = 1867,
                            SHOP_PAINT = 1868,
                            SHOP_PAWNBROKER = 1869,
                            SHOP_PET = 1870,
                            SHOP_RADIOTECHNICS = 1871,
                            SHOP_SEAFOOD = SHOP_FISH,
                            SHOP_SECOND_HAND = 1872,
                            SHOP_SHOES = 1873,
                            SHOP_SPORTS = 1874,
                            SHOP_STATIONERY = 1875,
                            SHOP_SUPERMARKET = 1876,
                            SHOP_TATTOO = 1877,
                            SHOP_TOBACCO = 1878,
                            SHOP_TOYS = 1879,
                            SHOP_TRADE = 1880,
                            SHOP_VACANT = 1881,
                            SHOP_VACUUM_CLEANER = 1882,
                            SHOP_VARIETY_STORE = 1883,
                            SHOP_VIDEO = 1884,
                            SHOP_WINDOW_BLIND = 1885;

    public static final int TOURISM = 1900;

    public static final int TRACKTYPE = 1950;

    public static final int WATERWAY_CANAL = 1970,
                            WATERWAY_DAM = 1971,
                            WATERWAY_DITCH = 1972,
                            WATERWAY_DOCK = 1973,
                            WATERWAY_DRAIN = 1974,
                            WATERWAY_RIVER = 1975,
                            WATERWAY_RIVERBANK = 1976,
                            WATERWAY_STREAM = 1977,
                            WATERWAY_WATERFALL = 1978;

    public static final int NON_PHYSICAL = 2000;
    
    public static final int UNKNOWN_TYPE = 0;
    public static final int FAVOURITE = 4;
    
    
    public static int getWaterwayType(String value) {
        int curType = UNKNOWN_TYPE;
        if (value.equals("canal")) {
            curType = WATERWAY_CANAL;
        } else if (value.equals("dam")) {
            curType = WATERWAY_DAM;
        } else if (value.equals("ditch")) {
            curType = WATERWAY_DITCH;
        } else if (value.equals("dock")) {
            curType = WATERWAY_DOCK;
        } else if (value.equals("drain")) {
            curType = WATERWAY_DRAIN;
        } else if (value.equals("river")) {
            curType = WATERWAY_RIVER;
        } else if (value.equals("riverbank")) {
            curType = WATERWAY_RIVERBANK;
        } else if (value.equals("stream")) {
            curType = WATERWAY_STREAM;
        } else if (value.equals("waterfall")) {
            curType = WATERWAY_WATERFALL;
        }
        return curType;
    }
    
    public static int getNaturalType(String value) {
        int curType = UNKNOWN_TYPE;
        if (value.equals("bay")) {
            curType = NATURAL_BAY;
        } else if (value.equals("beach")) {
            curType = NATURAL_BEACH;
        } else if (value.equals("cliff")) {
            curType = NATURAL_CLIFF;
        } else if (value.equals("coastline")) {
            curType = NATURAL_COASTLINE;
        } else if (value.equals("glacier")) {
            curType = NATURAL_GLACIER;
        } else if (value.equals("heath")) {
            curType = NATURAL_HEATH;
        } else if (value.equals("land")) {
            curType = NATURAL_LAND;
        } else if (value.equals("marsh")) {
            curType = NATURAL_MARSH;
        } else if (value.equals("peak")) {
            curType = NATURAL_PEAK;
        } else if (value.equals("sand")) {
            curType = NATURAL_SAND;
        } else if (value.equals("scrub")) {
            curType = NATURAL_SCRUB;
        } else if (value.equals("spring")) {
            curType = NATURAL_SPRING;
        } else if (value.equals("stone")) {
            curType = NATURAL_STONE;
        } else if (value.equals("tree")) {
            curType = NATURAL_TREE;
        } else if (value.equals("volcano")) {
            curType = NATURAL_VOLCANO;
        } else if (value.equals("water")) {
            curType = NATURAL_WATER;
        } else if (value.equals("wetland")) {
            curType = NATURAL_WETLAND;
        } else if (value.equals("wood")) {
            curType = NATURAL_WOOD;
        }
        return curType;
    }
    
    public static int getLanduseType(String value) {
        int curType = UNKNOWN_TYPE;
        if (value.equals("forest")) {
            curType = LANDUSE_FOREST;
        } else if (value.equals("grass")) {
            curType = LANDUSE_GRASS;
        } else if (value.equals("residential")) {
            curType = LANDUSE_RESIDENTIAL;
        } else if (value.equals("farm")) {
            curType = LANDUSE_FARM;
        } else if (value.equals("farmland")) {
            curType = LANDUSE_FARMLAND;
        } else if (value.equals("meadow")) {
            curType = LANDUSE_MEADOW;
        } else if (value.equals("reservoir")) {
            curType = LANDUSE_RESERVOIR;
        } else if (value.equals("industrial")) {
            curType = LANDUSE_INDUSTRIAL;
        } else if (value.equals("farmyard")) {
            curType = LANDUSE_FARMYARD;
        } else if (value.equals("cemetery")) {
            curType = LANDUSE_CEMETERY;
        }
        return curType;
    }
    
    public static int getHighwayType(String value) {
        int curType = HIGHWAY_IGNORED;
        if (value.equals("bridleway")) {
            curType = HIGHWAY_BRIDLEWAY;
        } else if (value.equals("crossing")) {
            curType = HIGHWAY_CROSSING;
        } else if (value.equals("cycleway")) {
            curType = HIGHWAY_CYCLEWAY;
        } else if (value.equals("footway")) {
            curType = HIGHWAY_FOOTWAY;
        } else if (value.equals("living_street")) {
            curType = HIGHWAY_LIVING_STREET;
        } else if (value.equals("motorway")) {
            curType = HIGHWAY_MOTORWAY;
        } else if (value.equals("motorway_link")) {
            curType = HIGHWAY_MOTORWAY_LINK;
        } else if (value.equals("path")) {
            curType = HIGHWAY_PATH;
        } else if (value.equals("pedestrian")) {
            curType = HIGHWAY_PEDESTRIAN;
        } else if (value.equals("primary")) {
            curType = HIGHWAY_PRIMARY;
        } else if (value.equals("primary_link")) {
            curType = HIGHWAY_PRIMARY_LINK;
        } else if (value.equals("residential")) {
            curType = HIGHWAY_RESIDENTIAL;
        } else if (value.equals("secondary")) {
            curType = HIGHWAY_SECONDARY;
        } else if (value.equals("secondary_link")) {
            curType = HIGHWAY_SECONDARY_LINK;
        } else if (value.equals("service")) {
            curType = HIGHWAY_SERVICE;
        } else if (value.equals("steps")) {
            curType = HIGHWAY_STEPS;
        } else if (value.equals("steps_large")) {
            curType = HIGHWAY_STEPS_LARGE;
        } else if (value.equals("tertiary")) {
            curType = HIGHWAY_TERTIARY;
        } else if (value.equals("track")) {
            curType = HIGHWAY_TRACK;
        } else if (value.equals("trunk")) {
            curType = HIGHWAY_TRUNK;
        } else if (value.equals("trunk_link")) {
            curType = HIGHWAY_TRUNK_LINK;
        } else if (value.equals("unclassified")) {
            curType = HIGHWAY_UNCLASSIFIED;
        }
        return curType;
    }
    
    public static int getCyclewayType(String value) {
        int curType = UNKNOWN_TYPE;
        if (value.equals("track")) {
            curType = CYCLEWAY_TRACK;
        } else if (value.equals("lane")) {
            curType = CYCLEWAY_LANE;
        } else if (value.equals("opposite")) {
            curType = CYCLEWAY_OPPOSITE;
        } else if (value.equals("opposite_lane")) {
            curType = CYCLEWAY_OPPOSITE_LANE;
        } else if (value.equals("opposite_track")) {
            curType = CYCLEWAY_OPPOSITE_TRACK;
        }
        return curType;
    }
    
    public static int getRailwayType(String value) {
        int curType = UNKNOWN_TYPE;
        if (value.equals("light_rail")) {
            curType = RAILWAY_LIGHT_RAIL;
        } else if (value.equals("rail")) {
            curType = RAILWAY_RAIL;
        } else if (value.equals("subway")) {
            curType = RAILWAY_SUBWAY;
        } else if (value.equals("tram")) {
            curType = RAILWAY_TRAM;
        }
        return curType;
    }
    
    public static int getAmenityType(String value) {
        int curType = UNKNOWN_TYPE;
        if (value.equals("arts_centre")) {
            curType = AMENITY_ARTS_CENTRE;
        } else if (value.equals("atm")) {
            curType = AMENITY_ATM;
        } else if (value.equals("bank")) {
            curType = AMENITY_BANK;
        } else if (value.equals("bar")) {
            curType = AMENITY_BAR;
        } else if (value.equals("bbq")) {
            curType = AMENITY_BBQ;
        } else if (value.equals("bench")) {
            curType = AMENITY_BENCH;
        } else if (value.equals("bicycle_parking")) {
            curType = AMENITY_BICYCLE_RENTAL;
        } else if (value.equals("bicycle_rental")) {
            curType = AMENITY_BICYCLE_RENTAL;
        } else if (value.equals("biergarten")) {
            curType = AMENITY_BIERGARTEN;
        } else if (value.equals("brothel")) {
            curType = AMENITY_BROTHEL;
        } else if (value.equals("bureau_de_change")) {
            curType = AMENITY_BUREAU_DE_CHANGE;
        } else if (value.equals("cafe")) {
            curType = AMENITY_CAFE;
        } else if (value.equals("car_rental")) {
            curType = AMENITY_CAR_RENTAL;
        } else if (value.equals("car_sharing")) {
            curType = AMENITY_CAR_SHARING;
        } else if (value.equals("car_wash")) {
            curType = AMENITY_CAR_WASH;
        } else if (value.equals("cinema")) {
            curType = AMENITY_CINEMA;
        } else if (value.equals("clock")) {
            curType = AMENITY_CLOCK;
        } else if (value.equals("college")) {
            curType = AMENITY_COLLEGE;
        } else if (value.equals("community_centre")) {
            curType = AMENITY_COMMUNITY_CENTRE;
        } else if (value.equals("courthouse")) {
            curType = AMENITY_COURTHOUSE;
        } else if (value.equals("crematorium")) {
            curType = AMENITY_CREMATORIUM;
        } else if (value.equals("dentist")) {
            curType = AMENITY_DENTIST;
        } else if (value.equals("doctors")) {
            curType = AMENITY_DOCTORS;
        } else if (value.equals("drinking_water")) {
            curType = AMENITY_DRINKING_WATER;
        } else if (value.equals("embassy")) {
            curType = AMENITY_EMBASSY;
        } else if (value.equals("ev_charging")) {
            curType = AMENITY_EV_CHARGING;
        } else if (value.equals("fast_food")) {
            curType = AMENITY_FAST_FOOD;
        } else if (value.equals("ferry_terminal")) {
            curType = AMENITY_FERRY_TERMINAL;
        } else if (value.equals("fire_station")) {
            curType = AMENITY_FIRE_STATION;
        } else if (value.equals("food_court")) {
            curType = AMENITY_FOOD_COURT;
        } else if (value.equals("fountain")) {
            curType = AMENITY_FOUNTAIN;
        } else if (value.equals("fuel")) {
            curType = AMENITY_FUEL;
        } else if (value.equals("grave_yard")) {
            curType = AMENITY_GRAVE_YARD;
        } else if (value.equals("grit_bin")) {
            curType = AMENITY_GRIT_BIN;
        } else if (value.equals("hospital")) {
            curType = AMENITY_HOSPITAL;
        } else if (value.equals("hunting_stand")) {
            curType = AMENITY_HUNTING_STAND;
        } else if (value.equals("ice_cream")) {
            curType = AMENITY_ICE_CREAM;
        } else if (value.equals("kindergarten")) {
            curType = AMENITY_KINDERGARTEN;
        } else if (value.equals("library")) {
            curType = AMENITY_LIBRARY;
        } else if (value.equals("marketplace")) {
            curType = AMENITY_MARKETPLACE;
        } else if (value.equals("nightclub")) {
            curType = AMENITY_NIGHTCLUB;
        } else if (value.equals("nursing_home")) {
            curType = AMENITY_NURSING_HOME;
        } else if (value.equals("parking")) {
            curType = AMENITY_PARKING;
        } else if (value.equals("parking_entrance")) {
            curType = AMENITY_PARKING_ENTRANCE;
        } else if (value.equals("parking_space")) {
            curType = AMENITY_PARKING_SPACE;
        } else if (value.equals("pharmacy")) {
            curType = AMENITY_PHARMACY;
        } else if (value.equals("place_of_worship")) {
            curType = AMENITY_PLACE_OF_WORSHIP;
        } else if (value.equals("police")) {
            curType = AMENITY_POLICE;
        } else if (value.equals("post_box")) {
            curType = AMENITY_POST_BOX;
        } else if (value.equals("post_office")) {
            curType = AMENITY_POST_OFFICE;
        } else if (value.equals("prison")) {
            curType = AMENITY_PRISON;
        } else if (value.equals("pub")) {
            curType = AMENITY_PUB;
        } else if (value.equals("public_building")) {
            curType = AMENITY_PUBLIC_BUILDING;
        } else if (value.equals("recycling")) {
            curType = AMENITY_RECYCLING;
        } else if (value.equals("restaurant")) {
            curType = AMENITY_RESTAURANT;
        } else if (value.equals("sauna")) {
            curType = AMENITY_SAUNA;
        } else if (value.equals("school")) {
            curType = AMENITY_SCHOOL;
        } else if (value.equals("shelter")) {
            curType = AMENITY_SHELTER;
        } else if (value.equals("social_centre")) {
            curType = AMENITY_SOCIAL_CENTRE;
        } else if (value.equals("social_facility")) {
            curType = AMENITY_SOCIAL_FACILITY;
        } else if (value.equals("stripclub")) {
            curType = AMENITY_STRIPCLUB;
        } else if (value.equals("studio")) {
            curType = AMENITY_STUDIO;
        } else if (value.equals("taxi")) {
            curType = AMENITY_TAXI;
        } else if (value.equals("telephone")) {
            curType = AMENITY_TELEPHONE;
        } else if (value.equals("theatre")) {
            curType = AMENITY_THEATRE;
        } else if (value.equals("toilets")) {
            curType = AMENITY_TOILETS;
        } else if (value.equals("townhall")) {
            curType = AMENITY_TOWNHALL;
        } else if (value.equals("university")) {
            curType = AMENITY_UNIVERSITY;
        } else if (value.equals("vending_machine")) {
            curType = AMENITY_VENDING_MACHINE;
        } else if (value.equals("veterinary")) {
            curType = AMENITY_VETERINARY;
        } else if (value.equals("waste_basket")) {
            curType = AMENITY_WASTE_BASKET;
        } else if (value.equals("waste_disposal")) {
            curType = AMENITY_WASTE_DISPOSAL;
        } else if (value.equals("watering_place")) {
            curType = AMENITY_WATERING_PLACE;
        }
        return curType;
    }

    public static int getShopType(String value) {
        int curType = UNKNOWN_TYPE;
        if (value.equals("alcohol")) {
            curType = SHOP_ALCOHOL;
        } else if (value.equals("bakery")) {
            curType = SHOP_BAKERY;
        } else if (value.equals("beverages")) {
            curType = SHOP_BEVERAGES;
        } else if (value.equals("bicycle")) {
            curType = SHOP_BICYCLE;
        } else if (value.equals("books")) {
            curType = SHOP_BOOKS;
        } else if (value.equals("butcher")) {
            curType = SHOP_BUTCHER;
        } else if (value.equals("car")) {
            curType = SHOP_CAR;
        } else if (value.equals("car_repair")) {
            curType = SHOP_CAR_REPAIR;
        } else if (value.equals("chemist")) {
            curType = SHOP_CHEMIST;
        } else if (value.equals("clothes")) {
            curType = SHOP_CLOTHES;
        } else if (value.equals("computer")) {
            curType = SHOP_COMPUTER;
        } else if (value.equals("convenience")) {
            curType = SHOP_CONVENIENCE;
        } else if (value.equals("copyshop")) {
            curType = SHOP_COPYSHOP;
        } else if (value.equals("doityourself")) {
            curType = SHOP_DO_IT_YOURSELF;
        } else if (value.equals("drugstore")) {
            curType = SHOP_CHEMIST;
        } else if (value.equals("dry_cleaning")) {
            curType = SHOP_DRY_CLEANING;
        } else if (value.equals("furniture")) {
            curType = SHOP_FURNITURE;
        } else if (value.equals("hairdresser")) {
            curType = SHOP_HAIRDRESSER;
        } else if (value.equals("kiosk")) {
            curType = SHOP_KIOSK;
        } else if (value.equals("mall")) {
            curType = SHOP_MALL;
        } else if (value.equals("motorcycle")) {
            curType = SHOP_MOTORCYCLE;
        } else if (value.equals("music")) {
            curType = SHOP_MUSICAL_INSTRUMENT;
        } else if (value.equals("optician")) {
            curType = SHOP_OPTICIAN;
        } else if (value.equals("organic")) {
            curType = SHOP_ORGANIC;
        } else if (value.equals("outdoor")) {
            curType = SHOP_OUTDOOR;
        } else if (value.equals("print")) {
            curType = SHOP_ANIME;
        } else if (value.equals("supermarket")) {
            curType = SHOP_SUPERMARKET;
        } else if (value.equals("toys")) {
            curType = SHOP_TOYS;
        } else if (value.equals("video")) {
            curType = SHOP_VIDEO;
        }
        return curType;
    }
    
    public static int getLeisureType(String value) {
        int curType = UNKNOWN_TYPE;
        if (value.equals("garden")) {
            curType = LEISURE_GARDEN;
        } else if (value.equals("pitch")) {
            curType = LEISURE_PITCH;
        } else if (value.equals("park")) {
            curType = LEISURE_PARK;
        } else if (value.equals("playground")) {
            curType = LEISURE_PLAYGROUND;
        } else if (value.equals("sports_centre")) {
            curType = LEISURE_SPORTS_CENTRE;
        } else if (value.equals("stadium")) {
            curType = LEISURE_STADIUM;
        } else if (value.equals("track")) {
            curType = LEISURE_TRACK;
        }
        return curType;
    }
    
    public static byte getAccessType(String value) {
        byte curType = UNKNOWN_TYPE;
        if (value.equals("private")) {
            curType = WayInfo.ACCESS_PRIVATE;
        } else if (value.equals("destination")
                || value.equals("customers")) {
            curType = WayInfo.ACCESS_DESTINATION;
        } else if (value.equals("yes")
                || value.equals("permissive")
                || value.equals("access")
                || value.equals("public")) {
            curType = WayInfo.ACCESS_YES;
        } else if (value.equals("forestry")) {
            curType = WayInfo.ACCESS_FORESTRY;
        } else if (value.equals("agricultural")) {
            curType =  WayInfo.ACCESS_AGRICULTURAL;
        } else if (value.equals("no")) {
            curType =  WayInfo.ACCESS_NO;
        }
        return curType;
    }
    
    public static byte getSurfaceType(String value) {
        byte curType = UNKNOWN_TYPE;
        if (value.equals("paved")) {
            curType = WayInfo.SURFACE_PAVED;
        } else if (value.equals("unpaved")) {
            curType = WayInfo.SURFACE_UNPAVED;
        } else if (value.equals("asphalt")) {
            curType = WayInfo.SURFACE_ASPHALT;
        } else if (value.equals("gravel")) {
            curType = WayInfo.SURFACE_GRAVEL;
        } else if (value.equals("ground")) {
            curType = WayInfo.SURFACE_GROUND;
        } else if (value.equals("grass")) {
            curType = WayInfo.SURFACE_GRASS;
        } else if (value.equals("dirt")) {
            curType = WayInfo.SURFACE_DIRT;
        } else if (value.equals("cobblestone")) {
            curType = WayInfo.SURFACE_COBBLESTONE;
        } else if (value.equals("paving_stones")) {
            curType = WayInfo.SURFACE_PAVING_STONES;
        } else if (value.equals("concrete")) {
            curType = WayInfo.SURFACE_CONCRETE;
        } else if (value.equals("sand")) {
            curType = WayInfo.SURFACE_SAND;
        } else if (value.equals("compacted")) {
            curType = WayInfo.SURFACE_COMPACTED;
        }
        return curType;
    }

    public static byte getBicycleRestrictions(String value) {
        byte prop = UNKNOWN_TYPE;
        if (value.equals("yes") || value.equals("designated")
                || value.equals("permissive")) {
            prop = WayInfo.BICYCLE_YES;
        } else if (value.equals("no") || value.equals("private")) {
            prop = WayInfo.BICYCLE_NO;
        } else if (value.equals("official")) {
            prop = WayInfo.BICYCLE_OFFICIAL;
        } else if (value.equals("dismount")) {
            prop = WayInfo.BICYCLE_DISMOUNT;
        } else if (value.equals("destination")) {
            prop = WayInfo.BICYCLE_DESTINATION;
        }
        return prop;
    }
   
}
