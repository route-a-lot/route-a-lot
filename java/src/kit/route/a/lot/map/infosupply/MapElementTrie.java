package kit.route.a.lot.map.infosupply;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.String;
import java.lang.Character;
import java.util.ArrayList;
import java.text.Collator;
import java.util.Locale;
import java.lang.ClassCastException;

import kit.route.a.lot.map.MapElement;

public class MapElementTrie {
    
    private final static String EMPTY = "";
    
    private String value;
    private MapElement word;
    private boolean suffix;
    private int count;
    private MapElementTrie[] children;   

    /**
    * Konstruktor
    */
    public MapElementTrie(String value) {
        this.value = value;
        this.children = new MapElementTrie[27]; 
        this.word = null;
        this.count = 0;
        this.suffix = false;   
    }
    
    /**
     * Konstruktor
     */
    public MapElementTrie() {
         this.value = EMPTY;
         this.children = new MapElementTrie[27]; 
         this.word = null;
         this.count = 0;
         this.suffix = false;    
     }
    /*
     * neue selcectMethode zur Auswahl eines Navigationspunktes
     */
    public ArrayList<MapElement> select(String str){
        ArrayList<MapElement> words = new ArrayList<MapElement>();
        if (str.length() == 0) {
            /*Blättinhalte werden in der DFS in words eingefügt*/
                    for(MapElementTrie node: children){
                            if(!(node == null)) {
                                    depthFirstSearch(words, node);
                            }
                     }
            return words;
        } else {
            /*prefix normalisieren*/
            str = normalize(str);
        }
        
        if(str.length() == 0){ return null; } 
        char cur = str.charAt(0);
        int index = Character.getNumericValue(cur) - 10;
        /* Sonderfälle abfangen */
        if(index < 0 || index > 25) {
              System.out.println("ungültiger Character in select");
          System.exit(0);
        }
        /*geändert, an unterschiedliche Zeichenlänge angepasst*/
        if(!(children[index] == null)){
                        MapElementTrie child = children[index];
                        String value = child.getValue();
                        if(str.length() < value.length() ){
                /*str ist nicht im Trie*/
                                return null;
                        } else if ( str.length() == value.length() ) {
                                if(str.toLowerCase().equals(value.toLowerCase())){
                                        str = "";
                    
                                }
                        } else if (value.length() < str.length()){
                                if(str.toLowerCase().startsWith(value.toLowerCase(),0)){
                                        str = str.substring(value.length());                                
                                }
                        }
        } else {
                return null;
        }
    if(!(children[index] == null) ){
                words = children[index].select(str);
        }

        return words;
    }
    /*
    * build fügt den ersten Knoten in die Kinder der Wurzel ein
    */
    public void insert(String str, MapElement element){
        if (str == null || (str.length() == 0) || element == null) {
            return;
        } else {
            /* normalisieren für die sortierung*/
            str = normalize(str);
        }
        
        if (str.length() == 0) {
            return;
        }
       
        char cur = str.charAt(0);
        int index = Character.getNumericValue(cur) - 10;
        if ( index < 0 || index > 25 ) {
            System.out.println("Zeichen "+ cur + "ist nicht in alphabet enthalten");
            System.exit(0);
        }

        /* falls es noch keine Einträge gibt neuen Knoten erstellen */
        MapElementTrie child;

        if ( children[index] == null ) {
            child = new MapElementTrie("");
            count++;
        } else {
            child = children[index];
        }
        children[index] = insert(child, str, element);
    
    
    }


    /* 
    * fügt die Blätter rekursiv ein
    *
    */
    private MapElementTrie insert(MapElementTrie parent, String str, MapElement element) {
        if (str.length() == 0){
            /*markiere Wortende*/
            parent.setSuffix(true);
            /*Wort in Blatt einfügen*/
            parent.setWord(element);
            return parent;
        }
        /* Value einfügen */
        char cur = str.charAt(0);
        parent.setValue(Character.toString(cur) );
        
        int index = 0;
        /* der Folgebuchstaben ist Schlüssel für den nächsten Eintrag */
        if(str.length() == 1) {
            index = 26;
        } else {
            cur = str.charAt(1);
            index = Character.getNumericValue(cur) - 10;
            /* Sonderfälle abfangen */
            if(index < 0 || index > 25) {
                System.out.println("Zeichen "+ cur + "ist nicht in alphabet enthalten");
                System.exit(0);
            }
        
        }
        MapElementTrie child;
        /* neuen Knoten einfügen, bei erster Traversierung */
        if ( parent.children[index] == null) {
            child = new MapElementTrie("");
            
           // parent.setCount();
        } else {
            child = parent.children[index]; 
        }
         parent.children[index] = insert( child, str.substring(1), element );
                          
        return parent;  
    }

