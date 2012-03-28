package kit.ral.map.info;

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

import kit.ral.common.Coordinates;
import kit.ral.common.RandomReadStream;
import kit.ral.common.description.POIDescription;
import kit.ral.map.MapElement;
import kit.ral.map.Node;
import kit.ral.map.POINode;

import org.apache.log4j.Logger;


public class FileElementDB extends ArrayElementDB {

    private int currentAction = -1;
    private static final int INITIALIZED_FOR_FILLING = 0;
    private static final int SAVING_NODES = 1;
    private static final int SWAPPING_IDS = 3;

    private long basePointer = 0;
    private int nodesCount = 0;
    private long nodesCountPointer = 0;
    private int elementsCount = 0;
    private long elementsCountPointer = 0;
    private long indexTablePointer = 0;
    private File elementDBFile;
    private RandomAccessFile randAccessFile;

    private DataOutputStream nodePositionStream;
    private File nodePositionFile;
    private DataOutputStream elementPositionStream;
    private File elementPositionFile;
    private RandomAccessFile nodePositionRAF;
    private File elementsFile;
    private RandomAccessFile elementsRAF;
    
    private RandomReadStream readStream;
    
    private static Logger logger = Logger.getLogger(FileElementDB.class);

    
    // CONSTRUCTOR
    
