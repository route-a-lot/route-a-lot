package kit.route.a.lot.map.infosupply;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.POINode;

import org.apache.log4j.Logger;


public class FileElementDB extends ArrayElementDB {

    private int currentAction = 0;
    private static final int SAVING_NODES = 1;
    private static final int SWAPPING_IDS = 2;
    private static final int SAVING_ELEMENTS = 3;

    private int nodesCount = 0;
    private long nodesCountPointer = 0;
    private int elementsCount = 0;
    private long elementsCountPointer = 0;
    private File outputFile;
    private RandomAccessFile randAccessFile;

    private DataOutputStream nodePositionStream;
    private File nodePositionFile;
    private RandomAccessFile nodePositionRAF;
    
    private static Logger logger = Logger.getLogger(FileElementDB.class);

    
    // CONSTRUCTOR
    
    public FileElementDB(File outputFile) {
        try {
            this.outputFile = outputFile;
            randAccessFile = new RandomAccessFile(outputFile, "rw");
            nodePositionFile = File.createTempFile("nodePositions", ".bin");
            nodePositionFile.deleteOnExit();
            nodePositionStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(nodePositionFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // GETTERS
    
    @Override
    public ArrayList<POINode> getFavorites() {
        return new ArrayList<POINode>();
    }
    
    @Override
    public Iterator<Node> getAllNodes() {
        return new ElementIterator<Node>(outputFile, nodesCountPointer + 4);
    }
  
    @Override
    public Iterator<MapElement> getAllMapElements() {
        return new ElementIterator<MapElement>(outputFile, elementsCountPointer + 4);
    }
    
    // CONSTRUCTIVE OPERATIONS (FAVORITES UNSUPPORTED)
    
    @Override
    public void addNode(int nodeID, Node node) {
        try {
            if (currentAction == 0) {
                currentAction = SAVING_NODES;
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
    
    @Override
    public void addMapElement(MapElement element) {
        if (currentAction == SWAPPING_IDS) {
            currentAction = SAVING_ELEMENTS;
            try {
                nodePositionStream.close();
                nodePositionRAF = new RandomAccessFile(nodePositionFile, "rw");
                elementsCountPointer = randAccessFile.getFilePointer();
                randAccessFile.writeInt(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (currentAction == SAVING_ELEMENTS) {
            try {
                MapElement.saveToOutput(randAccessFile, element, false);
                elementsCount++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void addFavorite(POINode favorite) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteFavorite(Coordinates pos, int detailLevel, int radius) {
        throw new UnsupportedOperationException();
    }
    
    
    // QUERY OPERATIONS (UNSUPPORTED)
    
    @Override
    public Node getNode(int nodeID) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public MapElement getMapElement(int id) {
        throw new UnsupportedOperationException();
    }
        
    @Override
    public POIDescription getFavoriteDescription(Coordinates pos, int detailLevel, float radius) {
        throw new UnsupportedOperationException();
    }
    
     
    // DIRECTIVE OPERATIONS
    
    public void lastElementAdded() {
        try {
            nodePositionRAF.close();
            nodePositionFile.delete();

            randAccessFile.writeInt(0);
            randAccessFile.seek(nodesCountPointer);
            randAccessFile.writeInt(nodesCount);
            randAccessFile.seek(elementsCountPointer);
            randAccessFile.writeInt(elementsCount);
            randAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void swapNodeIDs(int id1, int id2) {
        if (currentAction == SAVING_NODES) {
            currentAction = SWAPPING_IDS;
        }

        if (currentAction == SWAPPING_IDS) {
            try {
                nodePositionRAF.seek(id1 * 8); // 8 == length of long
                long posNode1 = nodePositionRAF.readLong();
                randAccessFile.seek(posNode1 + 1);
                randAccessFile.writeInt(id2);
                nodePositionRAF.seek(id2 * 8);
                long posNode2 = nodePositionRAF.readLong();
                nodePositionRAF.seek(id2 * 8);
                nodePositionRAF.writeLong(posNode1);
                nodePositionRAF.seek(id1 * 8);
                nodePositionRAF.writeLong(posNode2);
                randAccessFile.seek(posNode2 + 1);
                randAccessFile.writeInt(id1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // I/O OPERATIONS (UNSUPPORTED)

    @Override
    public void loadFromInput(DataInput input) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveToOutput(DataOutput output) throws IOException {
        throw new UnsupportedOperationException();
    }


    // MISCELLANEOUS
    
    private class ElementIterator<T> implements Iterator<T> {

        DataInputStream inputStream;
        T currentElement;
        boolean hasNext = true;
        boolean hasMoved = false;

        public ElementIterator(File elementDBFile, long mapElementStartPos) {
            try {
                inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(elementDBFile)));
                if (mapElementStartPos != inputStream.skip(mapElementStartPos)) {
                    logger.error("could not go to map element start position");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public boolean hasNext() {
            try {
                currentElement = (T) MapElement.loadFromInput(inputStream, false);
                hasMoved = true;
            } catch (IOException e) {
                try {
                    inputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                hasNext = false;
            }
            return hasNext;
        }

        @Override
        public T next() {
            if (!hasMoved) {
                hasNext();
            }
            if (!hasNext) {
                return null;
            }
            hasMoved = false;
            return currentElement;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("can't remove element from elementDB");
        }

    }

}
