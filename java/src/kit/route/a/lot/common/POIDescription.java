package kit.route.a.lot.common;


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
