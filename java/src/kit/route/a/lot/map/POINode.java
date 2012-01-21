package kit.route.a.lot.map;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;

public class POINode extends Node {

    private POIDescription info;

    public POINode(Coordinates position, POIDescription description){
        super(position);
        this.info = description;
    }
    
    public POINode() {
        this(null, null);
    }

    public POIDescription getInfo() {
        return info;
    }
    
    @Override
    protected void load(DataInputStream stream) throws IOException {
        super.load(stream);
        this.info = new POIDescription(stream.readUTF(), stream.readInt(), stream.readUTF());
    }

    @Override
    protected void save(DataOutputStream stream) throws IOException {
        super.save(stream);
        //TODO DISCUSS: implement POIDescription.saveToStream() etc. ?
        stream.writeUTF(this.info.getName());
        stream.writeInt(this.info.getCategory());       
        stream.writeUTF(this.info.getDescription());
    }
}
