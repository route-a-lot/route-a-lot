package kit.ral.map;
import static kit.ral.common.util.Util.readUTFString;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.MappedByteBuffer;

import kit.ral.common.Coordinates;
import kit.ral.common.description.Address;
import kit.ral.common.description.POIDescription;

public class POINode extends Node {

    private POIDescription info = null;
    private Address address;
    
    public POINode(Coordinates position, POIDescription description, Address address){
        super(position);
        this.info = description;
        this.address = address;
    }
    
    public POINode(int id) {
        super(id);
    }
    
    public POINode() {
        super();
    }
    
    
    @Override
    public String getName() {
        return getInfo().getName();
    }
    
    @Override
    public String getFullName() {  
        if ((address == null || address.getCity().length() == 0)) {
            return getName();
        } else {
            return getName() + ", " + address.getCity();
        }
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
    protected void load(MappedByteBuffer mmap) throws IOException {
        super.load(mmap);
        this.info = new POIDescription(readUTFString(mmap), mmap.getInt(), readUTFString(mmap));
    }

    @Override
    protected void save(DataOutput output) throws IOException {
        super.save(output);
        output.writeUTF(getInfo().getName());
        output.writeInt(getInfo().getCategory());       
        output.writeUTF(getInfo().getDescription());
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
}
