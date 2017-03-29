/**
 * File: MainMainVisitor.java
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

package bantam.visitor;
import bantam.ast.*;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;
import java.util.Hashtable;

/**
 * Determine if there is a main class and main method in set of files
 */
public class MainMainVisitor extends Visitor {
    // if we found a main class
    private boolean hasClass;
    // if we found a main method
    private boolean hasMethod;
    // class inheritance tree
    private Hashtable<String, ClassTreeNode> classMap;
    // searching inheritance tree
    private boolean searchingSuper;

    /**
     * checks if there exists a main method in a main class
     * else register an error
     * @param ast the ASTNode forming the root of the tree
     */
    public void hasMain(Program ast,
                        Hashtable<String, ClassTreeNode> classMap,
                        ErrorHandler errorHandler) {
        this.hasClass = false;
        this.hasMethod = false;
        this.searchingSuper = false;
        this.classMap = classMap;
        ast.accept(this);
        if (!(this.hasClass && this.hasMethod)) {
            errorHandler.register(errorHandler.SEMANT_ERROR,
                    "Missing Main method in a Main Class");
        }
    }

    /**
     * Simple version of hasMain for easier use in main.java
     */
    public boolean hasMain(Program ast) {
        hasMain(ast, new Hashtable<String, ClassTreeNode>(), new ErrorHandler());
        if (!(this.hasClass && this.hasMethod)) { return false; }
        return true;
    }

    /**
     * Checks for the class' name and if it is "Main" then continue the
     * search for a main method
     * @param classNode
     * @return
     */
    @Override
    public Object visit(Class_ classNode) {
        //if already found main class and searching inheritance for method
        if(this.searchingSuper) {
            super.visit(classNode);
        }
        if(classNode.getName().equals("Main")) {
            this.hasClass = true;
            super.visit(classNode);
            if(!this.hasMethod) {
                searchingSuper = true;
                ClassTreeNode currentClass = classMap.get("Main");
                while(currentClass.getParent() != null) {
                    currentClass = currentClass.getParent();
                    currentClass.getASTNode().accept(this);
                }
                searchingSuper = false;
            }
        }
        return null;
    }


    /**
     * For each method checks if it is the main method
     * @param methodNode
     * @return
     */
    @Override
    public Object visit(Method methodNode) {
        if(methodNode.getName().equals("main")) {
            this.hasMethod = true;
        }
        return null;
    }

    /**
     * If you hit a field, you're past all that we care about so
     * just stop this traversal.
     * @param fieldNode
     * @return
     */
    @Override
    public Object visit(Field fieldNode){
        return null;
    }
}
