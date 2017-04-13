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
 * File: MipsCodeGenerator.java
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

import java.util.*;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * The <tt>MipsCodeGenerator</tt> class generates mips assembly code
 * targeted for the SPIM emulator.  Note: this code will only run
 * under SPIM.
 * <p/>
 * This class is incomplete and will need to be implemented by the student.
 */
public class MipsCodeGenerator {

    /** Root of the class hierarchy tree */
    private ClassTreeNode root;

    /** Print stream for output assembly file */
    private PrintStream out;

    /** Assembly support object (using Mips assembly support) */
    private MipsSupport assemblySupport;

    /** Map containing the labels associated with each class */
    private Map<String, String> classNames;

    /** Map containing the method associated with each class */
    private Map<String, Set<String>> classMethods;

    /** Map which connects String constants to their label */
    private Map<String, String> stringLabels;

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
        this.classNames = new HashMap<>();
        // comment out
        //throw new RuntimeException("MIPS code generator unimplemented");

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

        //5 - Generate the object templates
        generateObjectTemplates();

        //6 - generate dispatch tables
        generateClassDispatchTables();

        //7 - Start the Text section
        out.println();
        assemblySupport.genTextStart();
        generateText();
    }

    //Below are the Helper Functions for the generate() method

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
     * Generates the String Constants for the program associated with the
     * input root
     * @param root
     */
    public void StringConstWriter(ClassTreeNode root){
        StringConstantsVisitor strVisitor = new StringConstantsVisitor(root, assemblySupport);

        this.stringLabels = strVisitor.getStringConstants();
        assemblySupport.genStringConst(this.stringLabels);
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
     * This method returns a list of class name labels used in the MIPS file
     * based on the number of classes in the program
     * @return classNames an arraylist of classname labels
     */
    private ArrayList<String> generateClassStrings() {
        ArrayList<String> classNames = new ArrayList<>();

        // The following code will be run on Object first (root) thus
        // Guaranteeing that Object has a labelId of 0
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
    private void generateClassStrings(
            ClassTreeNode parent,
            ArrayList<String> names) {

        // Generate the Label ID based on the number of classes
        // Get the name of the class
        int labelId = names.size();
        String parentName = parent.getName();

        //This is where we create the label for the class
        this.classNames.put(parentName, String.valueOf(labelId));
        String label = "class_name_" + labelId;
        assemblySupport.genStringConstTemplate(parentName, label);

        // Add the new label to the list
        names.add(label);

        HashSet<ClassTreeNode> children = new HashSet<>();

        //We then need to repeat this process for each child class
        //But we must make sure that string is first
        //Because Object is the first to be generated, String is
        //Guaranteed to be given index 1
        parent.getChildrenList().forEachRemaining( child -> {
            if(child.getName() == "String") {
                generateClassStrings(child, names);
            } else {
                children.add(child);
            }
        });

        // Generate the class strings for the children
        for(ClassTreeNode child : children ) {
            generateClassStrings(child, names);
        }
    }

    /**
     * This function generates the class_name_table based on
     * all of the classes in the program as well as the five
     * base class templates.
     */
    private void generateClassNameTable(ArrayList<String> classNames) {
        assemblySupport.genLabel("class_name_table");

        //Generate the words linking classes to their strings
        for (String className : classNames) {
            assemblySupport.genWord(className);
        }

        //Generate the references to templates for each class
        for (String className : this.classNames.keySet()) {
            assemblySupport.genGlobal( className + "_template");
        }
    }

    /**
     * This method generates the object templates in MIPS based on the
     * built in and user defined classes provided in the program.
     */
    private void generateObjectTemplates() {
        //Generate the templates for each class
        TemplateGenerator generator = new TemplateGenerator();
        generator.generateClassTemplates(this.root, this.assemblySupport, this.classNames);
    }

    /**
     * Generates the dispatch tables for classes
     */
    private void generateClassDispatchTables() {
        ClassDispatchVisitor CDV = new ClassDispatchVisitor(root, this.assemblySupport);
        CDV.generateDispatchTables();
        this.classMethods = CDV.getClassMethods();

        //Make the globals to allow us to reference these dispatch tables
        for(String class_ : this.classNames.keySet()) {
            this.assemblySupport.genGlobal(class_ + "_dispatch_table");
        }
    }

    private void generateText() {
        CodeGeneratorVisitor textGenerator = new CodeGeneratorVisitor(
                this.root,
                this.assemblySupport,
                this.out,
                this.classNames,
                this.stringLabels
        );
        textGenerator.generateText();
    }
}
