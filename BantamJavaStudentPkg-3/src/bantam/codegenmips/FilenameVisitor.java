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

import bantam.ast.*;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;
import bantam.util.SemanticTools;

import java.util.Hashtable;
import java.util.Map;

/**
 * This visitor determines the name of the file containing
 * the main class
 */
public class FilenameVisitor extends bantam.visitor.Visitor{

    /** The String representing the filename associated the the program */
    private String filename;

    /**
     * returns the filename containing the Main class
     * provided the given root node of the program
     * @param root
     * @return
     */
    public String getMainFilename(ClassTreeNode root) {
        this.filename = "";
        searchChildren(root);
        return this.filename;
    }

    /**
     * Accepts the visitor for the parent node and then
     * recurses through each of the parent's children
     * @param parent
     */
    private void searchChildren(ClassTreeNode parent) {
        parent.getASTNode().accept(this);
        parent.getChildrenList().forEachRemaining( child -> {
            searchChildren(child);
        });
    }

    /**
     * Visits a class node and checks to see if the name is valid
     * Then creates the class's node, and initializes its symbol tables
     * @param node the class node
     * @return
     */
    @Override
    public Object visit(Class_ node) {
        if(node.getName().equals("Main")) {
            this.filename = node.getFilename();
        }
        return null;
    }
}
