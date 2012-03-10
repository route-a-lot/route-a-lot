package kit.route.a.lot.map;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.description.POIDescription;

public class POINode extends Node {

    private POIDescription info = null;

    public POINode(Coordinates position, POIDescription description, int id){
        super(position, id);
        this.info = description;
    }
    
    public POINode(Coordinates position, POIDescription description){
        super(position);
        this.info = description;
    }
    
    public POINode(int id) {
        super(id);
    }
    
    public POINode() {
        super();
    }

    @Override
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof POINode)) {
            return false;
        }
        POINode poinode = (POINode) other;
        return super.equals(other) && info.equals(poinode.info);
    }
    
    public POIDescription getInfo() {
        if (this.info == null) {
            this.info = new POIDescription(null, -1, null);
        }
        return this.info;
    }
    
    @Override
    protected void load(DataInput input) throws IOException {
        super.load(input);
        this.info = new POIDescription(input.readUTF(), input.readInt(), input.readUTF());
    }

    @Override
    protected void save(DataOutput output) throws IOException {
        super.save(output);
        output.writeUTF(getInfo().getName());
        output.writeInt(getInfo().getCategory());       
        output.writeUTF(getInfo().getDescription());
    }
    
    public String getName() {
        return getInfo().getName();
    }
    
}
