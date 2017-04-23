/**
 * File: SemanticTools.java
 * This file was written in loving memory of our former
 * group member Victoria Chistolini who sadly did not
 * survive project 2.5. R.I.P.
 * @author Edward (osan) Zhou
 * @author Alex Rinker
 * @author Vivek Sah
 * Class: CS461
 * Project: 3
 * Date: March 9 2017
 */

package bantam.util;

/**
 * Created by Alex on 3/6/17.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


/**
 * This class holds utility methods for use with the
 * semantic analyzer. These methods are meant to simplify
 * common tasks throughout our visitors
 */
public class SemanticTools {
    private enum reservedWords { NULL, VOID, SUPER, THIS, BOOLEAN, INT };

    private enum primitives {INT, BOOLEAN};

    public static String[] builtins = {"TextIO", "Sys", "Object", "String"};

    /**
     * Returns true if the input string is a keyword, else returns false
     * @param word the word in question
     * @return boolean corresponding to whether or not the word is a keyword
     */
    public static boolean isReservedWord(String word) {
        for ( reservedWords r : reservedWords.values()) {
            if (r.name().equalsIgnoreCase(word)) { return true; }
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

    /**
     * Returns true if the input string is a builtin class
     * @param word the input in question
     * @return boolean
     */
    public static boolean isBuiltin(String word) {
        for ( String p : builtins) {
            if (p.equals(word)) { return true; }
        }
        return false;
    }

    /**
     * Generates a string from the input file
     * @param filename the file to be read
     * @return the contents of the file in String form
     */
    public static String generateStringFromTestfile(String filename) {
        filename = "testfiles/" + filename;
        String file = "";
        try {
            // \\Z represents the end of a file
            file = new Scanner(new File(filename)).useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            System.out.println("File '" + filename + "' was unable to be read");
        }
        return file;
    }
}
