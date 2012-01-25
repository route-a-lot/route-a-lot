package kit.route.a.lot.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import kit.route.a.lot.common.Address;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.OSMType;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.WayInfo;
import kit.route.a.lot.common.WeightCalculator;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.rendering.Projection;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class OSMLoader {

    private static Logger logger = Logger.getLogger(OSMLoader.class);

    private ArrayList<Integer> startIds;
    private ArrayList<Integer> endIds;
    private int maxWayNodeId = -1;

    float minLat, maxLat, minLon, maxLon;

    State state;
    WeightCalculator weightCalculator;
    Projection projection;

    int nodeCount;
    private long[] osmIds;

    public OSMLoader() {
        state = State.getInstance();
        weightCalculator = WeightCalculator.getInstance();
        startIds = new ArrayList<Integer>();
        endIds = new ArrayList<Integer>();
    }

    /**
     * Imports an osm map from file.
     * @param file the osm File to be imported
     */
    public void importMap(File file) {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = null;
        try {
            parser = factory.newSAXParser();
        } catch (ParserConfigurationException e1) {
            e1.printStackTrace();
        } catch (SAXException e1) {
            e1.printStackTrace();
        }

        maxLon = maxLat = Float.MIN_VALUE;
        minLon = minLat = Float.MAX_VALUE;

        nodeCount = 0;

        DefaultHandler boundsHandler = new DefaultHandler() {
            public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
                if (qName.equals("node")) {
                    nodeCount++;
                    if (nodeCount < 0) {
                        logger.fatal("Tried to import more than " + Integer.MAX_VALUE + " nodes.");
                        throw new SAXException("maximum of nodes exceeded");
                    }

                    float curLat = Float.parseFloat(attributes.getValue("lat"));
                    float curLon = Float.parseFloat(attributes.getValue("lon"));
                    if (minLat > curLat) {
                        minLat = curLat;
                    }
                    if (maxLat < curLat) {
                        maxLat = curLat;
                    }
                    if (minLon > curLon) {
                        minLon = curLon;
                    }
                    if (maxLon < curLon) {
                        maxLon = curLon;
                    }
                } else if (qName.equals("way")) {
                    throw new SAXException("finished with nodes");
                }
            }
        }; // boundsHandler end

        try {
            parser.parse(file, boundsHandler);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            if (nodeCount < 0) {
                return;
            }
        }

        Coordinates upLeft = new Coordinates(maxLat, minLon);
        Coordinates bottomRight = new Coordinates(minLat, maxLon);
        projection = Projection.getNewProjection(upLeft);
        state.getLoadedMapInfo().setBounds(projection.geoCoordinatesToLocalCoordinates(upLeft), projection.geoCoordinatesToLocalCoordinates(bottomRight));

        osmIds = new long[nodeCount];

        DefaultHandler handler = new DefaultHandler() {

            Map<Long, Integer> idMap = new HashMap<Long, Integer>(); // key is an OSM-id and value is the new id

            boolean inWay;
            boolean inPolyline;
            Integer curPolylineNode;
            List<Integer> curWayIds;
            List<Long> curWayOSMIds;
            String curWayName;
            WayInfo curWayInfo;

            Address curAddress;
            int curType;

            boolean inNode;
            Coordinates curNodeCoordinates;
            int curNodeId;
            POIDescription curNodePOIDescription;

            long ignoredKeys = 0;

            public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

                if ((inWay && inPolyline) || inNode) {
                    if (qName.equalsIgnoreCase("tag")) {
                        String key = attributes.getValue("k");
                        String value = attributes.getValue("v");
                        if (key.startsWith("addr:")) {
                            if (key.equalsIgnoreCase("addr:housenumber")
                                    || key.equalsIgnoreCase("addr:housename")) {
                                curAddress.setHousenumber(value);
                            } else if (key.equalsIgnoreCase("addr:street")) {
                                curAddress.setStreet(value);
                            } else if (key.equalsIgnoreCase("addr:state")) {
                                curAddress.setState(value);
                            } else if (key.equalsIgnoreCase("addr:postcode")) {
                                curAddress.setPostcode(value);
                            } else if (key.equalsIgnoreCase("addr:city")) {
                                curAddress.setCity(value);
                            } else if (key.equalsIgnoreCase("addr:country")) {
                                curAddress.setCountry(value);
                            } else if (key.equalsIgnoreCase("addr:full")) {
                                curAddress.setFullAddress(value);
                            } else if (key.equalsIgnoreCase("addr:interpolation")) {
                                curAddress.setInterpolation(value);
                            } else if (key.equalsIgnoreCase("addr:suburb")
                                    || key.equalsIgnoreCase("addr:quarter")
                                    || key.equalsIgnoreCase("addr:district")
                                    || key.equalsIgnoreCase("addr:hamlet")) {
                                // ignore
                            } else if (key.equalsIgnoreCase("addr:inclusion")) {
                                // ignore; indicates accuracy of interpolation (actual, estimate or
                                // potential)
                            } else {
                                logger.debug("Unknown addr:* tag: " + key + ", value: " + value);
                            }
                            return;
                        }

                        if (key.equalsIgnoreCase("amenity")) {
                            if (value.equalsIgnoreCase("arts_centre")) {
                                curType = OSMType.AMENITY_ARTS_CENTRE;
                            } else if (value.equalsIgnoreCase("atm")) {
                                curType = OSMType.AMENITY_ATM;
                            } else if (value.equalsIgnoreCase("bank")) {
                                curType = OSMType.AMENITY_BANK;
                            } else if (value.equalsIgnoreCase("bar")) {
                                curType = OSMType.AMENITY_BAR;
                            } else if (value.equalsIgnoreCase("bbq")) {
                                curType = OSMType.AMENITY_BBQ;
                            } else if (value.equalsIgnoreCase("bench")) {
                                curType = OSMType.AMENITY_BENCH;
                            } else if (value.equalsIgnoreCase("bicycle_parking")) {
                                curType = OSMType.AMENITY_BICYCLE_RENTAL;
                            } else if (value.equalsIgnoreCase("bicycle_rental")) {
                                curType = OSMType.AMENITY_BICYCLE_RENTAL;
                            } else if (value.equalsIgnoreCase("biergarten")) {
                                curType = OSMType.AMENITY_BIERGARTEN;
                            } else if (value.equalsIgnoreCase("brothel")) {
                                curType = OSMType.AMENITY_BROTHEL;
                            } else if (value.equalsIgnoreCase("bureau_de_change")) {
                                curType = OSMType.AMENITY_BUREAU_DE_CHANGE;
                            } else if (value.equalsIgnoreCase("cafe")) {
                                curType = OSMType.AMENITY_CAFE;
                            } else if (value.equalsIgnoreCase("car_rental")) {
                                curType = OSMType.AMENITY_CAR_RENTAL;
                            } else if (value.equalsIgnoreCase("car_sharing")) {
                                curType = OSMType.AMENITY_CAR_SHARING;
                            } else if (value.equalsIgnoreCase("car_wash")) {
                                curType = OSMType.AMENITY_CAR_WASH;
                            } else if (value.equalsIgnoreCase("cinema")) {
                                curType = OSMType.AMENITY_CINEMA;
                            } else if (value.equalsIgnoreCase("clock")) {
                                curType = OSMType.AMENITY_CLOCK;
                            } else if (value.equalsIgnoreCase("college")) {
                                curType = OSMType.AMENITY_COLLEGE;
                            } else if (value.equalsIgnoreCase("community_centre")) {
                                curType = OSMType.AMENITY_COMMUNITY_CENTRE;
                            } else if (value.equalsIgnoreCase("courthouse")) {
                                curType = OSMType.AMENITY_COURTHOUSE;
                            } else if (value.equalsIgnoreCase("crematorium")) {
                                curType = OSMType.AMENITY_CREMATORIUM;
                            } else if (value.equalsIgnoreCase("dentist")) {
                                curType = OSMType.AMENITY_DENTIST;
                            } else if (value.equalsIgnoreCase("doctors")) {
                                curType = OSMType.AMENITY_DOCTORS;
                            } else if (value.equalsIgnoreCase("drinking_water")) {
                                curType = OSMType.AMENITY_DRINKING_WATER;
                            } else if (value.equalsIgnoreCase("embassy")) {
                                curType = OSMType.AMENITY_EMBASSY;
                            } else if (value.equalsIgnoreCase("ev_charging")) {
                                curType = OSMType.AMENITY_EV_CHARGING;
                            } else if (value.equalsIgnoreCase("fast_food")) {
                                curType = OSMType.AMENITY_FAST_FOOD;
                            } else if (value.equalsIgnoreCase("ferry_terminal")) {
                                curType = OSMType.AMENITY_FERRY_TERMINAL;
                            } else if (value.equalsIgnoreCase("fire_station")) {
                                curType = OSMType.AMENITY_FIRE_STATION;
                            } else if (value.equalsIgnoreCase("food_court")) {
                                curType = OSMType.AMENITY_FOOD_COURT;
                            } else if (value.equalsIgnoreCase("fountain")) {
                                curType = OSMType.AMENITY_FOUNTAIN;
                            } else if (value.equalsIgnoreCase("fuel")) {
                                curType = OSMType.AMENITY_FUEL;
                            } else if (value.equalsIgnoreCase("grave_yard")) {
                                curType = OSMType.AMENITY_GRAVE_YARD;
                            } else if (value.equalsIgnoreCase("grit_bin")) {
                                curType = OSMType.AMENITY_GRIT_BIN;
                            } else if (value.equalsIgnoreCase("hospital")) {
                                curType = OSMType.AMENITY_HOSPITAL;
                            } else if (value.equalsIgnoreCase("hunting_stand")) {
                                curType = OSMType.AMENITY_HUNTING_STAND;
                            } else if (value.equalsIgnoreCase("ice_cream")) {
                                curType = OSMType.AMENITY_ICE_CREAM;
                            } else if (value.equalsIgnoreCase("kindergarten")) {
                                curType = OSMType.AMENITY_KINDERGARTEN;
                            } else if (value.equalsIgnoreCase("library")) {
                                curType = OSMType.AMENITY_LIBRARY;
                            } else if (value.equalsIgnoreCase("marketplace")) {
                                curType = OSMType.AMENITY_MARKETPLACE;
                            } else if (value.equalsIgnoreCase("nightclub")) {
                                curType = OSMType.AMENITY_NIGHTCLUB;
                            } else if (value.equalsIgnoreCase("nursing_home")) {
                                curType = OSMType.AMENITY_NURSING_HOME;
                            } else if (value.equalsIgnoreCase("parking")) {
                                curType = OSMType.AMENITY_PARKING;
                            } else if (value.equalsIgnoreCase("parking_entrance")) {
                                curType = OSMType.AMENITY_PARKING_ENTRANCE;
                            } else if (value.equalsIgnoreCase("parking_space")) {
                                curType = OSMType.AMENITY_PARKING_SPACE;
                            } else if (value.equalsIgnoreCase("pharmacy")) {
                                curType = OSMType.AMENITY_PHARMACY;
                            } else if (value.equalsIgnoreCase("place_of_worship")) {
                                curType = OSMType.AMENITY_PLACE_OF_WORSHIP;
                            } else if (value.equalsIgnoreCase("police")) {
                                curType = OSMType.AMENITY_POLICE;
                            } else if (value.equalsIgnoreCase("post_box")) {
                                curType = OSMType.AMENITY_POST_BOX;
                            } else if (value.equalsIgnoreCase("post_office")) {
                                curType = OSMType.AMENITY_POST_OFFICE;
                            } else if (value.equalsIgnoreCase("prison")) {
                                curType = OSMType.AMENITY_PRISON;
                            } else if (value.equalsIgnoreCase("pub")) {
                                curType = OSMType.AMENITY_PUB;
                            } else if (value.equalsIgnoreCase("public_building")) {
                                curType = OSMType.AMENITY_PUBLIC_BUILDING;
                            } else if (value.equalsIgnoreCase("recycling")) {
                                curType = OSMType.AMENITY_RECYCLING;
                            } else if (value.equalsIgnoreCase("restaurant")) {
                                curType = OSMType.AMENITY_RESTAURANT;
                            } else if (value.equalsIgnoreCase("sauna")) {
                                curType = OSMType.AMENITY_SAUNA;
                            } else if (value.equalsIgnoreCase("school")) {
                                curType = OSMType.AMENITY_SCHOOL;
                            } else if (value.equalsIgnoreCase("shelter")) {
                                curType = OSMType.AMENITY_SHELTER;
                            } else if (value.equalsIgnoreCase("social_centre")) {
                                curType = OSMType.AMENITY_SOCIAL_CENTRE;
                            } else if (value.equalsIgnoreCase("social_facility")) {
                                curType = OSMType.AMENITY_SOCIAL_FACILITY;
                            } else if (value.equalsIgnoreCase("stripclub")) {
                                curType = OSMType.AMENITY_STRIPCLUB;
                            } else if (value.equalsIgnoreCase("studio")) {
                                curType = OSMType.AMENITY_STUDIO;
                            } else if (value.equalsIgnoreCase("taxi")) {
                                curType = OSMType.AMENITY_TAXI;
                            } else if (value.equalsIgnoreCase("telephone")) {
                                curType = OSMType.AMENITY_TELEPHONE;
                            } else if (value.equalsIgnoreCase("theatre")) {
                                curType = OSMType.AMENITY_THEATRE;
                            } else if (value.equalsIgnoreCase("toilets")) {
                                curType = OSMType.AMENITY_TOILETS;
                            } else if (value.equalsIgnoreCase("townhall")) {
                                curType = OSMType.AMENITY_TOWNHALL;
                            } else if (value.equalsIgnoreCase("university")) {
                                curType = OSMType.AMENITY_UNIVERSITY;
                            } else if (value.equalsIgnoreCase("vending_machine")) {
                                curType = OSMType.AMENITY_VENDING_MACHINE;
                            } else if (value.equalsIgnoreCase("veterinary")) {
                                curType = OSMType.AMENITY_VETERINARY;
                            } else if (value.equalsIgnoreCase("waste_basket")) {
                                curType = OSMType.AMENITY_WASTE_BASKET;
                            } else if (value.equalsIgnoreCase("waste_disposal")) {
                                curType = OSMType.AMENITY_WASTE_DISPOSAL;
                            } else if (value.equalsIgnoreCase("watering_place")) {
                                curType = OSMType.AMENITY_WATERING_PLACE;
                            } else {
                                logger.debug("Unknown value for " + key + " key in tags: " + value);
                            }
                            return;
                        }

                        if (key.equalsIgnoreCase("shop")) {
                            if (value.equalsIgnoreCase("alcohol")) {
                                curType = OSMType.SHOP_ALCOHOL;
                            } else if (value.equalsIgnoreCase("bakery")) {
                                curType = OSMType.SHOP_BAKERY;
                            } else if (value.equalsIgnoreCase("beverages")) {
                                curType = OSMType.SHOP_BEVERAGES;
                            } else if (value.equalsIgnoreCase("bicycle")) {
                                curType = OSMType.SHOP_BICYCLE;
                            } else if (value.equalsIgnoreCase("books")) {
                                curType = OSMType.SHOP_BOOKS;
                            } else if (value.equalsIgnoreCase("butcher")) {
                                curType = OSMType.SHOP_BUTCHER;
                            } else if (value.equalsIgnoreCase("car")) {
                                curType = OSMType.SHOP_CAR;
                            } else if (value.equalsIgnoreCase("car_repair")) {
                                curType = OSMType.SHOP_CAR_REPAIR;
                            } else if (value.equalsIgnoreCase("chemist")) {
                                curType = OSMType.SHOP_CHEMIST;
                            } else if (value.equalsIgnoreCase("clothes")) {
                                curType = OSMType.SHOP_CLOTHES;
                            } else if (value.equalsIgnoreCase("computer")) {
                                curType = OSMType.SHOP_COMPUTER;
                            } else if (value.equalsIgnoreCase("convenience")) {
                                curType = OSMType.SHOP_CONVENIENCE;
                            } else if (value.equalsIgnoreCase("copyshop")) {
                                curType = OSMType.SHOP_COPYSHOP;
                            } else if (value.equalsIgnoreCase("doityourself")) {
                                curType = OSMType.SHOP_DO_IT_YOURSELF;
                            } else if (value.equalsIgnoreCase("drugstore")) {
                                curType = OSMType.SHOP_CHEMIST;
                            } else if (value.equalsIgnoreCase("dry_cleaning")) {
                                curType = OSMType.SHOP_DRY_CLEANING;
                            } else if (value.equalsIgnoreCase("furniture")) {
                                curType = OSMType.SHOP_FURNITURE;
                            } else if (value.equalsIgnoreCase("hairdresser")) {
                                curType = OSMType.SHOP_HAIRDRESSER;
                            } else if (value.equalsIgnoreCase("kiosk")) {
                                curType = OSMType.SHOP_KIOSK;
                            } else if (value.equalsIgnoreCase("mall")) {
                                curType = OSMType.SHOP_MALL;
                            } else if (value.equalsIgnoreCase("motorcycle")) {
                                curType = OSMType.SHOP_MOTORCYCLE;
                            } else if (value.equalsIgnoreCase("music")) {
                                curType = OSMType.SHOP_MUSICAL_INSTRUMENT;
                            } else if (value.equalsIgnoreCase("optician")) {
                                curType = OSMType.SHOP_OPTICIAN;
                            } else if (value.equalsIgnoreCase("organic")) {
                                curType = OSMType.SHOP_ORGANIC;
                            } else if (value.equalsIgnoreCase("outdoor")) {
                                curType = OSMType.SHOP_OUTDOOR;
                            } else if (value.equalsIgnoreCase("print")) {
                                curType = OSMType.SHOP_ANIME;
                            } else if (value.equalsIgnoreCase("supermarket")) {
                                curType = OSMType.SHOP_SUPERMARKET;
                            } else if (value.equalsIgnoreCase("toys")) {
                                curType = OSMType.SHOP_TOYS;
                            } else if (value.equalsIgnoreCase("video")) {
                                curType = OSMType.SHOP_VIDEO;
                            } else {
                                logger.debug("Unknown value for " + key + " key in tags: " + value);
                            }
                            return;
                        }

                        if (key.equalsIgnoreCase("historic")) {
                            if (value.equalsIgnoreCase("castle")) {
                                curType = OSMType.HISTORIC_CASTLE;
                            } else if (value.equalsIgnoreCase("monument")) {
                                curType = OSMType.HISTORIC_MONUMENT;
                            } else {
                                logger.debug("Unknown value for " + key + " key in tags: " + value);
                            }
                            return;
                        }

                        if (key.equalsIgnoreCase("crossing")) {
                            if (curType == OSMType.HIGHWAY_CROSSING) {
                                if (value.equalsIgnoreCase("no")) {
                                    curType = 0;
                                } else if (value.equalsIgnoreCase("traffic_signals")) {
                                    // not really important => ignore
                                } else {
                                    logger.debug("Unknown value for " + key + " key in tags: " + value);
                                }
                            } else {
                                logger.debug("Encountered a crossing tag but no HIGHWAY_CROSSING. value: "
                                        + value);
                            }
                            return;
                        }

                        if (key.equalsIgnoreCase("leisure")) {
                            if (value.equalsIgnoreCase("garden")) {
                                curType = OSMType.LEISURE_GARDEN;
                            } else if (value.equalsIgnoreCase("pitch")) {
                                curType = OSMType.LEISURE_PITCH;
                            } else if (value.equalsIgnoreCase("park")) {
                                curType = OSMType.LEISURE_PARK;
                            } else if (value.equalsIgnoreCase("playground")) {
                                curType = OSMType.LEISURE_PLAYGROUND;
                            } else if (value.equalsIgnoreCase("sports_centre")) {
                                curType = OSMType.LEISURE_SPORTS_CENTRE;
                            } else if (value.equalsIgnoreCase("stadium")) {
                                curType = OSMType.LEISURE_STADIUM;
                            } else if (value.equalsIgnoreCase("track")) {
                                curType = OSMType.LEISURE_TRACK;
                            } else {
                                logger.debug("Unknown value for " + key + " key in tags: " + value);
                            }
                            return;
                        }

                        if (key.equalsIgnoreCase("postal_code")) {
                            curAddress.setPostcode(value);
                            return;
                        }
                    }
                }

                if (inWay) {
                    if (inPolyline) {
                        if (qName.equalsIgnoreCase("nd")) {
                            Long osmId = Long.parseLong(attributes.getValue("ref"));
                            Integer newPolylineNode =
                                idMap.get(osmId);
                            if (newPolylineNode == null) {
                                logger.error("Node id is not known: id = " + attributes.getValue("ref"));
                            }
                            curPolylineNode = newPolylineNode;
                            curWayIds.add(curPolylineNode);
                            curWayOSMIds.add(osmId);
                        } else if (qName.equalsIgnoreCase("tag")) {
                            String key = attributes.getValue("k");
                            String value = attributes.getValue("v");
                            if (key.equalsIgnoreCase("name")) {
                                curWayName = value;
                            } else if (key.equalsIgnoreCase("highway")) {
                                if (value.equalsIgnoreCase("bridleway")) {
                                    curType = OSMType.HIGHWAY_BRIDLEWAY;
                                } else if (value.equalsIgnoreCase("crossing")) {
                                    curType = OSMType.HIGHWAY_CROSSING;
                                } else if (value.equalsIgnoreCase("cycleway")) {
                                    curType = OSMType.HIGHWAY_CYCLEWAY;
                                } else if (value.equalsIgnoreCase("footway")) {
                                    curType = OSMType.HIGHWAY_FOOTWAY;
                                } else if (value.equalsIgnoreCase("living_street")) {
                                    curType = OSMType.HIGHWAY_LIVING_STREET;
                                } else if (value.equalsIgnoreCase("motorway")) {
                                    curType = OSMType.HIGHWAY_MOTORWAY;
                                } else if (value.equalsIgnoreCase("motorway_link")) {
                                    curType = OSMType.HIGHWAY_MOTORWAY_LINK;
                                } else if (value.equalsIgnoreCase("path")) {
                                    curType = OSMType.HIGHWAY_PATH;
                                } else if (value.equalsIgnoreCase("pedestrian")) {
                                    curType = OSMType.HIGHWAY_PEDESTRIAN;
                                } else if (value.equalsIgnoreCase("primary")) {
                                    curType = OSMType.HIGHWAY_PRIMARY;
                                } else if (value.equalsIgnoreCase("primary_link")) {
                                    curType = OSMType.HIGHWAY_PRIMARY_LINK;
                                } else if (value.equalsIgnoreCase("residential")) {
                                    curType = OSMType.HIGHWAY_RESIDENTIAL;
                                } else if (value.equalsIgnoreCase("secondary")) {
                                    curType = OSMType.HIGHWAY_SECONDARY;
                                } else if (value.equalsIgnoreCase("secondary_link")) {
                                    curType = OSMType.HIGHWAY_SECONDARY_LINK;
                                } else if (value.equalsIgnoreCase("service")) {
                                    curType = OSMType.HIGHWAY_SERVICE;
                                } else if (value.equalsIgnoreCase("steps")) {
                                    curType = OSMType.HIGHWAY_STEPS;
                                } else if (value.equalsIgnoreCase("steps_large")) {
                                    curType = OSMType.HIGHWAY_STEPS_LARGE;
                                } else if (value.equalsIgnoreCase("tertiary")) {
                                    curType = OSMType.HIGHWAY_TERTIARY;
                                } else if (value.equalsIgnoreCase("track")) {
                                    curType = OSMType.HIGHWAY_TRACK;
                                } else if (value.equalsIgnoreCase("trunk")) {
                                    curType = OSMType.HIGHWAY_TRUNK;
                                } else if (value.equalsIgnoreCase("trunk_link")) {
                                    curType = OSMType.HIGHWAY_TRUNK_LINK;
                                } else if (value.equalsIgnoreCase("unclassified")) {
                                    curType = OSMType.HIGHWAY_UNCLASSIFIED;
                                } else {
                                    curType = OSMType.HIGHWAY_IGNORED;
                                    logger.debug("Highway type ignored: " + value);
                                }
                                curWayInfo.setStreet(true);
                            } else if (key.equalsIgnoreCase("bicycle")) {
                                if (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("designated")
                                        || value.equalsIgnoreCase("permissive")) {
                                    curWayInfo.setBicycle(WayInfo.BICYCLE_YES);
                                } else if (value.equalsIgnoreCase("no")
                                        || value.equalsIgnoreCase("private")) {
                                    curWayInfo.setBicycle(WayInfo.BICYCLE_NO);
                                } else if (value.equalsIgnoreCase("official")) {
                                    curWayInfo.setBicycle(WayInfo.BICYCLE_OFFICIAL);
                                } else if (value.equalsIgnoreCase("dismount")) {
                                    curWayInfo.setBicycle(WayInfo.BICYCLE_DISMOUNT);
                                } else if (value.equalsIgnoreCase("destination")) {
                                    curWayInfo.setBicycle(WayInfo.BICYCLE_DESTINATION);
                                } else {
                                    logger.debug("Unknown value for " + key + " key in tags: " + value);
                                }
                            } else if (key.equalsIgnoreCase("oneway")) {
                                if (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true")) {
                                    curWayInfo.setOneway(WayInfo.ONEWAY_YES);
                                } else if (value.equalsIgnoreCase("no")
                                        || value.equalsIgnoreCase("false")) {
                                    curWayInfo.setOneway(WayInfo.ONEWAY_NO);
                                } else if (value.equalsIgnoreCase("-1")) {
                                    curWayInfo.setOneway(WayInfo.ONEWAY_OPPOSITE);
                                } else {
                                    logger.debug("Unknown value for " + key + " key in tags: " + value);
                                }
                            } else if (key.equalsIgnoreCase("natural")) {
                                if (value.equalsIgnoreCase("bay")) {
                                    curType = OSMType.NATURAL_BAY;
                                } else if (value.equalsIgnoreCase("beach")) {
                                    curType = OSMType.NATURAL_BEACH;
                                } else if (value.equalsIgnoreCase("cliff")) {
                                    curType = OSMType.NATURAL_CLIFF;
                                } else if (value.equalsIgnoreCase("coastline")) {
                                    curType = OSMType.NATURAL_COASTLINE;
                                } else if (value.equalsIgnoreCase("glacier")) {
                                    curType = OSMType.NATURAL_GLACIER;
                                } else if (value.equalsIgnoreCase("heath")) {
                                    curType = OSMType.NATURAL_HEATH;
                                } else if (value.equalsIgnoreCase("land")) {
                                    curType = OSMType.NATURAL_LAND;
                                } else if (value.equalsIgnoreCase("marsh")) {
                                    curType = OSMType.NATURAL_MARSH;
                                } else if (value.equalsIgnoreCase("peak")) {
                                    curType = OSMType.NATURAL_PEAK;
                                } else if (value.equalsIgnoreCase("sand")) {
                                    curType = OSMType.NATURAL_SAND;
                                } else if (value.equalsIgnoreCase("scrub")) {
                                    curType = OSMType.NATURAL_SCRUB;
                                } else if (value.equalsIgnoreCase("spring")) {
                                    curType = OSMType.NATURAL_SPRING;
                                } else if (value.equalsIgnoreCase("stone")) {
                                    curType = OSMType.NATURAL_STONE;
                                } else if (value.equalsIgnoreCase("tree")) {
                                    curType = OSMType.NATURAL_TREE;
                                } else if (value.equalsIgnoreCase("volcano")) {
                                    curType = OSMType.NATURAL_VOLCANO;
                                } else if (value.equalsIgnoreCase("water")) {
                                    curType = OSMType.NATURAL_WATER;
                                } else if (value.equalsIgnoreCase("wetland")) {
                                    curType = OSMType.NATURAL_WETLAND;
                                } else if (value.equalsIgnoreCase("wood")) {
                                    curType = OSMType.NATURAL_WOOD;
                                } else {
                                    logger.debug("Unknown value for " + key + " key in tags: " + value);
                                }
                            } else if (key.equalsIgnoreCase("waterway")) {
                                if (value.equalsIgnoreCase("canal")) {
                                    curType = OSMType.WATERWAY_CANAL;
                                } else if (value.equalsIgnoreCase("dam")) {
                                    curType = OSMType.WATERWAY_DAM;
                                } else if (value.equalsIgnoreCase("ditch")) {
                                    curType = OSMType.WATERWAY_DITCH;
                                } else if (value.equalsIgnoreCase("dock")) {
                                    curType = OSMType.WATERWAY_DOCK;
                                } else if (value.equalsIgnoreCase("drain")) {
                                    curType = OSMType.WATERWAY_DRAIN;
                                } else if (value.equalsIgnoreCase("river")) {
                                    curType = OSMType.WATERWAY_RIVER;
                                } else if (value.equalsIgnoreCase("riverbank")) {
                                    curType = OSMType.WATERWAY_RIVERBANK;
                                } else if (value.equalsIgnoreCase("stream")) {
                                    curType = OSMType.WATERWAY_STREAM;
                                } else if (value.equalsIgnoreCase("waterfall")) {
                                    curType = OSMType.WATERWAY_WATERFALL;
                                } else {
                                    logger.debug("Unknown value for " + key + " key in tags: " + value);
                                }
                            } else if (key.equalsIgnoreCase("cycleway")) {
                                if (value.equalsIgnoreCase("track")) {
                                    curType = OSMType.CYCLEWAY_TRACK;
                                } else if (value.equalsIgnoreCase("lane")) {
                                    curType = OSMType.CYCLEWAY_LANE;
                                } else if (value.equalsIgnoreCase("opposite")) {
                                    curType = OSMType.CYCLEWAY_OPPOSITE;
                                } else if (value.equalsIgnoreCase("opposite_lane")) {
                                    curType = OSMType.CYCLEWAY_OPPOSITE_LANE;
                                } else if (value.equalsIgnoreCase("opposite_track")) {
                                    curType = OSMType.CYCLEWAY_OPPOSITE_TRACK;
                                } else {
                                    logger.debug("Unknown value for " + key + " key in tags: " + value);
                                }
                            } else if (key.equalsIgnoreCase("railway")) {
                                if (value.equalsIgnoreCase("light_rail")) {
                                    curWayInfo.setOther(true);
                                    curType = OSMType.RAILWAY_LIGHT_RAIL;
                                } else if (value.equalsIgnoreCase("rail")) {
                                    curWayInfo.setOther(true);
                                    curType = OSMType.RAILWAY_RAIL;
                                } else if (value.equalsIgnoreCase("subway")) {
                                    curWayInfo.setOther(true);
                                    curType = OSMType.RAILWAY_SUBWAY;
                                } else if (value.equalsIgnoreCase("tram")) {
                                    curWayInfo.setOther(true);
                                    curType = OSMType.RAILWAY_TRAM;
                                } else {
                                    logger.debug("Unknown value for " + key + " key in tags: " + value);
                                }
                            } else if (key.equalsIgnoreCase("building")) {
                                curWayInfo.setBuilding(true); // TODO here and with the following: check
                                // if value == yes or
                                // something more specific
                            } else if (key.equalsIgnoreCase("bridge")) {
                                curWayInfo.setBridge(WayInfo.BRIDGE);
                            } else if (key.equalsIgnoreCase("tunnel")) {
                                curWayInfo.setTunnel(WayInfo.TUNNEL);
                            } else if (key.equalsIgnoreCase("area")) {
                                if (value.equalsIgnoreCase("yes")) {
                                    curWayInfo.setArea(true);
                                } else {
                                    logger.debug("Unknown value for " + key + " key in tags: " + value);
                                }
                            } else if (key.equalsIgnoreCase("barrier")) {
                                curWayInfo.setOther(true);
                            } else if (key.equalsIgnoreCase("access")) {
                                if (value.equalsIgnoreCase("private")) {
                                    curWayInfo.setAccess(WayInfo.ACCESS_PRIVATE);
                                } else if (value.equalsIgnoreCase("destination")
                                        || value.equalsIgnoreCase("customers")) {
                                    curWayInfo.setAccess(WayInfo.ACCESS_DESTINATION);
                                } else if (value.equalsIgnoreCase("yes")
                                        || value.equalsIgnoreCase("permissive")
                                        || value.equalsIgnoreCase("access")
                                        || value.equalsIgnoreCase("public")) {
                                    curWayInfo.setAccess(WayInfo.ACCESS_YES);
                                } else if (value.equalsIgnoreCase("forestry")) {
                                    curWayInfo.setAccess(WayInfo.ACCESS_FORESTRY);
                                } else if (value.equalsIgnoreCase("agricultural")) {
                                    curWayInfo.setAccess(WayInfo.ACCESS_AGRICULTURAL);
                                } else if (value.equalsIgnoreCase("no")) {
                                    curWayInfo.setAccess(WayInfo.ACCESS_NO);
                                } else {
                                    logger.debug("Unknown value for " + key + " key in tags: " + value);
                                }
                            } else if (key.equalsIgnoreCase("surface")) {
                                if (value.equalsIgnoreCase("paved")) {
                                    curWayInfo.setSurface(WayInfo.SURFACE_PAVED);
                                } else if (value.equalsIgnoreCase("unpaved")) {
                                    curWayInfo.setAccess(WayInfo.SURFACE_UNPAVED);
                                } else if (value.equalsIgnoreCase("asphalt")) {
                                    curWayInfo.setAccess(WayInfo.SURFACE_ASPHALT);
                                } else if (value.equalsIgnoreCase("gravel")) {
                                    curWayInfo.setAccess(WayInfo.SURFACE_GRAVEL);
                                } else if (value.equalsIgnoreCase("ground")) {
                                    curWayInfo.setAccess(WayInfo.SURFACE_GROUND);
                                } else if (value.equalsIgnoreCase("grass")) {
                                    curWayInfo.setAccess(WayInfo.SURFACE_GRASS);
                                } else if (value.equalsIgnoreCase("dirt")) {
                                    curWayInfo.setAccess(WayInfo.SURFACE_DIRT);
                                } else if (value.equalsIgnoreCase("cobblestone")) {
                                    curWayInfo.setAccess(WayInfo.SURFACE_COBBLESTONE);
                                } else if (value.equalsIgnoreCase("paving_stones")) {
                                    curWayInfo.setAccess(WayInfo.SURFACE_PAVING_STONES);
                                } else if (value.equalsIgnoreCase("concrete")) {
                                    curWayInfo.setAccess(WayInfo.SURFACE_CONCRETE);
                                } else if (value.equalsIgnoreCase("sand")) {
                                    curWayInfo.setAccess(WayInfo.SURFACE_SAND);
                                } else if (value.equalsIgnoreCase("compacted")) {
                                    curWayInfo.setAccess(WayInfo.SURFACE_COMPACTED);
                                } else {
                                    logger.debug("Unknown value for " + key + " key in tags: " + value);
                                }
                            } else if (key.equalsIgnoreCase("segregated")) {
                                if (value.equalsIgnoreCase("yes")) {
                                    curWayInfo.setSegregated(WayInfo.SEGREGATED_YES);
                                } else if (value.equalsIgnoreCase("no")) {
                                    curWayInfo.setSegregated(WayInfo.SEGREGATED_NO);
                                } else {
                                    logger.debug("Unknown value for " + key + " key in tags: " + value);
                                }
                            } else if (key.equalsIgnoreCase("layer")) {
                                try {
                                    curWayInfo.setLayer(Byte.parseByte(value));
                                } catch (NumberFormatException e) {
                                    logger.error(e.toString());
                                    logger.error("Could not parse " + value + " as Integer; used in "
                                            + key + " in a " + qName);
                                }
                            } else if (key.equalsIgnoreCase("lanes")) {
                                try {
                                    curWayInfo.setLanes(Byte.parseByte(value));
                                } catch (NumberFormatException e) {
                                    logger.error(e.toString());
                                    logger.error("Could not parse " + value + " as Integer; used in "
                                            + key + " in a " + qName);
                                }
                            } else if (key.equalsIgnoreCase("note") || key.equalsIgnoreCase("maxspeed")
                                    || key.equalsIgnoreCase("created_by") || key.equalsIgnoreCase("foot")
                                    || key.equalsIgnoreCase("source")
                                    || key.equalsIgnoreCase("opening_date")
                                    || key.equalsIgnoreCase("landuse") /* TODO really ignore that? */
                                    || key.startsWith("building:") /* " */
                                    || key.equalsIgnoreCase("ref") || key.equalsIgnoreCase("planned")
                                    || key.equalsIgnoreCase("construction")) {
                                // ignore
                            } else {
                                ignoredKeys++;
                                logger.debug("Key ignored: " + key + ", value = " + value + " : "
                                        + ignoredKeys);
                            }
                        } else {
                            logger.debug("Element ignored in polyline: " + qName);
                        }
                    } else {
                        if (qName.equalsIgnoreCase("nd")) {
                            Long osmId = Long.parseLong(attributes.getValue("ref"));
                            curPolylineNode = idMap.get(osmId);
                            if (curPolylineNode == null) {
                                logger.error("Node id is not known: id = " + attributes.getValue("ref"));
                            }
                            inPolyline = true;
                            curWayIds.add(curPolylineNode);
                            curWayOSMIds.add(osmId);
                        }
                    }
                    return;
                }

                if (inNode) {
                    if (qName.equalsIgnoreCase("tag")) {
                        String key = attributes.getValue("k");
                        String value = attributes.getValue("v");

                        if (key.equalsIgnoreCase("name")) {
                            curNodePOIDescription.setName(value);
                        } else if (key.equalsIgnoreCase("barrier")) {
                            if (value.equalsIgnoreCase("gate") || value.equalsIgnoreCase("bollard")
                                    || value.equalsIgnoreCase("cycle_barrier")
                                    || value.equalsIgnoreCase("entrance")
                                    || value.equalsIgnoreCase("lift_gate")) {
                                // ignore because it should be no problem for bikers
                                // TODO cycle_barrier could be interesting...
                                // also: check for bicycle == yes or no
                            } else {
                                logger.debug("Unknown value for " + key + " key in tags: " + value);
                            }
                        } else if (key.equalsIgnoreCase("natural")) {
                            if (value.equalsIgnoreCase("tree")) {
                                // TODO should a tree be rendered?
                                // if not the node can probably be ignored
                                // if yes this 'tree information' should not be ignored
                            } else {
                                logger.debug("Unknown value for " + key + " key in tags: " + value);
                            }
                        } else if (key.equalsIgnoreCase("highway")) {
                            if (value.equalsIgnoreCase("traffic_signals")) {
                                // ignore
                                // TODO would be nice not ignoring it
                            } else {
                                logger.debug("Unknown value for " + key + " key in tags: " + value);
                            }
                        } else if (key.equalsIgnoreCase("railway")) {
                            if (value.equalsIgnoreCase("level_crossing")) {
                                // TODO should be drawn, but is no POI
                            } else {
                                logger.debug("Unknown value for " + key + " key in tags: " + value);
                            }
                        } else if (key.equalsIgnoreCase("source") || key.equalsIgnoreCase("created_by")
                                || key.equalsIgnoreCase("note") || key.equalsIgnoreCase("source_ref")) {
                            // ignore
                        } else {
                            logger.debug("Unknown key in tags in a node: key: " + key + ", value: "
                                    + value);
                        }
                    } else {
                        logger.error("Node inner elment ignored: " + qName);
                    }
                    return;
                }

                if (qName.equalsIgnoreCase("node")) {
                    Coordinates geoCoordinates = new Coordinates();

                    geoCoordinates.setLatitude(Float.parseFloat(attributes.getValue("lat")));
                    geoCoordinates.setLongitude(Float.parseFloat(attributes.getValue("lon")));

                    curNodeCoordinates = projection.geoCoordinatesToLocalCoordinates(geoCoordinates);
                    //                        // System.out.println("coordinates " + curNodeCoordinates);
                    curNodeId = idMap.size();
                    if (curNodeId >= Integer.MAX_VALUE) {
                        logger.error("Tried to import more than " + curNodeId + " nodes!");
                        // TODO throw exception
                    }
                    long osmId = Long.parseLong(attributes.getValue("id"));
                    idMap.put(osmId, curNodeId);
                    osmIds[curNodeId] = osmId;

                    curAddress = new Address();
                    curNodePOIDescription = new POIDescription("", 0, "");
                    curType = 0;

                    inNode = true;

                    // TODO tags (for POI's)
                } else if (qName.equalsIgnoreCase("way")) {
                    inWay = true;
                    curWayIds = new ArrayList<Integer>();
                    curWayOSMIds = new ArrayList<Long>();
                    curWayInfo = new WayInfo();
                    curAddress = new Address();
                    curType = 0;
                } else if (qName.equalsIgnoreCase("relation")) {
                    // TODO should not be ignored because for Autobahn and Bundestrassen they should be
                    // rendered
                    logger.debug("Ignored relation.");
                } else if (qName.equalsIgnoreCase("osm")) {
                    String version = attributes.getValue("version");
                    if (!version.equals("0.6")) {
                        logger.debug("OSM-Version is " + version);
                    }
                } else {
                    logger.trace("Element start ignored: " + qName);
                }
            }

            public void endElement(String uri, String localName, String qName) throws SAXException {
                if (inWay && qName.equalsIgnoreCase("way")) {

                    curWayInfo.setType(curType);

                    curWayInfo.setAddress(curAddress);
                    if (curWayInfo.isStreet()) {
                        curAddress.setStreet(curWayName);
                    }
                    if (curWayInfo.isStreet() || curWayInfo.isArea() || curWayInfo.isBuilding()) {
                        state.getLoadedMapInfo().addWay(curWayIds, curWayName, curWayInfo);
                    }

                    if (curWayInfo.isRoutable()) {

                        for (int i = 0; i < curWayIds.size(); i++) {
                            int curId = curWayIds.get(i);
                            if (curId > maxWayNodeId) {
                                maxWayNodeId++;
                                state.getLoadedMapInfo().swapNodeIds(curId, maxWayNodeId);
                                long osmId1 = osmIds[curId];
                                long osmId2 = osmIds[maxWayNodeId];
                                idMap.remove(osmId1);
                                idMap.remove(osmId2);
                                idMap.put(osmId1, maxWayNodeId);
                                idMap.put(osmId2, curId);
                                osmIds[maxWayNodeId] = osmId1;
                                osmIds[curId] = osmId2;
                                curWayIds.set(i, maxWayNodeId);
                            }
                        }

                        if (curWayInfo.getOneway() == WayInfo.ONEWAY_NO
                                || curWayInfo.getOneway() == WayInfo.ONEWAY_YES) {
                            startIds.add(curWayIds.get(0));
                            for (int i = 1; i < curWayIds.size() - 1; i++) {
                                endIds.add(curWayIds.get(i));
                                startIds.add(curWayIds.get(i));
                            }
                            endIds.add(curWayIds.get(curWayIds.size() - 1));
                        }

                        if (curWayInfo.getOneway() == WayInfo.ONEWAY_NO
                                || curWayInfo.getOneway() == WayInfo.ONEWAY_OPPOSITE) {
                            startIds.add(curWayIds.get(curWayIds.size() - 1));
                            for (int i = curWayIds.size() - 2; i > 0; i--) {
                                endIds.add(curWayIds.get(i));
                                startIds.add(curWayIds.get(i));
                            }
                            endIds.add(curWayIds.get(0));
                        }

                    }

                    inWay = false;
                    inPolyline = false;
                    curWayIds = null;
                    curWayOSMIds = null;
                    curWayName = "";
                } else if (inNode && qName.equalsIgnoreCase("node")) {

                    if (curType != 0) {
                        curNodePOIDescription.setCategory(curType);
                        state.getLoadedMapInfo().addPOI(curNodeCoordinates, curNodeId,
                                curNodePOIDescription, curAddress);
                    } else {
                        state.getLoadedMapInfo().addNode(curNodeCoordinates, curNodeId, curAddress);
                    }

                    //                        // System.out.println(curNodeCoordinates);
                    inNode = false;
                }
            }

            public void characters(char ch[], int start, int length) throws SAXException {
            }

        };

        try {
            parser.parse(file, handler);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        weightCalculator.setProjection(projection);

        logger.info("create adjacient fields...");

        int countIDs = startIds.size();
        int[] startIDs = new int[countIDs];
        int[] endIDs = new int[countIDs];
        int[] weights = new int[countIDs];
        for (int i = 0; i < countIDs; i++) {
            startIDs[i] = startIds.get(i);
            endIDs[i] = endIds.get(i);
            weights[i] = weightCalculator.calcWeight(startIDs[i], endIDs[i]);
        }

        for (int i = 0; i < startIDs.length; i++) {
            if (startIDs[i] > maxWayNodeId || endIDs[i] > maxWayNodeId) {
                logger.error("Id found that is greater than maxWayNodeId");
            }
            if (weights[i] == 0) {
                logger.warn("Added edge with 0 weight.");
            } else if (weights[i] < 0) {
                logger.error("Added an edge with weight < 0");
            }
        }

        state.getLoadedGraph().buildGraph(startIDs, endIDs, weights, maxWayNodeId);


    }
}
