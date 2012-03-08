package kit.route.a.lot.common;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import kit.route.a.lot.map.MapElement;


public class MapElementMock extends MapElement {
    
    private String name;
    private int id = 0; 
    
    public MapElementMock(String name){
        this.name = name;
    }
    
    public String getName(){

        return name;
    }
    
    public void setName(String name){
        this.name = name;
    } 
    
    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    @Override
    public boolean isInBounds(Bounds bounds) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public MapElement getReduced(int detail, float range) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Selection getSelection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void load(DataInput input) throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void save(DataOutput output) throws IOException {
        // TODO Auto-generated method stub
        
    }

}
