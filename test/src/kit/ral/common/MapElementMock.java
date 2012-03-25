package kit.ral.common;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import kit.ral.map.MapElement;


public class MapElementMock extends MapElement {
    
    private String name;
    private int id = -1; 
    
    public MapElementMock(String name){
        this.name = name;
    }
    
    @Override
    public String getName(){
        return name;
    }
    
    @Override
    public void setID(int id){
        this.id = id;
    }

    @Override
    public int getID(){
        return id;
    }

    @Override
    public boolean isInBounds(Bounds bounds) {
        return false;
    }

    @Override
    public MapElement getReduced(int detail, float range) {
        return null;
    }

    @Override
    public Selection getSelection() {
        return null;
    }

    @Override
    protected void load(DataInput input) throws IOException {
    }

    @Override
    protected void save(DataOutput output) throws IOException {
    }

}
