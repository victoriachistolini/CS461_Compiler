package bantam.util;

/**
 * Created by Alex on 3/6/17.
 */

/**
 * This class holds utility methods for use with the
 * semantic analyzer
 */
public class SemanticTools {
    private enum keywords { NULL, VOID, SUPER, THIS, BOOLEAN, INT };

    private enum primitives {INT, BOOLEAN}

    /**
     * Returns true if the input string is a keyword, else returns false
     * @param word the word in question
     * @return boolean corresponding to whether or not the word is a keyword
     */
    public static boolean isKeyword(String word) {
        for ( keywords k : keywords.values()) {
            if (k.name().equalsIgnoreCase(word)) { return true; }
        }
        return false;
    }

    /**
     * Returns true if the input string is a defined primitive else returns false
     * @param word the input in question
     * @return boolean
     */
    public static boolean isPrimitive(String word) {
        for ( primitives p : primitives.values()) {
            if (p.name().equalsIgnoreCase(word)) { return true; }
        }
        return false;
    }
}
