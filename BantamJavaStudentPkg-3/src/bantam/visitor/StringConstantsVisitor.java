/**
 * File: StringConstantsVisitor.java
 * @author Edward (osan) Zhou
 * @author Alex Rinker
 * @author Vivek Sah
 * Class: CS461
 * Project: 4
 * Date: March 30, 2017
 */

package bantam.visitor;

import bantam.ast.Class_;
import bantam.ast.ConstStringExpr;

import bantam.codegenmips.MipsSupport;
import bantam.util.ClassTreeNode;

import java.util.*;

/**
 * Creates a Map whose keys are the String constants from the program and
 * values are names for the String constants.
 *
 * Value format is: "StringConst_X"; where X is a unique number.
 */

public class StringConstantsVisitor extends Visitor {

    private ClassTreeNode root;
    private MipsSupport mipsSupport;
    private Set<String> filenames;

    private Map<String,String> stringConstantContainer;
    private int nameNum;


    public StringConstantsVisitor(){
        stringConstantContainer = new HashMap<>();
        nameNum = -1;
        filenames = new HashSet<>();
    }
    /**
     * Constructor
     * @param root Object tree node
     */
    public StringConstantsVisitor(ClassTreeNode root, MipsSupport mipsSupport) {
        stringConstantContainer = new HashMap<>();
        nameNum = -1;
        this.root = root;
        this.mipsSupport = mipsSupport;
        filenames = new HashSet<>();
    }


    /**
     * gets string constant and value mappings
     * @return
     */
    public Map<String,String> getStringConstants(){
        populateStringConstantContainer(root);
        filenames.forEach(name ->
                mipsSupport.genStringConstTemplate(name, mipsSupport.getLabel()));
        return stringConstantContainer;
    }

    private void populateStringConstantContainer(ClassTreeNode parent) {
        parent.getASTNode().accept(this);
        parent.getChildrenList().forEachRemaining(child -> {
            populateStringConstantContainer(child);
        });
    }

    /**
     * Checks for unique files and creates labels for them
     * @return null
     */
    @Override
    public Object visit(Class_ classNode) {
        filenames.add(classNode.getFilename());
        return super.visit(classNode);
    }

    /**
     * add new entry when visiting a string constant node
     * @param node the string constant expression node
     * @return visit instance
     */
    @Override
    public Object visit(ConstStringExpr node) {
        this.nameNum++;
        stringConstantContainer.put(node.getConstant(), "StringConst_" +
                                    Integer.toString(this.nameNum));
        return super.visit(node);
    }


    public String toString(){
        return stringConstantContainer.toString();
    }

    /**
     * does nothing
     * @param args unused
     */
   public static void main(String[] args) {
       return;
   }
}
