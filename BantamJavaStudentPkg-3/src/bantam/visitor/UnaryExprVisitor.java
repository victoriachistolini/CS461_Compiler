package bantam.visitor;

import bantam.ast.*;
import bantam.util.ErrorHandler;

/**
 * Created by vivek on 3/6/17.
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
                "UnaryDecrExpr must have VarExpr as expression"
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
                    "UnaryIncrExpr must have VarExpr as expression"
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
}

