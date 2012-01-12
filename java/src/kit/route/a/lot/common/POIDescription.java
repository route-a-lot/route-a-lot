package kit.route.a.lot.common;


public class POIDescription {

	/** Attributes */
    	/**
     	* 
     	*/
    	private String name;
    	/**
     	* 
     	*/
    	private int category;
    	/**
     	* 
     	*/
    	private String description;
        
        public POIDescription(String name, int category, String description){
		this.name = name;
		this.category = category;
		this.description = description;
	}
	
    	public String getName() {
        	return name;
    	}
    
    	public void setName(String name) {
        	this.name = name;
    	}
    
    	public int getCategory() {
        	return category;
    	}
    
    	public void setCategory(int category) {
        	this.category = category;
    	}
    
    	public String getDescription() {
        	return description;
    	}
    
    	public void setDescription(String description) {
        	this.description = description;
    	}
}