    public FileElementDB(File outputFile) {
        try {
            this.elementDBFile = outputFile;
            randAccessFile = new RandomAccessFile(outputFile, "rw");
            nodePositionFile = File.createTempFile("nodePositions", ".bin");
            nodePositionFile.deleteOnExit();
            nodePositionStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(nodePositionFile)));
            elementPositionFile = File.createTempFile("elementPositions", ".bin");
            elementPositionFile.deleteOnExit();
            elementPositionStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(elementPositionFile)));
            elementsFile = File.createTempFile("elements", ".tmp");
            elementsFile.deleteOnExit();
            elementsRAF = new RandomAccessFile(elementsFile, "rw");
            currentAction = INITIALIZED_FOR_FILLING;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public FileElementDB() {
    }
    
    // GETTERS
    
    @Override
    public ArrayList<POINode> getFavorites() {
        return new ArrayList<POINode>();    // TODO implement
    }
    
    @Override
    public Iterator<Node> getAllNodes() {
        return new ElementIterator<Node>(elementDBFile, nodesCountPointer + 4, nodesCount);
    }
  
    @Override
    public Iterator<MapElement> getAllMapElements() {
        return new ElementIterator<MapElement>(elementDBFile, elementsCountPointer + 4, elementsCount);
    }
    
    // CONSTRUCTIVE OPERATIONS (FAVORITES UNSUPPORTED)
    
    @Override
    public void addNode(int nodeID, Node node) {
        try {
            if (currentAction == INITIALIZED_FOR_FILLING) {
                currentAction = SAVING_NODES;
                basePointer = randAccessFile.getFilePointer();
                randAccessFile.writeLong(0);    // indexTablePointer
                randAccessFile.writeLong(0);    // nodesCountPointer
                randAccessFile.writeLong(0);    // elementsCountPointer
                nodesCountPointer = randAccessFile.getFilePointer();
                randAccessFile.writeInt(0);
            }
            if (currentAction == SAVING_NODES) {
                nodePositionStream.writeLong(randAccessFile.getFilePointer() - basePointer);
                MapElement.saveToOutput(randAccessFile, node, false);
                nodesCount++;
            } else {
                logger.warn("Not in saving nodes state");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void addMapElement(MapElement element) {
        try {
            elementPositionStream.writeLong(elementsRAF.getFilePointer());
            MapElement.saveToOutput(elementsRAF, element, false);
            elementsCount++;
        } catch (IOException e) {
            e.printStackTrace();
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
    
    
    // QUERY OPERATIONS
    
    @Override
    public Node getNode(int nodeId) {
        if (nodeId >= nodesCount) {
            throw new IllegalArgumentException("Node id is too big");
        }
        try {
            long pointerToPointer = indexTablePointer + nodeId * 8;
            readStream.setPosition(pointerToPointer);
            long pointer = readStream.readLong() + basePointer;
            readStream.setPosition(pointer);
            Node node = (Node) Node.loadFromInput(readStream); 
            if (nodeId != node.getID()) {
                logger.error("Node hasn't the expected id.");
            }
            return node;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public MapElement getMapElement(int elementId) {
        if (elementId >= elementsCount) {
            throw new IllegalArgumentException("Element id is too big");
        }
        try {
            long pointerToPointer = indexTablePointer + nodesCount * 8 + elementId * 8;
            readStream.setPosition(pointerToPointer);
            long pointer = readStream.readLong() + basePointer;
            readStream.setPosition(pointer);
            MapElement element = MapElement.loadFromInput(readStream);
            if (elementId != element.getID()) {
                logger.error("Element hasn't the expected id.");
            }
            return element;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
        
    @Override
    public POIDescription getFavoriteDescription(Coordinates pos, int detailLevel, float radius) {
//        throw new UnsupportedOperationException();
        return new POIDescription("", 0, "");
    }
    
     
    // DIRECTIVE OPERATIONS
    
    public void lastElementAdded() {
        try {
            elementPositionStream.close();
            nodePositionRAF.close();
            copyElementsToDBFile();
            elementsRAF.close();
            elementsFile.delete();
            createIndexTable();
            System.out.println("Node positions file length: " + nodePositionFile.length());
            nodePositionFile.delete();
            elementPositionFile.delete();

//            randAccessFile.seek(randAccessFile.length());
//            randAccessFile.writeInt(0);         // 0 favorites
            randAccessFile.seek(nodesCountPointer);
            randAccessFile.writeInt(nodesCount);
            randAccessFile.close();
            
            RandomReadStream randomReadStream = new RandomReadStream(elementDBFile, new FileInputStream(elementDBFile));
            loadFromInput(randomReadStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void copyElementsToDBFile() {
        try {
            elementsCountPointer = randAccessFile.getFilePointer();
            randAccessFile.writeInt(elementsCount);
            byte[] buf = new byte[2048];
            elementsRAF.seek(0);
            int len = elementsRAF.read(buf);
            while (len > 0) {
                randAccessFile.write(buf, 0, len);
                len = elementsRAF.read(buf);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    private void createIndexTable() {
        try {
            DataInputStream nodePositionInput = new DataInputStream(new BufferedInputStream(new FileInputStream(nodePositionFile)));
            DataInputStream elementPositionInput = new DataInputStream(new BufferedInputStream(new FileInputStream(elementPositionFile)));
            indexTablePointer = randAccessFile.getFilePointer();
            copyPositionsToFile(nodePositionInput, elementPositionInput);
            randAccessFile.seek(basePointer);
            randAccessFile.writeLong(indexTablePointer - basePointer);
            randAccessFile.writeLong(nodesCountPointer - basePointer);
            randAccessFile.writeLong(elementsCountPointer - basePointer);
            nodePositionInput.close();
            elementPositionInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    private void copyPositionsToFile(DataInputStream nodePositionInput, DataInputStream elementPositionInput)
            throws IOException {
        byte[] buf = new byte[2048];
        int len = nodePositionInput.read(buf);
        while (len > 0) {
            randAccessFile.write(buf, 0, len);
            len = nodePositionInput.read(buf);
        }
        for (int i = 0; i < elementsCount; i++) {
            randAccessFile.writeLong(elementPositionInput.readLong() + elementsCountPointer + 4);
        }
    }

    @Override
    public void swapNodeIDs(int id1, int id2) {
        if (currentAction == SAVING_NODES) {
            currentAction = SWAPPING_IDS;
            try {
                nodePositionStream.close();
                nodePositionRAF = new RandomAccessFile(nodePositionFile, "rw");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (currentAction == SWAPPING_IDS) {
            try {
                long savedPointer = randAccessFile.getFilePointer();
                nodePositionRAF.seek(id1 * 8); // 8 == length of long
                long posNode1 = nodePositionRAF.readLong() + basePointer;
                randAccessFile.seek(posNode1 + 2);
                randAccessFile.writeInt(id2);
                nodePositionRAF.seek(id2 * 8);
                long posNode2 = nodePositionRAF.readLong() + basePointer;
                nodePositionRAF.seek(id2 * 8);
                nodePositionRAF.writeLong(posNode1 - basePointer);
                nodePositionRAF.seek(id1 * 8);
                nodePositionRAF.writeLong(posNode2 - basePointer);
                randAccessFile.seek(posNode2 + 2);
                randAccessFile.writeInt(id1);
                randAccessFile.seek(savedPointer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            logger.warn("Not in swapping id state");
        }
    }


    // I/O OPERATIONS

    @Override
    public void loadFromInput(DataInput input) throws IOException {
        if (!(input instanceof RandomReadStream)) {
            throw new IllegalArgumentException();
        }
        
        basePointer = ((RandomReadStream) input).getPosition();
        
        indexTablePointer = input.readLong() + basePointer;
        nodesCountPointer = input.readLong() + basePointer;
        elementsCountPointer = input.readLong() + basePointer;
        nodesCount = input.readInt();
        input.skipBytes((int) (elementsCountPointer - nodesCountPointer - 4));  // nodes
        elementsCount = input.readInt();
        input.skipBytes((int) (indexTablePointer - elementsCountPointer - 4));  // elements
        input.skipBytes(nodesCount * 8);                                        // index table
        
        readStream = ((RandomReadStream) input).openForReading();
    }

    @Override
    public void saveToOutput(DataOutput output) throws IOException {
        DataInputStream dbInput = new DataInputStream(new BufferedInputStream(new FileInputStream(elementDBFile)));
        byte[] buf = new byte[2048];
        int len = dbInput.read(buf);
        while (len > 0) {
            output.write(buf, 0, len);
            len = dbInput.read(buf);
        }
    }


    private class ElementIterator<T extends MapElement> implements Iterator<T> {

        DataInputStream inputStream;
        T currentElement;
        boolean hasNext = true;
        boolean hasMoved = false;
        int maxElementIndex;
        int currentElementIndex = 0;

        public ElementIterator(File elementDBFile, long mapElementStartPos, int mapElementCount) {
            try {
                maxElementIndex = mapElementCount;
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
            currentElementIndex++;
            if (currentElementIndex > maxElementIndex) {
                hasNext = false;
            } else {
                try {
                    currentElement = (T) MapElement.loadFromInput(inputStream);
                    hasMoved = true;
                } catch (IOException e) {
                    try {
                        inputStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    hasNext = false;
                }
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