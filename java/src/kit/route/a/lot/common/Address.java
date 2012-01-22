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


    public static Address loadFromStream(DataInputStream stream) throws IOException {
        Address result = new Address();
        result.setStreet(stream.readUTF());
        result.setHousenumber(stream.readUTF());
        result.setState(stream.readUTF());
        result.setPostcode(stream.readUTF());
        result.setCity(stream.readUTF());
        result.setCountry(stream.readUTF());
        result.setFullAddress(stream.readUTF());
        result.setInterpolation(stream.readUTF());
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
