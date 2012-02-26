package kit.route.a.lot.map.infosupply;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.map.Area;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.POINode;
import kit.route.a.lot.map.Street;


public class FileElementDB extends ArrayElementDB implements ElementDB {
    
    private static Logger logger = Logger.getLogger(FileElementDB.class);
    
    private boolean isSavingNodes = false;
    private boolean isSavingElements = false;
    private int nodesCount = 0;
    private long nodesCountPointer = 0;
    private int elementsCount = 0;
    private long elementsCountPointer = 0;
    private RandomAccessFile randAccessFile;
    
    private DataOutputStream nodePositionStream;
    private File nodePositionFile;

    public FileElementDB(File outputFile) {
        try {
            randAccessFile = new RandomAccessFile(outputFile, "rw");
            nodePositionFile = File.createTempFile("nodePositions", ".bin");
            nodePositionStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(nodePositionFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void addFavorite(POINode favorite) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addMapElement(MapElement element) {
        if (!isSavingElements) {
            if (isSavingNodes) {
                isSavingNodes = false;
                isSavingElements = true;
                try {
                    elementsCountPointer = randAccessFile.getFilePointer();
                    randAccessFile.writeInt(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                return;
            }
        }
        try {
            MapElement.saveToOutput(randAccessFile, element, false);
            elementsCount++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addNode(int nodeID, Node node) {
        try {
            if (!isSavingNodes && !isSavingElements) {
                isSavingNodes = true;
                nodesCountPointer = randAccessFile.getFilePointer();
                randAccessFile.writeInt(0);
            }
            nodePositionStream.writeLong(randAccessFile.getFilePointer());
            MapElement.saveToOutput(randAccessFile, node, false);
            nodesCount++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void lastElementAdded() {
        try {
            randAccessFile.writeInt(0);
            randAccessFile.seek(nodesCountPointer);
            randAccessFile.writeInt(nodesCount);
            randAccessFile.seek(elementsCountPointer);
            randAccessFile.writeInt(elementsCount);
            
            nodePositionStream.close();
            RandomAccessFile nodePositions = new RandomAccessFile(nodePositionFile, "r");
            
            Collections.sort(swapList);
            
//            // swap node ids in map elements - are already swapped in OSMLoader
//            Iterator<SwapTask> swapIterator = swapList.iterator();
//            if (swapIterator.hasNext()) {
//                long prevPointer;
//                SwapTask currentSwap = swapIterator.next();
//                
//                for (int i = 0; i < elementsCount && swapIterator.hasNext(); i++) {
//                    prevPointer = randAccessFile.getFilePointer();
//                    MapElement element = MapElement.loadFromInput(randAccessFile, false);
//                    Node[] nodes;
//                    if (element instanceof Street) {
//                        Street street = (Street) element;
//                        nodes = street.getNodes();
//                    } else if (element instanceof Area) {
//                        Area area = (Area) element;
//                        nodes = area.getNodes();
//                    } else {
//                        logger.error("Read element was neither a street nor an area. That's weird...");
//                        break;
//                    }
//                    for (Node node : nodes) {
//                        if (node.getID() == currentSwap.from) {
//                            node.setID(currentSwap.to);
//                            currentSwap = swapIterator.next();
//                        }
//                    }
//                    randAccessFile.seek(prevPointer);
//                    MapElement.saveToOutput(randAccessFile, element, false);
//                }
//                swapIterator = swapList.iterator();
//            }
            
            // swap node positions
            for (SwapTask currentSwap : swapList) {
                System.out.println(currentSwap.id1 + " ' " + currentSwap.id2);
                
//                nodePositions.seek(currentSwap.id1 * 8);    // 8 == length of long
//                long posNode1 = nodePositions.readLong();
//                randAccessFile.seek(posNode1);
//                MapElement node1 = MapElement.loadFromInput(randAccessFile, false);
//                nodePositions.seek(currentSwap.id2 * 8);
//                long posNode2 = nodePositions.readLong();
//                randAccessFile.seek(posNode2);
//                MapElement node2 = MapElement.loadFromInput(randAccessFile, false);
//                randAccessFile.seek(posNode2);
//                MapElement.saveToOutput(randAccessFile, node1, false);
//                randAccessFile.seek(posNode1);
//                MapElement.saveToOutput(randAccessFile, node2, false);
            }
            
            randAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteFavorite(Coordinates pos, int detailLevel, int radius) {
        throw new UnsupportedOperationException();
    }

    @Override
    public POIDescription getFavoriteDescription(Coordinates pos, int detailLevel, int radius) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ArrayList<POINode> getFavorites() {
        return new ArrayList<POINode>();
    }

    @Override
    public MapElement getMapElement(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node getNode(int nodeID) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void loadFromStream(DataInputStream stream) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveToStream(DataOutputStream stream) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    public List<SwapTask> swapList = new ArrayList<SwapTask>();

    @Override
    public void swapNodeIDs(int id1, int id2) {
        swapList.add(new SwapTask(id1, id2));
        swapList.add(new SwapTask(id2, id1));
    }
    
    private class SwapTask implements Comparable<SwapTask> {
        int id1;
        int id2;
        
        SwapTask(int id1, int id2) {
            this.id1 = id1;
            this.id2 = id2;
        }
        
        @Override
        public int compareTo(SwapTask o) {
            return id1 - o.id1;
        }
    }

}
