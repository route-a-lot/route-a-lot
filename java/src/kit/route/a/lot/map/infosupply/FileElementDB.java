package kit.route.a.lot.map.infosupply;

import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.POINode;


public class FileElementDB extends ArrayElementDB implements ElementDB {
    
    private boolean isSavingNodes = false;
    private boolean isSavingElements = false;
    private int nodesCount = 0;
    private long nodesCountPointer = 0;
    private int elementsCount = 0;
    private long elementsCountPointer = 0;
    private RandomAccessFile randAccessFile;
    
    private DataOutputStream nodePositionStream;
    private File nodePositionFile;
    private RandomAccessFile nodePositionRAF;

    public FileElementDB(File outputFile) {
        try {
            randAccessFile = new RandomAccessFile(outputFile, "rw");
            nodePositionFile = File.createTempFile("nodePositions", ".bin");
            nodePositionFile.deleteOnExit();
            nodePositionStream = new DataOutputStream(new BufferedOutputStream(
                    new FileOutputStream(nodePositionFile)));
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
                    nodePositionStream.close();
                    nodePositionRAF = new RandomAccessFile(nodePositionFile, "rw");
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
    public void loadFromInput(DataInput input) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveToOutput(DataOutput output) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void swapNodeIDs(int id1, int id2) {
        if (!isSavingElements) {
            return;
        }
        try {
            long posOnEntering = randAccessFile.getFilePointer();
            
            nodePositionRAF.seek(id1 * 8);  // 8 == length of long
            long posNode1 = nodePositionRAF.readLong();
            randAccessFile.seek(posNode1);
            randAccessFile.readByte();
            randAccessFile.writeInt(id2);
            nodePositionRAF.seek(id2 * 8);
            long posNode2 = nodePositionRAF.readLong();
            nodePositionRAF.seek(id2 * 8);
            nodePositionRAF.writeLong(posNode1);
            nodePositionRAF.seek(id1 * 8);
            nodePositionRAF.writeLong(posNode2);
            randAccessFile.seek(posNode2);
            randAccessFile.readByte();
            randAccessFile.writeInt(id1);
            
            randAccessFile.seek(posOnEntering);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
