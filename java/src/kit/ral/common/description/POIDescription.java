
/**
Copyright (c) 2012, Matthias Grundmann, Jan Jacob, Yvonne Braun, Josua Stabenow
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


public class POIDescription {
    
    private String name;
    private int category;
    private String description;
    private static final String EMPTY = "";
        
    public POIDescription(String name, int category, String description){
		setName(name);
		setCategory(category);
		setDescription(description);
	}
    
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof POIDescription)) {
            return false;
        }
        POIDescription poiDescription = (POIDescription) other;
        return name == poiDescription.name
                && category == poiDescription.category
                && description == poiDescription.description;
    }
	
	public String getName() {
    	return (name != null) ? name : "";
	}

	public void setName(String name) {
    	this.name = EMPTY.equals(name) ? null : name;
	}

	public int getCategory() {
    	return category;
	}

	public void setCategory(int category) {
    	this.category = category;
	}

	public String getDescription() {
    	return (description != null) ? description : "";
	}

	public void setDescription(String description) {
    	this.description = EMPTY.equals(description) ? null : description;
	}
}
