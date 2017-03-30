/* Bantam Java Compiler and Language Toolset.

   Copyright (C) 2009 by Marc Corliss (corliss@hws.edu) and 
                         David Furcy (furcyd@uwosh.edu) and
                         E Christopher Lewis (lewis@vmware.com).
   ALL RIGHTS RESERVED.

   The Bantam Java toolset is distributed under the following 
   conditions:

     You may make copies of the toolset for your own use and 
     modify those copies.

     All copies of the toolset must retain the author names and 
     copyright notice.

     You may not sell the toolset or distribute it in 
     conjunction with a commerical product or service without 
     the expressed written consent of the authors.

   THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS 
   OR IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE 
   IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A 
   PARTICULAR PURPOSE. 
*/

/**
 * File: FilenameVisitor.java
 * @author Edward (osan) Zhou
 * @author Alex Rinker
 * @author Vivek Sah
 * Class: CS461
 * Project: 4A
 * Date: March 30 2017
 */

package bantam.codegenmips;

import bantam.util.ClassTreeNode;

import java.io.PrintStream;

import java.util.Calendar;
import bantam.visitor.StringConstantsVisitor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Locale;

/**
 * The <tt>MipsCodeGenerator</tt> class generates mips assembly code
 * targeted for the SPIM emulator.  Note: this code will only run
 * under SPIM.
 * <p/>
 * This class is incomplete and will need to be implemented by the student.
 */
public class MipsCodeGenerator {
    /**
     * Root of the class hierarchy tree
     */
    private ClassTreeNode root;

    /**
     * Print stream for output assembly file
     */
    private PrintStream out;

    /**
     * Assembly support object (using Mips assembly support)
     */
    private MipsSupport assemblySupport;

    /**
     * Boolean indicating whether garbage collection is enabled
     */
    private boolean gc = false;

    /**
     * Boolean indicating whether optimization is enabled
     */
    private boolean opt = false;

    /**
     * Boolean indicating whether debugging is enabled
     */
    private boolean debug = false;

    /**
     * MipsCodeGenerator constructor
     *
     * @param root    root of the class hierarchy tree
     * @param outFile filename of the assembly output file
     * @param gc      boolean indicating whether garbage collection is enabled
     * @param opt     boolean indicating whether optimization is enabled
     * @param debug   boolean indicating whether debugging is enabled
     */
    public MipsCodeGenerator(ClassTreeNode root, String outFile,
                             boolean gc, boolean opt, boolean debug) {
        this.root = root;
        this.gc = gc;
        this.opt = opt;
        this.debug = debug;

        try {
            out = new PrintStream(new FileOutputStream(outFile));
            assemblySupport = new MipsSupport(out);
        } catch (IOException e) {
            // if don't have permission to write to file then report an error and exit
            System.err.println("Error: don't have permission to write to file '" + outFile + "'");
            System.exit(1);
        }
    }

    /**
     * Generate assembly file
     * <p/>
     * In particular, will need to do the following:
     * 1 - start the data section
     * 2 - generate data for the garbage collector
     * 3 - generate string constants
     * 4 - generate class name table
     * 5 - generate object templates
     * 6 - generate dispatch tables
     * 7 - start the text section
     * 8 - generate initialization subroutines
     * 9 - generate user-defined methods
     * See the lab manual for the details of each of these steps.
     */
    public void generate() {
        // comment out
//        throw new RuntimeException("MIPS code generator unimplemented");

        //Generate the File Header
        generateHeader();

        //1 - Start the data section
        assemblySupport.genDataStart();

        //2 - Generate data for the garbage collector
        generateGCData();

        //3 - Generate String Constants for all used Strings
        StringConstWriter(root);

        //3.5 - Generate Strings for Class Names
        ArrayList<String> classNames = generateClassStrings();

        //4 - Generate the class name table
        generateClassNameTable(classNames);

        //6 - generate dispatch tables
        generateClassDispatchTables();
//        root.getChildrenList().forEachRemaining( x ->
//            System.out.println(x.getName())
//        );

        // add code below...
    }

    public void StringConstWriter(ClassTreeNode root){
        StringConstantsVisitor strVisitor = new StringConstantsVisitor(root, assemblySupport);

        Map<String, String> strContainer = strVisitor.getStringConstants();
        assemblySupport.genStringConst(strContainer);
    }

    //Helper Functions for the generate() method

    /**
     * This function generates a file header for the MIPS assembly file
     * containing information about the authors, date, and compiled .btm file
     */
    private void generateHeader() {
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
        );
        out.println();
    }

    /**
     * This function generates data for the Garbage collector to use in the
     * MIPS assembly file.
     * Currently this method only sets the gc_flag to 0
     */
    private void generateGCData() {
        assemblySupport.genLabel("gc_flag");
        assemblySupport.genWord("0");
    }

    /**
     * This function generates the class_name_table based on
     * all of the classes in the program as well as the five
     * base class templates.
     */
    private void generateClassNameTable(ArrayList<String> classNames) {
        for (String className : classNames) {
            assemblySupport.genWord(className);
        }
    }

    /**
     * This method returns a list of class name labels used in the MIPS file
     * based on the number of classes in the program
     * @return classNames an arraylist of classname labels
     */
    private ArrayList<String> generateClassStrings() {
        ArrayList<String> classNames = new ArrayList<>();
        generateClassStrings(root, classNames);
        return classNames;
    }

    /**
     * this is the recursive helper method for generateClassStrings which
     * loops through each parent class' children and generates their MIPS String
     * value as well as updates the list of names for use in the parent function.
     * @param parent the classTreeNode who's String is to be generated
     * @param names the list of all current MIPS class labels referencing these classes
     */
    private void generateClassStrings(ClassTreeNode parent, ArrayList<String> names) {
        String label = "class_name_" + names.size();
        assemblySupport.genStringConstTemplate(parent.getName(), label);
        names.add(label);
        parent.getChildrenList().forEachRemaining( child ->
            generateClassStrings(child, names)
        );
    }

    /**
     * Generates the dispatch tables for classes
     */
    private void generateClassDispatchTables() {
        ClassDispatchVisitor CDV = new ClassDispatchVisitor(root, assemblySupport);
        CDV.generateDispatchTables();
    }
}
