package kit.ral.common.description;

import static kit.ral.common.util.Util.readUTFString;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.MappedByteBuffer;


public class Address {
    
    
    private String street;
    private String housenumber;
    private String state;
    private String postcode;
    private String city;
    private String country;
    private String fullAddress;    
    private String interpolation;
    private static final String EMPTY = "";

    public String getStreet() {
        return (street != null) ? street : "";
    }

    
    public void setStreet(String street) {
        this.street = EMPTY.equals(street) ? null : street ;
    }

    
    public String getHousenumber() {
        return (housenumber != null) ? housenumber : "";
    }

    
    public void setHousenumber(String housenumber) {
        this.housenumber = EMPTY.equals(housenumber) ? null : housenumber;
    }

    
    public String getState() {
        return (state != null) ? state : "";
    }

    
    public void setState(String state) {
        this.state = EMPTY.equals(state) ? null : state;
    }

    
    public String getPostcode() {
        return (postcode != null) ? postcode: "";
    }

    
    public void setPostcode(String postcode) {
        this.postcode = EMPTY.equals(postcode) ? null : postcode;
    }

    
    public String getCity() {
        return (city != null) ? city : "";
    }

    
    public void setCity(String city) {
        this.city = EMPTY.equals(city) ? null : city;
    }

    
    public String getCountry() {
        return (country != null) ? country : "";
    }

    
    public void setCountry(String country) {
        this.country = EMPTY.equals(country) ? null : country;
    }

    
    public String getFullAddress() {
        return (fullAddress != null) ? fullAddress :"";
    }

    
    public void setFullAddress(String fullAddress) {
        this.fullAddress = EMPTY.equals(fullAddress) ? null : fullAddress;
    }

    
    public String getInterpolation() {
        return (interpolation != null) ? interpolation : "";
    }

    
    public void setInterpolation(String interpolation) {
        this.interpolation = EMPTY.equals(interpolation) ? null : interpolation;
    }


    public static Address loadFromInput(DataInput input) throws IOException {
        Address result = new Address();
        result.setStreet(input.readUTF());
        result.setHousenumber(input.readUTF());
        result.setState(input.readUTF());
        result.setPostcode(input.readUTF());
        result.setCity(input.readUTF());
        result.setCountry(input.readUTF());
        result.setFullAddress(input.readUTF());
        result.setInterpolation(input.readUTF());
        return result;
    }
    
    public static Address loadFromInput(MappedByteBuffer mmap) throws IOException {
        Address result = new Address();
        result.setStreet(readUTFString(mmap));
        result.setHousenumber(readUTFString(mmap));
        result.setState(readUTFString(mmap));
        result.setPostcode(readUTFString(mmap));
        result.setCity(readUTFString(mmap));
        result.setCountry(readUTFString(mmap));
        result.setFullAddress(readUTFString(mmap));
        result.setInterpolation(readUTFString(mmap));
        return result;
    }

    public void saveToOutput(DataOutput output) throws IOException {
        output.writeUTF(getStreet());
        output.writeUTF(getHousenumber());
        output.writeUTF(getState());
        output.writeUTF(getPostcode());
        output.writeUTF(getCity());
        output.writeUTF(getCountry());
        output.writeUTF(getFullAddress());       
        output.writeUTF(getInterpolation());
    }
}
