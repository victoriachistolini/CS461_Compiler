package bantam.util;

/**
 * Created by Alex on 3/6/17.
 */

/**
 * This class holds utility methods for use with the
 * semantic analyzer
 */
public class SemanticTools {
    private static enum keywords { NULL, VOID, SUPER, THIS, BOOLEAN, INT };

    /**
     * Returns true if the input string is a keyword, else returns false
     * @param word the word in question
     * @return boolean corresponding to whether or not the word is a keyword
     */
    public static boolean isKeyword(String word) {
        for ( keywords k : keywords.values()) {
            if (k.name().equals(word)) { return true; }
        }
        return false;
    }
}
