/**
 * File: NumLocalVarsVisitor.java
 * @author Victoria Chistolini
 * @author Edward (osan) Zhou
 * @author Alex Rinker
 * @author Vivek Sah
 * Class: CS461
 * Project: 2.5
 * Date: Feb 25, 2017
 */
package bantam.visitor;

import bantam.ast.*;
import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import com.sun.org.apache.bcel.internal.generic.NEW;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 2/25/17.
 */
public class NumLocalVarsVisitor extends Visitor {
    private Map localVars;
    private String currClass;
    private String currMethod;

    /**
     * returns whether a Main class exists and has a main method
     * within it
     * @param ast the ASTNode forming the root of the tree
     * @return whether a Main class exists with a main method
     */
    public Map<String, Integer> getNumLocalVars(Program ast) {
        localVars = new HashMap<String, Integer>();
        ast.accept(this);
        return localVars;
    }

    /**
     * Handles traversing the class node
     * @param classNode the class AST node
     * @return nothing
     */
    @Override
    public Object visit(Class_ classNode) {
        currClass = classNode.getName();
        super.visit(classNode);
        return null;
    }

    /**
     * Handles traversing a method node
     * @param methodNode the method AST node
     * @return nothing
     */
    @Override
    public Object visit(Method methodNode) {
        currMethod = methodNode.getName();
        super.visit(methodNode);
        return null;
    }

    /**
     * Handles traversing the Formal node
     * @param formalNode the formal AST Node
     * @return nothing
     */
    @Override
    public Object visit(Formal formalNode) {
        addVar();
        return null;
    }

    /**
     * Handles traversing the DeclStmt node
     * @param declNode the AST node
     * @return nothing
     */
    @Override
    public Object visit(DeclStmt declNode) {
        addVar();
        return null;
    }

    //The following handle Nodes to terminate traversal on
    @Override
    public Object visit(Field fieldNode) {
        return null;
    }

    @Override
    public Object visit(AssignExpr assNode) { return null; }

    @Override
    public Object visit(DispatchExpr disNode) { return null; }

    @Override
    public Object visit(NewExpr newNode) { return null; }

    @Override
    public Object visit(InstanceofExpr instNode) { return null; }

    @Override
    public Object visit(CastExpr castNode) { return null; }

    @Override
    public Object visit(BinaryExpr binNode) { return null; }

    @Override
    public Object visit(UnaryExpr unNode) { return null; }

    @Override
    public Object visit(ConstExpr conNode) { return null; }

    @Override
    public Object visit(VarExpr varNode) { return null; }

    @Override
    public Object visit(ReturnStmt returnNode) { return null; }

    //End Handling traversal termination

    /**
     * This helper method adds to variable count based on the
     * current class and method
     */
    private void addVar() {
        String key = this.currClass + "." + this.currMethod;
        if(localVars.containsKey(key)) {
            localVars.put(key, (int) localVars.get(key) +1);
        }
        else {
            localVars.put(currClass + "." + currMethod, 1);
        }
    }
}

