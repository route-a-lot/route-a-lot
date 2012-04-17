package kit.ral.common.description;


public class OSMType {

    // types: for explanations see
    // http://wiki.openstreetmap.org/wiki/Map_Features

    public static final int UNKNOWN_TYPE = 0;

    public static final int
        NATURAL_BAY = 1250, NATURAL_BEACH = 1251, NATURAL_CLIFF = 1252,
        NATURAL_COASTLINE = 1253, NATURAL_GLACIER = 1254, NATURAL_HEATH = 1255,
        NATURAL_LAND = 1256, NATURAL_MARSH = 1257, NATURAL_PEAK = 1258,
        NATURAL_SAND = 1259, NATURAL_SCRUB = 1260, NATURAL_SPRING = 1261,
        NATURAL_STONE = 1262, NATURAL_TREE = 1263, NATURAL_VOLCANO = 1264,
        NATURAL_WATER = 12565, NATURAL_WETLAND = 1266, NATURAL_WOOD = 1267;
    
    public static final int
        LANDUSE_GRASS = 1350, LANDUSE_FOREST = 1351, LANDUSE_RESIDENTIAL = 1352,
        LANDUSE_FARM = 1353, LANDUSE_FARMLAND = 1354, LANDUSE_MEADOW = 1355,
        LANDUSE_RESERVOIR = 1356, LANDUSE_INDUSTRIAL = 1357, LANDUSE_FARMYARD = 1358,
        LANDUSE_CEMETERY = 1359;
    
    public static final int 
        WATERWAY_CANAL = 1470, WATERWAY_DAM = 1471, WATERWAY_DITCH = 1472, WATERWAY_DOCK = 1473,
        WATERWAY_DRAIN = 1474, WATERWAY_RIVER = 1475, WATERWAY_RIVERBANK = 1476,
        WATERWAY_STREAM = 1477, WATERWAY_WATERFALL = 1478;
    
    public static final int
        //LEISURE_COMMON = 1550, LEISURE_DANCE = 1551, LEISURE_DOG_PARK = 1552,
        //LEISURE_FISHING = 1553, LEISURE_GOLF_COURSE = 1554, LEISURE_NATURE_RESERVE = 1555,
        //LEISURE_ICE_RINK = 1556, LEISURE_MARINA = 1557, LEISURE_MINIATURE_GOLF = 1558,
        //LEISURE_SLIPWAY = 1559, LEISURE_SWIMMING_POOL = 1560, LEISURE_WATER_PARK = 1561,
        LEISURE_GARDEN = 1562, LEISURE_PARK = 1563, LEISURE_PITCH = 1564,
        LEISURE_PLAYGROUND = 1565,  LEISURE_SPORTS_CENTRE = 1566,
        LEISURE_STADIUM = 1567, LEISURE_TRACK = 1568;

    public static final int
        // RAILWAY_LEVEL_CROSSING = 1854,
        RAILWAY_LIGHT_RAIL = 1855, RAILWAY_RAIL = 1856,
        RAILWAY_SUBWAY = 1862, RAILWAY_TRAM = 1865;
    
    public static final int 
        CYCLEWAY_LANE = 1950, CYCLEWAY_OPPOSITE = 1951, CYCLEWAY_OPPOSITE_LANE = 1952,
        CYCLEWAY_OPPOSITE_TRACK = 1953, CYCLEWAY_TRACK = 1954;

    public static final int 
        HIGHWAY_BRIDLEWAY = 3000,
        HIGHWAY_FOOTWAY = 3001,
        HIGHWAY_PEDESTRIAN = 3002,
        HIGHWAY_PATH = 3003,
        HIGHWAY_CYCLEWAY = 3004,
        HIGHWAY_TRACK = 3005,
        //HIGHWAY_BYWAY = 3006,
        //HIGHWAY_ROAD = 3007,
        
        HIGHWAY_IGNORED = 3008,
        HIGHWAY_SERVICE = 3009,
        
        HIGHWAY_LIVING_STREET = 3010,
        HIGHWAY_RESIDENTIAL = 3011,       
        HIGHWAY_TERTIARY_LINK = 3012,
        HIGHWAY_TERTIARY = 3020,          
        HIGHWAY_SECONDARY_LINK = 3021,
        HIGHWAY_SECONDARY = 3022,         
        HIGHWAY_PRIMARY_LINK = 3023,
        HIGHWAY_PRIMARY = 3024,           
        HIGHWAY_MOTORWAY_LINK = 3025,
        HIGHWAY_MOTORWAY = 3026,       
        HIGHWAY_TRUNK_LINK = 3027,
        HIGHWAY_TRUNK = 3028,
        //HIGHWAY_RACEWAY = 3029,
               
