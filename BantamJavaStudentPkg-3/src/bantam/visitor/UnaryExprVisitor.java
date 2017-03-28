/**
 * File: UnaryExprVisitor.java
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
import bantam.util.ErrorHandler;

/**
 * Makes sure that unary expressions have a varexpr as its modifying expression
 */
public class UnaryExprVisitor extends Visitor {
    private ErrorHandler errHandler;
    private Class_ currClass;

    public void checkUnaryExpr(Program ast, ErrorHandler e){
        this.errHandler = e;
        ast.accept(this);
    }

    /**
     * visits the class node and updates the current class value
     * @param classNode
     * @return
     */
    @Override
    public Object visit(Class_ classNode) {
        this.currClass = classNode;
        return super.visit(classNode);
    }

    /**
     * visits unary decr expression and checks if the exp is of type varexp
     * @param unaryDecrExpr
     * @return
     */
    @Override
    public Object visit(UnaryDecrExpr unaryDecrExpr) {
        if (!(unaryDecrExpr.getExpr() instanceof VarExpr)) {
            errHandler.register(
                errHandler.SEMANT_ERROR,
                currClass.getFilename(),
                unaryDecrExpr.getLineNum(),
                "Invalid decrement of expression"
           );
        }
        return null;
    }

    /**
     * visits the unary Incr expression and checks if exp is of type varExp
     * @param unaryIncrExpr
     * @return
     */
    @Override
    public Object visit(UnaryIncrExpr unaryIncrExpr) {
        if (!(unaryIncrExpr.getExpr() instanceof VarExpr)) {
            errHandler.register(
                    errHandler.SEMANT_ERROR,
                    currClass.getFilename(),
                    unaryIncrExpr.getLineNum(),
                    "Invalid increment of expression"
            );
        }
        return null;
    }

    //The following handle Nodes to terminate traversal on
    @Override
    public Object visit(Formal formal){ return null; }

    @Override
    public Object visit(Field field){ return null; }

    @Override
    public Object visit(VarExpr varNode) { return null; }

    //End terminate traversal
}

