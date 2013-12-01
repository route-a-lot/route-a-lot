
/**
Copyright (c) 2012, Matthias Grundmann, Josua Stabenow
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * The names of the contributors may not be used to endorse or promote products
          derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
**/

package kit.ral.common.description;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.MappedByteBuffer;

import static kit.ral.common.util.Util.readUTFString;


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