        HIGHWAY_STEPS = 3031,
        HIGHWAY_STEPS_LARGE = 3032,
        HIGHWAY_CROSSING = 3033,
        HIGHWAY_MOTORWAY_JUNCTION = 3034,
        /*HIGHWAY_GIVE_WAY = 3035,
        HIGHWAY_MINI_ROUNDABOUT = 3036,     
        HIGHWAY_ROUNDABOUT = 3037,
        HIGHWAY_STOP = 3038,
        HIGHWAY_TRAFFIC_SIGNALS = 3039,
        HIGHWAY_BUS_STOP = 3040,
        HIGHWAY_EMERGENCY_ACCESS_POINT = 3041,
        HIGHWAY_FORD = 3042,
        HIGHWAY_SPEED_CAMERA = 3043,
        HIGHWAY_SERVICES = 3044,
        HIGHWAY_TURNING_CIRCLE = 3045,*/
        
        HIGHWAY_UNCLASSIFIED = 3048;
        

    public static final int 
        /*SHOP_ART = 4303, SHOP_BABY_GOODS = 4304, SHOP_BATHROOM_FURNISHING = 4306,
        SHOP_BEAUTY = 4307, SHOP_BED = 4308, SHOP_BOUTIQUE = 4312,
        SHOP_CAR_PARTS = 43016, SHOP_CARPET = 4317, SHOP_CHARITY = 4318,
        SHOP_CONFECTIONARY = 4322, SHOP_CURTAIN = 4325, SHOP_DELI = 4326,
        SHOP_DEPARTMENT_STORE = 4327, SHOP_DIVE = 4328, SHOP_ELECTRONICS = 4331,
        SHOP_EROTIC = 4332, SHOP_FABRIC = 4333, SHOP_FARM = 4334, 
        SHOP_FLORIST = 4336, SHOP_FRAME = 4337, SHOP_FURNACE = 4338,
        SHOP_FUNERAL_DIRECTORS = 4339, SHOP_GARDEN_CENTRE = 4341, SHOP_GAS = 4342,
        SHOP_GENERAL = 4343, SHOP_GIFT = 4344, SHOP_GLACERY = 4345,
        SHOP_GREENGROCER = 4346, SHOP_HARDWARE = 4348, SHOP_LAUNDRY = 4357,
        SHOP_HEARING_AIDS = 4349, SHOP_HERBALIST = 4350, SHOP_HIFI = 4351,
        SHOP_HUNTING = 4352, SHOP_INTERIOR_DECORATION = 4353, SHOP_JEWELRY = 4354,
        SHOP_KITCHEN = 4356, SHOP_MASSAGE = 4359, SHOP_MOBILE_PHONE = 4360,
        SHOP_MONEY_LENDER = 4361, SHOP_NEWSAGENT = 4364, SHOP_PAINT = 4368,
        SHOP_PAWNBROKER = 4369, SHOP_PET = 4370, SHOP_RADIOTECHNICS = 4371,
        SHOP_FISH = 4335, SHOP_SEAFOOD = SHOP_FISH, SHOP_SECOND_HAND = 4372,
        SHOP_SHOES = 4373, SHOP_SPORTS = 4374, SHOP_STATIONERY = 4375,
        SHOP_TATTOO = 4377, SHOP_TOBACCO = 4378, SHOP_TRADE = 4380,
        SHOP_VACANT = 4381, SHOP_VACUUM_CLEANER = 4382, SHOP_VARIETY_STORE = 4383,
        SHOP_WINDOW_BLIND = 4385, */     
        SHOP_ALCOHOL = 4301, SHOP_ANIME = 4302, SHOP_BAKERY = 4305, 
        SHOP_BEVERAGES = 4309, SHOP_BICYCLE = 4310, SHOP_BOOKS = 4311, 
        SHOP_BUTCHER = 4313, SHOP_CAR = 4314, SHOP_CAR_REPAIR = 4315,
        SHOP_CHEMIST = 4319, SHOP_CLOTHES = 4320, SHOP_COMPUTER = 4321,
        SHOP_CONVENIENCE = 4323, SHOP_COPYSHOP = 4324, SHOP_DRY_CLEANING = 4329,
        SHOP_DO_IT_YOURSELF = 4330, SHOP_FURNITURE = 4340, SHOP_HAIRDRESSER = 4347,
        SHOP_KIOSK = 4355, SHOP_MALL = 4358, SHOP_MOTORCYCLE = 4362,
        SHOP_MUSICAL_INSTRUMENT = 4363, SHOP_OPTICIAN = 4365, SHOP_ORGANIC = 4366,
        SHOP_OUTDOOR = 4367, SHOP_SUPERMARKET = 4376,  SHOP_TOYS = 4379, 
        SHOP_VIDEO = 4384;

