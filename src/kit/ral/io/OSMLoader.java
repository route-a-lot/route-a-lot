
/**
Copyright (c) 2012, Matthias Grundmann, Malte Wolff, Jan Jacob, Daniel Krauß, Josua Stabenow
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * The names of the contributors may not be used to endorse or promote products
          derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
**/

package kit.ral.io;

import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;
import kit.ral.common.Progress;
import kit.ral.common.WeightCalculator;
import kit.ral.common.description.Address;
import kit.ral.common.description.OSMType;
import kit.ral.common.description.POIDescription;
import kit.ral.common.description.WayInfo;
import kit.ral.common.projection.Projection;
import kit.ral.common.projection.ProjectionFactory;
import kit.ral.common.util.Util;
import kit.ral.controller.State;
import kit.ral.map.info.MapInfo;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.*;

import static kit.ral.common.description.OSMType.*;

public class OSMLoader {

    private static Logger logger = Logger.getLogger(OSMLoader.class);
    
    private Collection<Edge> edges;
    private List<Integer> wayType;
    private Collection<Edge> undirectedEdges;
    private int maxWayNodeId = -1;

    float minLat, maxLat, minLon, maxLon;

    private State state;
    private WeightCalculator weightCalculator;
    private Projection projection;
    
    int nodeCount;
    private long[] osmIds;
    
    // key is an OSM-id and value is the new id
    Map<Long, Integer> idMap = new HashMap<Long, Integer>();

    public OSMLoader(State state, WeightCalculator weightCalculator) {
        this.state = state;
        edges = new HashSet<Edge>();
        wayType = new ArrayList<Integer>();
        undirectedEdges = new HashSet<Edge>();
        this.weightCalculator = weightCalculator;
    }

    /**
     * Imports an osm map from file.
     * 
     * @param file
     *            the osm File to be imported
     * @param progress 
     */
    public void importMap(File file, Progress progress) {
        final MapInfo mapInfo = state.getMapInfo();
        // TODO handle progress
        logger.info("Importing " + file);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = null;
        try {
            parser = factory.newSAXParser();
        } catch (ParserConfigurationException e1) {
            e1.printStackTrace();
        } catch (SAXException e1) {
            e1.printStackTrace();
        }

        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e1) {
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
        
        };

        logger.info("Start calculating bounds... " + new Date());
        try {
            parser.parse(inputStream, boundsHandler);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            if (nodeCount < 0) {
                return;
            }
        }
        progress.addProgress(0.001);
        
        Coordinates topLeft = new Coordinates(maxLat, minLon);
        Coordinates bottomRight = new Coordinates(minLat, maxLon);
        projection = ProjectionFactory.getNewProjection(topLeft, bottomRight);
        weightCalculator.setProjection(projection);
        mapInfo.setBounds(new Bounds(
                projection.getLocalCoordinates(topLeft),
                projection.getLocalCoordinates(bottomRight)));
        mapInfo.setGeoBounds(new Bounds(topLeft, bottomRight, true));
        logger.debug("Finished calculating bounds: topLeft=" + topLeft
                + ", bottomRight=" + bottomRight);

        logger.info("Importing elements... " + new Date());
        osmIds = new long[nodeCount];

