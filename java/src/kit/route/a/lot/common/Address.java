package kit.route.a.lot.common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class Address {
    
    
    private String street;
    private String housenumber;
    private String state;
    private String postcode;
    private String city;
    private String country;
    private String fullAddress;
    
    private String interpolation;
    

    public String getStreet() {
        return (street != null) ? street : "";
    }

    
    public void setStreet(String street) {
        this.street = street;
    }

    
    public String getHousenumber() {
        return (housenumber != null) ? housenumber : "";
    }

    
    public void setHousenumber(String housenumber) {
        this.housenumber = housenumber;
    }

    
    public String getState() {
        return (state != null) ? state : "";
    }

    
    public void setState(String state) {
        this.state = state;
    }

    
    public String getPostcode() {
        return (postcode != null) ? postcode: "";
    }

    
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    
    public String getCity() {
        return (city != null) ? city : "";
    }

    
    public void setCity(String city) {
        this.city = city;
    }

    
    public String getCountry() {
        return (country != null) ? country : "";
    }

    
    public void setCountry(String country) {
        this.country = country;
    }

    
    public String getFullAddress() {
        return (fullAddress != null) ? fullAddress :"";
    }

    
    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    
    public String getInterpolation() {
        return (interpolation != null) ? interpolation : "";
    }

    
    public void setInterpolation(String interpolation) {
        this.interpolation = interpolation;
    }


    public static Address loadFromStream(DataInputStream stream) throws IOException {
        Address result = new Address();
        result.street = stream.readUTF();
        result.housenumber = stream.readUTF();
        result.state = stream.readUTF();
        result.postcode = stream.readUTF();
        result.city = stream.readUTF();
        result.country = stream.readUTF();
        result.fullAddress = stream.readUTF();
        result.interpolation = stream.readUTF();
        return result;
    }

    public void saveToStream(DataOutputStream stream) throws IOException {
        stream.writeUTF(getStreet());
        stream.writeUTF(getHousenumber());
        stream.writeUTF(getState());
        stream.writeUTF(getPostcode());
        stream.writeUTF(getCity());
        stream.writeUTF(getCountry());
        stream.writeUTF(getFullAddress());       
        stream.writeUTF(getInterpolation());
    }
}
