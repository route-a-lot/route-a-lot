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
    private static final int SWAPPING_IDS = 2;
    private static final int SAVING_ELEMENTS = 3;

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
            nodePositionStream.writeLong(randAccessFile.getFilePointer() - basePointer);
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
                randAccessFile.seek(elementsCountPointer);
                randAccessFile.skipBytes(8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (currentAction == SAVING_ELEMENTS) {
            try {
                elementPositionStream.writeLong(randAccessFile.getFilePointer() - basePointer);
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
            return (Node) Node.loadFromInput(readStream);
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
            return MapElement.loadFromInput(readStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
        
    @Override
    public POIDescription getFavoriteDescription(Coordinates pos, int detailLevel, float radius) {
        throw new UnsupportedOperationException();
    }
    
     
    // DIRECTIVE OPERATIONS
    
    public void lastElementAdded() {
        try {
            elementPositionStream.close();
            nodePositionRAF.close();
            long endPointer = randAccessFile.getFilePointer();
            createIndexTable();
            System.out.println("Node positions file length: " + nodePositionFile.length());
            nodePositionFile.delete();

            randAccessFile.seek(endPointer);
            randAccessFile.writeInt(0);         // 0 favorites
            randAccessFile.seek(nodesCountPointer);
            randAccessFile.writeInt(nodesCount);
            randAccessFile.seek(elementsCountPointer);
            randAccessFile.writeInt(elementsCount);
            randAccessFile.close();
            
            RandomReadStream randomReadStream = new RandomReadStream(elementDBFile, new FileInputStream(elementDBFile));
            loadFromInput(randomReadStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void createIndexTable() {
        try {
            DataInputStream nodePositionInput = new DataInputStream(new BufferedInputStream(new FileInputStream(nodePositionFile)));
            DataInputStream elementPositionInput = new DataInputStream(new BufferedInputStream(new FileInputStream(elementPositionFile)));
            indexTablePointer = randAccessFile.getFilePointer();
            for (int i = 0; i < nodesCount; i++) {  // TODO could be done without random access, too
                randAccessFile.writeLong(nodePositionInput.readLong());
            }
            for (int i = 0; i < elementsCount; i++) {  // TODO could be done without random access, too
                randAccessFile.writeLong(elementPositionInput.readLong());
            }
            randAccessFile.seek(basePointer);
            randAccessFile.writeLong(indexTablePointer - basePointer);
            randAccessFile.writeLong(nodesCountPointer - basePointer);
            randAccessFile.writeLong(elementsCountPointer - basePointer);
            nodePositionInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    @Override
    public void swapNodeIDs(int id1, int id2) {
        if (currentAction == SAVING_NODES) {
            currentAction = SWAPPING_IDS;
            try {
                nodePositionStream.close();
                nodePositionRAF = new RandomAccessFile(nodePositionFile, "rw");
                elementsCountPointer = randAccessFile.getFilePointer();
                randAccessFile.writeInt(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (currentAction == SWAPPING_IDS) {
            try {
                nodePositionRAF.seek(id1 * 8); // 8 == length of long
                long posNode1 = nodePositionRAF.readLong() + basePointer;
                randAccessFile.seek(posNode1 + 1);
                randAccessFile.writeInt(id2);
                nodePositionRAF.seek(id2 * 8);
                long posNode2 = nodePositionRAF.readLong() + basePointer;
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