        DefaultHandler handler = new DefaultHandler() {
            
            boolean isInWay, isInNode;
            List<Integer> curWayIds;
            List<Long> curWayOSMIds;
            String curName;
            WayInfo curWayInfo;

            Address curAddress;
            int curType;

            Coordinates curNodeCoordinates;
            int curNodeId;        

            long ignoredKeys = 0;

            public void startElement(String uri, String localName, String qName, Attributes attributes)
                    throws SAXException {

                qName = qName.toLowerCase();
                // parse node/way inner tags
                if (qName.equals("tag")) {
					String key = attributes.getValue("k").toLowerCase();
					String value = attributes.getValue("v");
					
					// parse node key addr:*
					if (key.startsWith("addr:")) {
						if (key.equals("addr:housenumber")
								|| key.equals("addr:housename")) {
							curAddress.setHousenumber(value);
						} else if (key.equals("addr:street")) {
							curAddress.setStreet(value);
						} else if (key.equals("addr:state")) {
							curAddress.setState(value);
						} else if (key.equals("addr:postcode")) {
							curAddress.setPostcode(value);
						} else if (key.equals("addr:city")) {
							curAddress.setCity(value);
						} else if (key.equals("addr:country")) {
							curAddress.setCountry(value);
						} else if (key.equals("addr:full")) {
							curAddress.setFullAddress(value);
						} else if (key.equals("addr:interpolation")) {
							curAddress.setInterpolation(value);
						} else if (key.equals("addr:suburb")
								|| key.equals("addr:quarter")
								|| key.equals("addr:district")
								|| key.equals("addr:hamlet") 
								|| key.equals("addr:inclusion")) {
							// ignore
						} else {
							logger.debug("Unknown addr:* tag: " + key + ", value: " + value);
						}
						return;
					}
					if (key.equals("postal_code")) {
						curAddress.setPostcode(value);
						return;
					}
					if (key.equals("name")) {
                        curName = value;
                        return;
					}
					
					// parse other node keys
					value = value.toLowerCase();
					boolean found = true;
					if (key.equals("amenity")) {
						curType = getAmenityType(value);
					} else if (key.equals("shop")) {
						curType = getShopType(value);
					} else if (key.equals("historic")) {
						if (value.equals("castle")) {
							curType = HISTORIC_CASTLE;
						} else if (value.equals("monument")) {
							curType = HISTORIC_MONUMENT;
						}
					} else if (key.equals("leisure")) {
						curType = getLeisureType(value);
					} else {
						found = false;
					}
					if (found && (curType == UNKNOWN_TYPE)) {
						logger.debug("Unknown value for " + key + " key in tags: " + value);
					}
					if (found) {
						return;
					}
                }
				
                // parse way nodes/tags
                if (isInWay) {
					if (qName.equals("nd")) {
						Long osmID = Long.parseLong(attributes.getValue("ref"));
						Integer nodeID = idMap.get(osmID);
						if (nodeID == null) {
							logger.warn("OSM Node ID is unknown: " + osmID);
							return;
						}
						curWayIds.add(nodeID);
						curWayOSMIds.add(osmID);
					} else if (qName.equals("tag")) {
                        String key = attributes.getValue("k").toLowerCase();
                        String value = attributes.getValue("v").toLowerCase();
                        if (key.equals("highway")) {
                            curWayInfo.setStreet(true);
                            curType = getHighwayType(value);
                            if (curType == HIGHWAY_IGNORED) {
                                logger.debug("Highway type ignored: " + value);
                            }
                        } else if (key.equals("bicycle")) {
                            byte bicycle = getBicycleRestrictions(value);
                            if (bicycle != UNKNOWN_TYPE) {
                                curWayInfo.setBicycle(bicycle);
                            } else {
                                logger.debug("Unknown value for " + key + " key in tags: " + value);
                            }
                        } else if (key.equals("oneway")) {
                            if (value.equals("yes") || value.equals("true")) {
                                curWayInfo.setOneway(WayInfo.ONEWAY_YES);
                            } else if (value.equals("no") || value.equals("false")) {
                                curWayInfo.setOneway(WayInfo.ONEWAY_NO);
                            } else if (value.equals("-1")) {
                                curWayInfo.setOneway(WayInfo.ONEWAY_OPPOSITE);
                            } else {
                                logger.debug("Unknown value for " + key + " key in tags: " + value);
                            }
                        } else if (key.equals("natural")) {
                            curType = getNaturalType(value);
                            if (curType == NATURAL_WATER || curType == NATURAL_WOOD) {
                                curWayInfo.setArea(true);
                            }
                            if (curType == UNKNOWN_TYPE) {
                                logger.debug("Unknown value for " + key + " key in tags: " + value);
                            }
                        } else if (key.equals("landuse")) {
                            curType = getLanduseType(value);
                            if (curType != UNKNOWN_TYPE) {
                                curWayInfo.setArea(true);
                            } else {
                                logger.debug("Unknown value for " + key + " key in tags: " + value);
                            }    
                        } else if (key.equals("waterway")) {
                            curType = getWaterwayType(value);
                            if (curType == UNKNOWN_TYPE) {
                                logger.debug("Unknown value for " + key + " key in tags: " + value);
                            }
                        } else if (key.equals("cycleway")) {
                            curType = getCyclewayType(value);
                            if (curType == UNKNOWN_TYPE) {
                                logger.debug("Unknown value for " + key + " key in tags: " + value);
                            }
                        } else if (key.equals("railway")) {
                            curType = getRailwayType(value);
                            if (curType != UNKNOWN_TYPE) {
                                curWayInfo.setOther(true);
                            } else {
                                logger.debug("Unknown value for " + key + " key in tags: " + value);
                            }
                        } else if (key.equals("building")) {
                            curType = OSMType.BUILDING;
                            curWayInfo.setBuilding(true);
                            // TODO here and with the following:
                            // check if value == yes or something more specific
                        } else if (key.equals("bridge")) {
                            curWayInfo.setBridge(WayInfo.BRIDGE);
                        } else if (key.equals("tunnel")) {
                            curWayInfo.setTunnel(WayInfo.TUNNEL);
                        } else if (key.equals("area")) {
                            if (value.equals("yes")) {
                                curWayInfo.setArea(true);
                            } else {
                                logger.debug("Unknown value for " + key + " key in tags: " + value);
                            }
                        } else if (key.equals("barrier")) {
                            curWayInfo.setOther(true);
                        } else if (key.equals("access")) {
                            byte access = getAccessType(value);
                            if (access != UNKNOWN_TYPE) {
                                curWayInfo.setAccess(access);
                            } else {
                                logger.debug("Unknown value for " + key + " key in tags: " + value);
                            }
                        } else if (key.equals("surface")) {
                            byte surface = getSurfaceType(value);
                            if (surface != UNKNOWN_TYPE) {
                                curWayInfo.setSurface(surface);
                            } else {
                                logger.debug("Unknown value for " + key + " key in tags: " + value);
                            }
                        } else if (key.equals("segregated")) {
                            if (value.equals("yes")) {
                                curWayInfo.setSegregated(WayInfo.SEGREGATED_YES);
                            } else if (value.equals("no")) {
                                curWayInfo.setSegregated(WayInfo.SEGREGATED_NO);
                            } else {
                                logger.debug("Unknown value for " + key + " key in tags: " + value);
                            }
                        } else if (key.equals("layer")) {
                            try {
                                curWayInfo.setLayer(Byte.parseByte(value));
                            } catch (NumberFormatException e) {
                                logger.error(e.toString());
                                logger.error("Could not parse " + value + " as Integer; used in " + key
                                        + " in a " + qName);
                            }
                        } else if (key.equals("lanes")) {
                            try {
                                curWayInfo.setLanes(Byte.parseByte(value));
                            } catch (NumberFormatException e) {
                                logger.error(e.toString());
                                logger.error("Could not parse " + value + " as Integer;" +
                                		" used in " + key + " in a " + qName);
                            }
                        } else if (key.equals("note") || key.equals("maxspeed")
                                || key.equals("created_by") || key.equals("foot")
                                || key.equals("source") || key.equals("opening_date")
                                || key.startsWith("building:") /* " */
                                || key.equals("ref") || key.equals("planned")
                                || key.equals("construction")) {
                            // ignore
                        } else {
                            if (ignoredKeys % 10000 == 0) {
                                Util.printMemoryInformation();
                            }
                            ignoredKeys++;
                            logger.debug("Key ignored: " + key
                                    + ", value = " + value + " : " + ignoredKeys);
                        }
                    }
                }

				// parse node inner tags
                else if (isInNode && qName.equals("tag")) {
					String key = attributes.getValue("k").toLowerCase();
					String value = attributes.getValue("v").toLowerCase();

					if (key.equals("barrier")) {
						if (value.equals("gate") || value.equals("bollard")
								|| value.equals("cycle_barrier") || value.equals("entrance")
								|| value.equals("lift_gate")) {
							// ignore because it should be no problem for bikers
							// TODO cycle_barrier could be interesting...
							// also: check for bicycle == yes or no
						} else {
							logger.debug("Unknown value for " + key + " key in tags: " + value);
						}
					} else if (key.equals("natural")) {
						if (value.equals("tree")) {
							// TODO should a tree be rendered?
							// if not the node can probably be ignored
							// if yes this 'tree information' should not be ignored
						} else {
							logger.debug("Unknown value for " + key + " key in tags: " + value);
						}
					} else if (key.equals("highway")) {
						if (value.equals("traffic_signals")) {
							// ignore
							// TODO would be nice not ignoring it
						} else {
							logger.debug("Unknown value for " + key + " key in tags: " + value);
						}
					} else if (key.equals("railway")) {
						if (value.equals("level_crossing")) {
							// TODO should be drawn, but is no POI
						} else {
							logger.debug("Unknown value for " + key + " key in tags: " + value);
						}
					} else if (key.equals("source") || key.equals("created_by")
							|| key.equals("note") || key.equals("source_ref")) {
						// ignore
					} else {
						logger.debug("Unknown key in tags in a node: key: " + key + ", value: " + value);
					}
                }

				// parse node tag
                else if (qName.equals("node")) {
					isInNode = true;
					curType = UNKNOWN_TYPE;
					curName = "";
					curAddress = new Address();				
					curNodeCoordinates = projection.getLocalCoordinates(
							new Coordinates(Float.parseFloat(attributes.getValue("lat")),
											Float.parseFloat(attributes.getValue("lon"))));
					curNodeId = idMap.size();
					if (curNodeId >= Integer.MAX_VALUE) {
						throw new IllegalStateException(
								"Tried to import more than " + curNodeId + " nodes!");
					}
					long osmId = Long.parseLong(attributes.getValue("id"));
					idMap.put(osmId, curNodeId);
					osmIds[curNodeId] = osmId;
					// TODO tags (for POI's)
				}
				
				// parse way tag
                else if (qName.equals("way")) {
                    isInWay = true;
					curType = UNKNOWN_TYPE;
					curName = "";
					curAddress = new Address();
					curWayInfo = new WayInfo();  
					curWayIds = new ArrayList<Integer>();
                    curWayOSMIds = new ArrayList<Long>();    
                }
				
				else {
                    logger.trace("Tag ignored: " + qName);
                }
            }

            public void endElement(String uri, String localName, String qName) throws SAXException {
                
                // finish node import
                if (isInNode && qName.equalsIgnoreCase("node")) {
                    isInNode = false;
                    // add node to map info
                    mapInfo.addNode(curNodeCoordinates, curNodeId, curAddress);
                    // if appropriate also add node as POI
                    if ((curType != UNKNOWN_TYPE) && (curName.length() > 0)) {
                        mapInfo.addPOI(curNodeCoordinates,
                                new POIDescription(curName, curType, ""), curAddress);
                    }                 
                }
                
				// finish way import
                else if (isInWay && qName.equalsIgnoreCase("way")) {
					isInWay = false;
					
					// combine way info
                    curWayInfo.setType(curType);
					curWayInfo.setAddress(curAddress);
					if (curWayInfo.isStreet()) {
                        curAddress.setStreet(curName);
                    }

                    if (curWayInfo.isRoutable()) {

                        // swap routable node IDs to primary ID range
                        int tempSwap = -1;
                        for (int i = 0; i < curWayIds.size(); i++) {
                            int curId = curWayIds.get(i);
                            if (curId > maxWayNodeId) {
                                maxWayNodeId++;
                                mapInfo.swapNodeIds(curId, maxWayNodeId);
                                long osmId1 = osmIds[curId];
                                long osmId2 = osmIds[maxWayNodeId];
                                idMap.remove(osmId1);
                                idMap.remove(osmId2);
                                idMap.put(osmId1, maxWayNodeId);
                                idMap.put(osmId2, curId);
                                osmIds[maxWayNodeId] = osmId1;
                                osmIds[curId] = osmId2;
                                for (int index = curWayIds.lastIndexOf(maxWayNodeId); index != -1; index =
                                        curWayIds.lastIndexOf(maxWayNodeId)) {
                                    curWayIds.set(index, tempSwap);
                                }
                                for (int index = curWayIds.lastIndexOf(curId); index != -1; index =
                                        curWayIds.lastIndexOf(curId)) {
                                    curWayIds.set(index, maxWayNodeId);
                                }
                                for (int index = curWayIds.lastIndexOf(tempSwap); index != -1; index =
                                        curWayIds.lastIndexOf(tempSwap)) {
                                    curWayIds.set(index, curId);
                                }
                            }
                        }

                        // add way edges to the undirected graph
                        for (int i = 1; i < curWayIds.size(); i++) {
                            undirectedEdges.add(new Edge(curWayIds.get(i - 1), curWayIds.get(i)));
                        }

                        // add way edges to the directed graph
                        if (curWayInfo.getOneway() == WayInfo.ONEWAY_NO
                                || curWayInfo.getOneway() == WayInfo.ONEWAY_YES) {
                            for (int i = 1; i < curWayIds.size(); i++) {
                                edges.add(new Edge(curWayIds.get(i - 1), curWayIds.get(i)));
                                wayType.add(curWayInfo.getType());
                            }
                        }
                        if (curWayInfo.getOneway() == WayInfo.ONEWAY_NO
                                || curWayInfo.getOneway() == WayInfo.ONEWAY_OPPOSITE) {
                            for (int i = curWayIds.size() - 1; i > 0; i--) {
                                edges.add(new Edge(curWayIds.get(i), curWayIds.get(i - 1)));
                                wayType.add(curWayInfo.getType());
                            }
                            wayType.add(curWayInfo.getType());
                        }
                        
                    }
                    
					// add way to map info
                    if (curWayInfo.isStreet() || curWayInfo.isArea() || curWayInfo.isBuilding()) {
                        mapInfo.addWay(curWayIds, curName, curWayInfo);
                    }
                }
            }

        };
        