    /* 
    * Getter - Methode für Value
    */
    public String getValue(){
        return value;
    }

    /* 
    * Setter - Methode für Value
    */
    public void setValue(String value){
        this.value = value;
    }
    /*
    * Setter Methode für children
    */
    public void setChildren(MapElementTrie[] children){
        this.children = children;
    }
    /*
    * Getter Methode für children
    */
    public MapElementTrie[] getChildren(){
        return this.children;
    }        
    /*
    * Setter Methode für Wort
    */
    public void setWord(MapElement word){
        this.word = word;
    }
    /*
    * Getter-Methode für Wort
    *
    */
    public MapElement getWord(){
        return word;
    }
    
    /*
    * Setter Methode suffix
    */
    public void setSuffix(boolean suffix){
        this.suffix = suffix;
    }
    /*
    * Getter Methode suffix
    */
    public boolean getSuffix(){
        return suffix;
    }
    /*
     * Setter Methode für count
     */
     public void setCount(int count){
         this.count = count;
     }
     /*
     * Getter Methode für count
     */
     public int getCount(){
         return count;
     }
    /*
    * Tiefensuche
    */
    public ArrayList<MapElement> depthFirstSearch(ArrayList<MapElement> words, MapElementTrie child){
        
            if (child.getSuffix()) {
                MapElement element = child.getWord();
                TraverseNonTreeEdge(element, words);
            } else {
                TraverseTreeEdge(child, words);
            }
        
        return words;
    }   
    
    //public void TraverseTreeEdge(Node child, String str, String tmp, ArrayList<String> words){
    public void TraverseTreeEdge(MapElementTrie child, ArrayList<MapElement> words){

        MapElementTrie[] children = child.getChildren();
        for(MapElementTrie node: children) {
            if( !(node == null) ) {
                depthFirstSearch(words, node);
            }
        }
    }

    public void TraverseNonTreeEdge(MapElement element,  ArrayList<MapElement> words){
            words.add(element);
    }
       
    /*
    * gibt vorerst die Nachbarn des Knotens zurück bei 
    * dem die Tiefensuche beginnt
    */
    public MapElementTrie[] getStartNodes(String prefix){
        if(prefix.length() == 0){
            return children;
        }
    MapElementTrie[] dfsStartNodes = null;
    char cur = prefix.charAt(0); 
    int index = Character.getNumericValue(cur) - 10;
    /* Sonderfälle abfangen */
        if(index < 0 || index > 25) {
            System.out.println("Zeichen "+ cur + "ist nicht in alphabet enthalten");
            System.exit(0);    
        }
    /*geändert, an unterschiedliche Zeichenlänge angepasst*/
    if(value.length() > 1){
        if(prefix.length() <= ( value.length() - 1) ){
            if (value.startsWith(prefix,1)){
                return children;
            } else {
                return null;
            }
        } else {
            /*-----------------------------------------------------*/
            if(prefix.startsWith(value.substring(1))){
                prefix = prefix.substring( (value.length() - 1) );
            /*------------------------------------------------------*/
            }
        }
    }
    /*Falls Präfix abgearbeitet ist gibt Kindknoten für Tiefensuche zurück*/
    if(prefix.length() == 0){ 
        return children;
    } else {
        /*--------------------------------------------------*/
        cur = prefix.charAt(0); 
        index = Character.getNumericValue(cur) - 10;
        /* Sonderfälle abfangen */
            if(index < 0 || index > 25) {
                System.out.println("Zeichen "+ cur + "ist nicht in alphabet enthalten");
                System.exit(0);    
            }
            /*-----------------------------------------------*/
        MapElementTrie nextNeighbor = children[index];
        if(!(nextNeighbor == null) ){
            dfsStartNodes = nextNeighbor.getStartNodes(prefix.substring(1));
        } else {
            return null;
        }
    }
    return dfsStartNodes;
    }
    
