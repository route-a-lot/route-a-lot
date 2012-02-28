package kit.route.a.lot.common;

import java.lang.String;
import java.lang.Character;
import java.util.ArrayList;
import java.text.Collator;
import java.util.Locale;
import java.lang.ClassCastException;

public class StringTrie<T>{

    
    private String value;
    private StringTrie<T> [] children;
    private T word;
    private int count;
    private final String ISROOT = "$";
    private boolean suffix; 

    /**
    * Konstruktor
    */
    public StringTrie(String value) {

        this.value = value;
        this.children = new StringTrie[27]; 
        this.word = null;
        this.count = 0;
        this.suffix = false;
    
    }
    /**
    * Root
    */
    
     public StringTrie() {

                this.value = ISROOT;
                this.children = new StringTrie[27];
        this.word = null;
        this.count= 0;
        this.suffix = false;
        }

    /*
    * build fügt den ersten Knoten in die Kinder der Wurzel ein
    */
    public void insert(String str, T element){
        if (str == null || (str.length() == 0) || element == null) {
            return;
        } else {
            /* normalisieren für die sortierung*/
            str = normalize(str);
        }
        /* else if ( !(str.endsWith("$") ) ) {
            word = str;
                    str = str + "$";
            } */
        
        char cur = str.charAt(0);
        /*Umlaute entfernen*/
        //cur = normalize(cur);
    
        int index = Character.getNumericValue(cur) - 10;
        if ( index < 0 || index > 25 ) {
            //index = 27;
            System.out.println("Zeichen "+ cur + "ist nicht in alphabet enthalten");
            System.exit(0);
        }

        /* falls es noch keine Einträge gibt neuen Knoten erstellen */
        StringTrie child;

        if ( children[index] == null ) {
            child = new StringTrie("$");
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
    private StringTrie<T> insert(StringTrie<T> parent, String str, T element) {
        System.out.println("insert String: " + str);
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
        /*
        if(cur == '$'){
            //Wortende markieren
            parent.setSuffix(true);
            //Wort in Blatt einfügen
            parent.setWord(word);
            return parent;
        }
        */
        int index = 0;
        /* der Folgebuchstaben ist Schlüssel für den nächsten Eintrag */
        if(str.length() == 1) {
            index = 26;
        } else {
            cur = str.charAt(1);
            System.out.println("insert Buchstabe: " + cur);
            /* Umlaute abfangen */
            //cur = normalize(cur); 
            index = Character.getNumericValue(cur) - 10;
            /* Sonderfälle abfangen */
            if(index < 0 || index > 25) {
                //index = 27;
                System.out.println("Zeichen "+ cur + "ist nicht in alphabet enthalten");
                        System.exit(0);
            }
        
        }
        StringTrie<T> child;
        System.out.println("insert at Index: " + index);
        /* neuen Knoten einfügen, bei erster Traversierung */
        if ( parent.children[index] == null) {
            child = new StringTrie("$");
            //parent.setCount();
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
    public void setChildren(StringTrie[] children){
        this.children = children;
    }
    /*
    * Getter Methode für children
    */
    public StringTrie[] getChildren(){
        return this.children;
    }        
    /*
    * Setter Methode für Wort
    */
    public void setWord(T word){
        this.word = word;
    }
    /*
    * Getter-Methode für Wort
    *
    */
    public T getWord(){
        return word;
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
    * Tiefensuche
    */
    public ArrayList<T> depthFirstSearch( ArrayList<T> words, StringTrie<T> child){
    
            
         //String tmp = child.getValue();
                    if (child.getSuffix()) {
                T element = child.getWord();
                            TraverseNonTreeEdge(element, words);
            } else {
                TraverseTreeEdge(child, words);
            }
        
        return words;
    }   
    
    //public void TraverseTreeEdge(Node child, String str, String tmp, ArrayList<String> words){
    public void TraverseTreeEdge(StringTrie<T> child, ArrayList<T> words){

        
        StringTrie[] children = child.getChildren();
        for(StringTrie<T> node: children) {
            if( !(node == null) ) {
                depthFirstSearch(words, node);
            }
        }
    }

    public void TraverseNonTreeEdge(T element,  ArrayList<T> words){
            words.add(element);
    }
       
    /*
    * gibt vorerst die Nachbarn des Knotens zurück bei 
    * dem die Tiefensuche beginnt
    */
    public StringTrie[] getStartNodes(String prefix){
        if(prefix.length() == 0){
            return children;
        }
    StringTrie[] dfsStartNodes = null;
    char cur = prefix.charAt(0);
    //cur = normalize(cur); 
    int index = Character.getNumericValue(cur) - 10;
    /* Sonderfälle abfangen */
        if(index < 0 || index > 25) {
              index = 27;
        }
    /*geändert, an unterschiedliche Zeichenlänge angepasst*/
    if(value.length() > 1){
        if(prefix.length() < ( value.length() - 1) ){
            if (value.startsWith(prefix,1)){
                return children;
            } else {
                return null;
            }
        } else {
            prefix = prefix.substring( (value.length() - 1) );
        }
    }
    /*Falls Präfix abgearbeitet ist gibt Kindknoten für Tiefensuche zurück*/
    if(prefix.length() == 0){ 
        return children;
    } else {
        StringTrie<T> nextNeighbor = children[index];
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
    public String normalize(String str){
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
                        org[i] = alphabet[j];
                        j = alphabet.length;
                    }
                    /*Sonderfälle*/
                    if(j == (alphabet.length - 1) ){
                        org[i] = "";
                    }
                } catch(ClassCastException e) {
                    org[i] = "";
                }
                }       
            
        }
        str = "";
        for(int j = 0; j < org.length; j++){
            str = str + org[j];
        }
        
        return str;
    }

    /*
    * sucht Worte die mit dem Parameter praefix beginnen
    * und gibt diese als ArrayList zurück
    */
    public ArrayList<T> search(String prefix){
        if(prefix == null){
            return  null;
        } else {
            prefix = normalize(prefix);
        }
        StringTrie[] children = getStartNodes(prefix);
        if(children == null) {
            System.out.println("No words found");
            return null;
        }
        ArrayList<T> words = new ArrayList<T>();
        for(StringTrie<T> node: children){
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
        ArrayList<StringTrie<T>> allChildren = new ArrayList<StringTrie<T>>();
        for(StringTrie<T> node: children){
            if(node != null && !(node.getSuffix()) ){
                allChildren.add(node);
                /*Sprung in dfsComp*/
                //dfsCompact(node);
            }
        }
        while(allChildren.size() > 0){
            StringTrie<T> node = allChildren.remove(0);
            node.setCount(node.countChild(node));
                    if(node.getCount() == 1 ){
                    StringTrie<T> child = getChild(node);
                                        value = node.getValue();
                                        if(!(child.getSuffix()) ){
                                            otherValue = child.getValue();
                        node.setValue( value + otherValue );
                                                node.setChildren( child.getChildren() );
                        
                    }
            }
            StringTrie[] nextLayer = node.getChildren();
            for(StringTrie<T> child: nextLayer){
                if(child != null && !(child.getSuffix() ) ){
                                    allChildren.add(child);
                            }

            }
            
        }//end while
        
    
    }
                    

    /*
    * gibt einziges Kind zurück
    */
    private StringTrie<T> getChild(StringTrie<T> node){
        for(StringTrie<T> child: node.getChildren()){
            if(!(child == null)){
                return child;
            }
        }
        return null;
    }
        
    private int countChild(StringTrie<T> node){
        int count = 0;
        for(StringTrie<T> child: node.getChildren()){
                        if(!(child == null)){
                if(!(child.getSuffix() ) ){
                                    count++;
                }
                        }
                }
                return count;
    }
                
   

    /*public static StringTrie loadFromInput(DataInput input) throws IOException {      
        StringTrie result = new StringTrie();
        int treeSize = input.readInt();
        for(int i = 0; i < treeSize; i++) {
            result.tree.add(MapElement.loadFromInput(input, true));
        }
        result.maxLength = input.readInt();
        return result;
    }
    
    public void saveToOutput(DataOutput output) throws IOException {
        output.writeInt(tree.size());
        for(MapElement element: tree) {
            MapElement.saveToOutput(output, element, true);
        }
        output.writeInt(maxLength);          
    }
    */
}