        // real parsing and import
        try {
            inputStream.close();
            inputStream = new BufferedInputStream(new ProgressInputStream(
                    new FileInputStream(file), progress.createSubProgress(0.400), file.length()));
            parser.parse(inputStream, handler);       
        } catch (SAXException e) {
            e.printStackTrace(); // not XML 1.0
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // start map info internal importing
        mapInfo.lastElementAdded();
        
        // create routing graph
        logger.info("create adjacient fields... " + new Date());
        int countIDs = edges.size();
        int[] startIDs = new int[countIDs];
        int[] endIDs = new int[countIDs];
        int[] weights = new int[countIDs];
        int i = 0;
        for (Edge edge : edges) {
            startIDs[i] = edge.getStartId();
            endIDs[i] = edge.getEndId();
            weights[i] = weightCalculator.calcWeightWithHeightAndHighwayMalus(
                    startIDs[i], endIDs[i], wayType.get(i));
            i++;
        }
        // check for errors (TODO should not be necessary)
        for (i = 0; i < startIDs.length; i++) {
            if (startIDs[i] > maxWayNodeId || endIDs[i] > maxWayNodeId) {
                logger.error("Id found that is greater than maxWayNodeId");
            }
            if (weights[i] == 0) {
                logger.warn("Got edge with 0 weight. Added weight 1.");
                weights[i] = 1;
            } else if (weights[i] < 0) {
                logger.error("Added an edge with weight < 0");
            }
        }
        progress.addProgress(0.01);

        // create undirected routing graph (for METIS)
        int countUndirectedIDs = undirectedEdges.size();
        int[] undirectedEdgeStartIDs = new int[countUndirectedIDs];
        int[] undirectedEdgeEndIDs = new int[countUndirectedIDs];
        i = 0;
        for (Edge edge : undirectedEdges) {
            undirectedEdgeStartIDs[i] = edge.getStartId();
            undirectedEdgeEndIDs[i] = edge.getEndId();
            i++;
        }
        progress.addProgress(0.04);

        // pass graphs to the routing package
        state.getLoadedGraph().buildGraph(startIDs, endIDs, weights, maxWayNodeId);
        state.getLoadedGraph().buildGraphWithUndirectedEdges(
                undirectedEdgeStartIDs, undirectedEdgeEndIDs, maxWayNodeId);
        progress.addProgress(0.05);
    }
    
    private class Edge {
        private int startId, endId;
        
        public Edge(int startId, int endId) {
            this.startId = startId;
            this.endId = endId;
        }

        
        public int getStartId() {
            return startId;
        }

        public int getEndId() {
            return endId;
        }

        @Override
        public boolean equals(Object o) {
            if (! (o instanceof Edge)) {
                return false;
            } else {
                Edge other = (Edge) o;
                return startId == other.startId && endId == other.endId;
            }
        }
        
        @Override
        public int hashCode() {
            return 17 * startId + 389 * endId;
        }
        
    }
    
}