    /*
    * Methode zum normalisieren des Textes
    */
    public static String normalize(String str){
        if(str == null){ 
            return null; 
        }
        str = str.replaceAll("ß", "ss");
        Collator collator = Collator.getInstance(Locale.GERMAN);
        collator.setStrength(Collator.PRIMARY);
        String[] org = str.split("");
        String alph = "abcdefghijklmnopqrstuvwxyz";     
        String[] alphabet = alph.split("");
        /*Normalisieren*/
        for(int i = 1; i < org.length; i++){
                for(int j = 1; j < alphabet.length; j++){
                try{
                    if(collator.compare(org[i],alphabet[j]) == 0){
                        /*normalisieren*/
                        org[i] = alphabet[j];
                        j = alphabet.length;
                    }
                    /* Zeichen ohne Sonderfallbehandlung entfernen */
                    if(j == (alphabet.length - 1) ){
                        org[i] = "";
                    }
                } catch(ClassCastException e) {
                    /*Zeichen die keinen Strings entsprechen entfernen*/
                    org[i] = "";
                }
                }       
            
        }
        str = "";
        /*String generieren*/
        for(int j = 0; j < org.length; j++){
            str = str + org[j];
        }
        
        return str;
    }

    /*
    * sucht Worte die mit dem Parameter praefix beginnen
    * und gibt diese als ArrayList zurück
    */
    public ArrayList<MapElement> search(String prefix){
        if(prefix == null){
            return  null;
        } else {
            prefix = normalize(prefix);
        }
        if(prefix.length() == 0){ return null;}
        MapElementTrie[] children = getStartNodes(prefix);
        if(children == null) {
            System.out.println("No words found");
            return new ArrayList<MapElement>();
        }
        ArrayList<MapElement> words = new ArrayList<MapElement>();
        for(MapElementTrie node: children){
            if(!(node == null)) {
                depthFirstSearch(words, node);
            }
        }
        return words;   
    }
    
    /*
     * kompaktifizieren des Tries, Breitensuche
     */
     public void compactify(){
         /*Breitensuche: Liste initialisieren*/
         String value;
         String otherValue;
         ArrayList<MapElementTrie> allChildren = new ArrayList<MapElementTrie>();
         for(MapElementTrie node: children){
             if(node != null && !(node.getSuffix()) ){
                 allChildren.add(node);
                 /*Sprung in dfsComp*/
                 //dfsCompact(node);
             }
         }
         while(allChildren.size() > 0){
             MapElementTrie node = allChildren.remove(0);
             node.setCount(node.countChild(node));
             
                     if(node.getCount() == 1 ){
             //System.out.println("Kinder == 1, Value: " + node.getValue() );
                     MapElementTrie child = getChild(node);
                                         value = node.getValue();
                                         if(!(child.getSuffix()) ){
                                             otherValue = child.getValue();
                         node.setValue( value + otherValue );
                         //System.out.println("neuer Knotenwert: " + node.getValue());
                                                 node.setChildren( child.getChildren() );
                         allChildren.add(node);
                         
                     }
             } else {
             //System.out.println("Kinder > 1, Value: " + node.getValue() );
             MapElementTrie[] nextLayer = node.getChildren();
                 for(MapElementTrie child: nextLayer){
                     if(child != null && !(child.getSuffix() ) ){
                                         allChildren.add(child);
                                 }

                 }   
             }
             
         }//end while
         
     
     }
                     

    /*
    * gibt einziges Kind zurück
    */
    private MapElementTrie getChild(MapElementTrie node){
        for(MapElementTrie child: node.getChildren()){
            if(!(child == null)){
                return child;
            }
        }
        return null;
    }
        
    private int countChild(MapElementTrie node){
        int count = 0;
        for(MapElementTrie child: node.getChildren()){
                        if(!(child == null)){
               
                                    count++;
                
                        }      
                }
                return count;
    }
                
   

    public static MapElementTrie loadFromInput(DataInput input) throws IOException {      
        MapElementTrie result = new MapElementTrie();
        result.value = input.readUTF();
        result.word = (input.readBoolean()) ? MapElement.loadFromInput(input, true) : null;
        result.suffix = input.readBoolean();
        result.count = input.readInt();
        int len = input.readInt();
        for (int i = 0; i < len; i++) {
            if (input.readBoolean()) {
                result.children[i] = loadFromInput(input);
            }
        }
        return result;
    }
    
    public void saveToOutput(DataOutput output) throws IOException {
        output.writeUTF(value);
        output.writeBoolean(word != null);
        if (word != null) {
            MapElement.saveToOutput(output, word, true);
        }
        output.writeBoolean(suffix);
        output.writeInt(count); 
        output.writeInt(children.length);
        for (int i = 0; i < children.length; i++) {
            output.writeBoolean(children[i] != null);
            if (children[i] != null) {
                children[i].saveToOutput(output);
            }
        }        
    }
    
}