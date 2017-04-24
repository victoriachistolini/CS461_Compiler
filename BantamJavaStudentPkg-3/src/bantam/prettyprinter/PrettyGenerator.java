/**
 * File: PrettyGenerator.java
 * @author Edward (osan) Zhou
 * @author Alex Rinker
 * @author Vivek Sah
 * Class: CS461
 * Project: Extension 1
 * Date: April 24 2017
 */

package bantam.prettyprinter;

import bantam.util.ClassTreeNode;

import java.io.PrintStream;

import java.util.*;

import java.io.FileOutputStream;
import java.io.IOException;


/**
 * This class generates a nicely formatted
 */
public class PrettyGenerator {
    /** Root of the class hierarchy tree */
    private ClassTreeNode root;

    /** Print stream for output btm file */
    private PrintStream out;

    /** Pretty Printer support object (using Mips assembly support) */
    private PrettySupport prettySupport;

    /**
     * MipsCodeGenerator constructor
     *
     * @param root    root of the class hierarchy tree
     * @param outFile filename of the btm output file
     */
    public PrettyGenerator(ClassTreeNode root, String outFile) {
        this.root = root;

        try {
            out = new PrintStream(new FileOutputStream(outFile));
            prettySupport = new PrettySupport(out);
        } catch (IOException e) {
            // if don't have permission to write to file then report an error and exit
            System.err.println("Error: don't have permission to write to file '" + outFile + "'");
            System.exit(1);
        }
    }

    /**
     * Generate the Pretty Printed btm file
     */
    public void prettyGenerate() {

        //Generate the File Header
        generateHeader();

        throw new RuntimeException("Pretty Printer unimplemented");
    }

    //Below are the Helper Functions for the generate() method

    /**
     * This function generates a file header for the MIPS assembly file
     * containing information about the authors, date, and compiled .btm file
     */
    private void generateHeader() {
        //TODO Change this to work with the pretty support file
        /**
        assemblySupport.genComment("Authors: Vivek Sah, Alex Rinker, Ed Zhou");
        Calendar cal = Calendar.getInstance();
        String month = cal.getDisplayName(
                Calendar.MONTH, Calendar.LONG, Locale.getDefault()
        );
        int year = cal.get(Calendar.YEAR);
        assemblySupport.genComment("Date: " + month + " " + year);

        FilenameVisitor fVisitor = new FilenameVisitor();

        assemblySupport.genComment(
                "Compiled From Sources: " + fVisitor.getMainFilename(root)
        );*/
        out.println();
    }
}