    /*public static final int
        BARRIER = 4500,
        TRAFFIC_CALMING = 4501;
    
    public static final int
        GEOLOGICAL = 4600, TOURISM = 4601, CRAFT = 4602,
        MILITARY = 4603, OFFICE = 4604, POWER = 4605,
        EMERGENCY = 4606, SERVICE = 4607, MAN_MADE = 4608;*/

    //public static final int PUBLIC_TRANSPORT = 4700;
    //public static final int TRACKTYPE = 4800;
    public static final int BUILDING = 5000;   
    
    public static final int 
        AMENITY_BAR = 5050, AMENITY_BBQ = 5051, AMENITY_BIERGARTEN = 5052,
        AMENITY_CAFE = 5053, AMENITY_DRINKING_WATER = 5054, AMENITY_FAST_FOOD = 5055,
        AMENITY_FOOD_COURT = 5056, AMENITY_ICE_CREAM = 5057, AMENITY_PUB = 5058,
        AMENITY_RESTAURANT = 5059, AMENITY_COLLEGE = 5060, AMENITY_KINDERGARTEN = 5061,
        AMENITY_LIBRARY = 5062, AMENITY_SCHOOL = 5063, AMENITY_UNIVERSITY = 5064,
        AMENITY_BICYCLE_PARKING = 5065, AMENITY_BICYCLE_RENTAL = 5066, AMENITY_CAR_RENTAL = 5067,
        AMENITY_CAR_SHARING = 5068, AMENITY_CAR_WASH = 5069, AMENITY_EV_CHARGING = 5070,
        AMENITY_FERRY_TERMINAL = 5071, AMENITY_FUEL = 5072, AMENITY_GRIT_BIN = 5073,
        AMENITY_PARKING = 5074, AMENITY_PARKING_ENTRANCE = 5075, AMENITY_PARKING_SPACE = 5076,
        AMENITY_TAXI = 5077, AMENITY_ATM = 5078, AMENITY_BANK = 5079,
        AMENITY_BUREAU_DE_CHANGE = 5080, AMENITY_DENTIST = 5081, AMENITY_DOCTORS = 5082,
        AMENITY_HOSPITAL = 5083, AMENITY_NURSING_HOME = 5084, AMENITY_PHARMACY = 5085,
        AMENITY_SOCIAL_FACILITY = 5086, AMENITY_VETERINARY = 5087, AMENITY_ARTS_CENTRE = 5088,
        AMENITY_CINEMA = 5089, AMENITY_COMMUNITY_CENTRE = 5090, AMENITY_FOUNTAIN = 5091,
        AMENITY_NIGHTCLUB = 5092, AMENITY_SOCIAL_CENTRE = 5093, AMENITY_STRIPCLUB = 5094,
        AMENITY_STUDIO = 5095, AMENITY_THEATRE = 5096, AMENITY_BENCH = 5097,
        AMENITY_BROTHEL = 5098, AMENITY_CLOCK = 5099, AMENITY_COURTHOUSE = 5110,
        AMENITY_CREMATORIUM = 5111, AMENITY_EMBASSY = 5112, AMENITY_FIRE_STATION = 5113,
        AMENITY_GRAVE_YARD = 5114, AMENITY_HUNTING_STAND = 5115, AMENITY_MARKETPLACE = 5116,
        AMENITY_PLACE_OF_WORSHIP = 5117, AMENITY_POLICE = 5118, AMENITY_POST_BOX = 5119,
        AMENITY_POST_OFFICE = 5120, AMENITY_PRISON = 5121, AMENITY_PUBLIC_BUILDING = 5122,
        AMENITY_RECYCLING = 5123, AMENITY_SAUNA = 5124, AMENITY_SHELTER = 5125,
        AMENITY_TELEPHONE = 5126, AMENITY_TOILETS = 5127, AMENITY_TOWNHALL = 5128,
        AMENITY_VENDING_MACHINE = 5129, AMENITY_WASTE_BASKET = 5130, AMENITY_WASTE_DISPOSAL = 5131,
        AMENITY_WATERING_PLACE = 5132;
    
    
    public static final int
        //HISTORIC = 5200,
        HISTORIC_CASTLE = 5201, HISTORIC_MONUMENT = 5202;
    
    //public static final int AEROWAY = 5500;    
    //public static final int NON_PHYSICAL = 5600;
    
    public static final int FAVOURITE = 5700;
    
    
    /*public static final int POI_PUBLIC, POI_MARKET, POI_OUTLET,
     * POI_BICYCLE_STORE, POI_RESTAURANT, POI_TOILETS, POI_LEISURE;*/
    
    
    
    
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
        } else if (value.equals("tertiary_link")) {
            curType = HIGHWAY_TERTIARY_LINK;
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
