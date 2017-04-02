/**
 * File: ClassDispatchVisitor.java
 * @author Edward (osan) Zhou
 * @author Alex Rinker
 * @author Vivek Sah
 * Class: CS461
 * Project: 4A
 * Date: March 30 2017
 */

package bantam.codegenmips;

import bantam.ast.*;
import bantam.util.ClassTreeNode;
import bantam.visitor.Visitor;

import java.util.*;

/**
 * generates mips class templates in the .data section
 */
public class ClassDispatchVisitor extends Visitor {

    public static final String[] BUILT_IN_NAMES = new String[] {
            "Object", "String", "Sys", "TextIO"
    };
    public static final Set<String> BUILT_IN_CLASSES = new HashSet<String>(
            Arrays.asList(BUILT_IN_NAMES)
    );

    /** Object class tree node */
    private ClassTreeNode root;

    /** Class for generating mips*/
    private MipsSupport mipsSupport;

    /** Current class */
    private String currClass;

    /** boolean representing whether the class is built in or not */
    private boolean builtin;

    /** List of current methods */
    private ArrayList<String> currMethods;

    /** A map keeping track of the methods associated with each class */
    private Map<String, Set<String>> classMethods;

    /**
     * Constructor for the class, sets up the root and mipSupport fields
     * @param root
     * @param mipsSupport
     */
    ClassDispatchVisitor(ClassTreeNode root, MipsSupport mipsSupport) {
        this.root = root;
        this.mipsSupport = mipsSupport;
        this.classMethods = new HashMap<>();
        this.builtin = false;
        currMethods = new ArrayList<>();
    }

    /**
     * Generate dispatch tables for the program
     */
    public void generateDispatchTables() {
        generateDispatchTables(root, new ArrayList<>());
    }

    /**
     * returns a Map which contains the methods associated with
     * each of the classes in the program
     * @return classMethods, the Map storing the desired information
     */
    public Map<String, Set<String>> getClassMethods() {
        return this.classMethods;
    }

    /**
     * This method takes in a class node, and the parent methods for this node and
     * generates a dispatch table based on the methods within this class and the methods
     * already listed in the parentMethods array
     * @param parent the Class node for the current class
     * @param parentMethods a list of methods for the parent of this class node
     */
    private void generateDispatchTables(ClassTreeNode parent, ArrayList<String> parentMethods) {
        parent.getASTNode().accept(this);
        ArrayList<String> finalMethods = new ArrayList<>(parentMethods);
        ArrayList<String> tempMethods = new ArrayList<>(currMethods);
        for(int i = 0; i<parentMethods.size(); i++) {
            for(int j = 0; j < currMethods.size(); j++) {
                if(parentMethods.get(i).endsWith("." + currMethods.get(j))) {
                    finalMethods.set(i, currClass + "." + currMethods.get(j));
                    tempMethods.remove(currMethods.get(j));
                }
            }
        }

        //attach current class methods to final methods
        for(int i = 0; i<tempMethods.size(); i++) {
            finalMethods.add(currClass + "." + tempMethods.get(i));
        }
        finalMethods.forEach(e -> mipsSupport.genWord(e));
        currMethods = new ArrayList<>();
        parent.getChildrenList().forEachRemaining(x -> generateDispatchTables(x, finalMethods));
    }

    /**
     * This method visits the class node and generates the
     * appropriate information for the dispatch table
     * @param node the class node
     * @return
     */
    @Override
    public Object visit(Class_ node) {
        this.currClass = node.getName();

        //Create an entry to hold the methods for this class only if it isn't a built in
        if (!BUILT_IN_CLASSES.contains(node.getName())) {
            this.classMethods.put(node.getName(), new HashSet<>());
        }

        mipsSupport.genLabel(this.currClass + "_dispatch_table");
        return super.visit(node);
    }

    /**
     * This method stop visitation at the field node
     * @param node the field node
     * @return
     */
    @Override
    public Object visit(Field node) {
        return null;
    }

    /**
     * This method updates the information needed to include information about
     * this class' method for the dispatch table
     * @param node the method node
     * @return
     */
    @Override
    public Object visit(Method node) {
        currMethods.add(node.getName());

        if (!BUILT_IN_CLASSES.contains(this.currClass)) {
            this.classMethods.get(this.currClass).add(node.getName());
        }
        return null;
    }
}
