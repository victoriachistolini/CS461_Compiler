/**
 * File: NumLocalVarsVisitor.java
 * This file was written in loving memory of our former
 * group member Victoria Chistolini who sadley did not
 * survive project 2.5. RIP.
 * @author Edward (osan) Zhou
 * @author Alex Rinker
 * @author Vivek Sah
 * Class: CS461
 * Project: 3
 * Date: March 9 2017
 */
package bantam.codegenmips;

import bantam.ast.*;
import bantam.visitor.Visitor;
import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import com.sun.org.apache.bcel.internal.generic.NEW;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 2/25/17.
 */
public class NumLocalVarsVisitor extends Visitor {
    private int numLocalVars;

    public int getNumLocalVars(Method node) {
        this.numLocalVars = 0;
        super.visit(node);
        return this.numLocalVars;
    }

    /**
     * Handles traversing the Formal node
     * @param formalNode the formal AST Node
     * @return nothing
     */
    @Override
    public Object visit(Formal formalNode) {
        this.numLocalVars++;
        return null;
    }

    /**
     * Handles traversing the DeclStmt node
     * @param declNode the AST node
     * @return nothing
     */
    @Override
    public Object visit(DeclStmt declNode) {
        this.numLocalVars++;
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
}

