package kit.ral.common;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import kit.ral.common.util.StringUtil;

public class StringTrie {

    // FIELDS
    
    /**
     * Key prefix associated to this node. This initially always is a
     * single character (apart from the root). However, a subsequent call
     * to <code>compactify()</code> may change that (-> Patricia-Trie).
     */
    private String keypart;

    /**
     * The value associated with this node. Used as MapElement ID.
     */
    private int value = -1;
    private StringTrie[] children = new StringTrie[27];

    
    // CONSTRUCTOR
    
    /**
     * Creates a new empty StringTrie.
     */
    public StringTrie() {
        keypart = "";
    }

    
    // BASE FUNCTIONALITY
    
    /**
     * Inserts the given value into the StringTrie at the key position.
     * Values with identical keys are replaced. Calling
     * this method after compactifying the StringTrie may result in errors.
     * 
     * @param key
     * @param value
     * @throws IllegalArgumentException
     *             key is <code>null</code> or key has length 0
     */
    public void insert(String key, int value) {
        if ((key == null) || (key.length() == 0)) {
            throw new IllegalArgumentException();
        }
        insert(StringUtil.normalize(key), 0, value);
    }    
    
    /**
     * Returns all values associated with keys that start with the given key prefix.
     * @param key prefix
     * @return a list of values
     * @throws IllegalArgumentException key is <code>null</code>
     */
    public ArrayList<Integer> search(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        ArrayList<Integer> values = new ArrayList<Integer>();
        search(StringUtil.normalize(key), 0, values);
        return values;
    }    
     
    /**
     * Converts the StringTrie into a PatriciaTrie. Afterwards
     * <code>insert()</code> should not be called any more.
     */
    public void compactify() {
        StringTrie onlyChild = null;
        do {
            if (value >= 0) {
                break;
            }
            // find only child, or null else
            for (StringTrie child : children) {
                if (child != null) {
                    onlyChild = (onlyChild == null) ? child : null;
                    if (onlyChild == null) {
                        break;
                    }
                }
            }
            // merge this with the only child
            if (onlyChild != null) {
                this.children = onlyChild.children;
                this.keypart += onlyChild.keypart;
                this.value = onlyChild.value;
            }
        } while (onlyChild != null);
        for (StringTrie child : children) {
            if (child != null) {
                child.compactify();
            }
        }
    }
    
    
    // RECURSIVE HELPER FUNCTIONS

    /**
     * Inserts the given value into the StringTrie at the key position.
     * Only the key suffix beginning at position
     * <code>charIndex</code> is taken into account.
     * 
     * @param key
     * @param charIndex
     * @param value
     */
    private void insert(String key, int charIndex, int value) {
        // TODO maybe PatriciaTrie insert handling
        if (charIndex >= key.length()) {
            this.value = value;
        } else {
            int index = StringUtil.getCharIndex(key.charAt(charIndex));
            if (children[index] == null) {
                children[index] = new StringTrie();
                children[index].keypart = String.valueOf(key.charAt(charIndex));
            }
            children[index].insert(key, charIndex + 1, value);
        }
    }
   
    /**
     * Returns all values associated with keys that start with the given key prefix.
     * Only the second part of key (beginning at position <code>charIndex</code>)
     * is taken into account.
     * @param key
     * @param charIndex
     * @param values
     */
    private void search(String key, int charIndex, ArrayList<Integer> values) {
        int minLen = Math.min(key.length() - charIndex, keypart.length());
        // if keypart is prefix of (offset) key, or vice versa
        if (key.regionMatches(charIndex, keypart, 0, minLen)) {
            // add this entry and all child entries if expr ends within value
            if (key.length() - charIndex <= keypart.length()) {
                getAllValues(values);
            } else { // select within child entries
                charIndex += keypart.length();
                StringTrie child = children[StringUtil.getCharIndex(key.charAt(charIndex))];
                if (child != null) {
                    child.search(key, charIndex, values);
                }
            }
        }
    }
    
    /**
     * Adds all values in the StringTrie to the given list.
     * @param values a list that is to be filled with values.
     */
    private void getAllValues(ArrayList<Integer> values) {
        if (value >= 0) {
            values.add(value);
        }
        for (StringTrie child : children) {
            if (child != null) {
                child.getAllValues(values);
            }
        }
    }
    
    
    // I/O FUNCTIONALITY

    /**
     * Loads a complete StringTrie from the given source.
     * @param input the source
     * @return the loaded StringTrie
     * @throws IOException
     */
    public static StringTrie loadFromInput(DataInput input) throws IOException {
        StringTrie result = new StringTrie();
        result.keypart = input.readUTF();
        result.value = input.readInt();
        for (int i = 0; i < result.children.length; i++) {
            if (input.readBoolean()) {
                result.children[i] = loadFromInput(input);
            }
        }
        return result;
    }

    /**
     * Saves the complete StringTrie to the given destination.
     * 
     * @param output
     *            the destination (e.g. a stream)
     * @throws IOException
     */
    public void saveToOutput(DataOutput output) throws IOException {
        output.writeUTF(keypart);
        output.writeInt(value);
        for (int i = 0; i < children.length; i++) {
            output.writeBoolean(children[i] != null);
            if (children[i] != null) {
                children[i].saveToOutput(output);
            }
        }
    }

    // TODO write equals

}
