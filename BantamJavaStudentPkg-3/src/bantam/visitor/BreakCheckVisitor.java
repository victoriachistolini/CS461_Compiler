package bantam.visitor;

import bantam.ast.*;
import bantam.util.ErrorHandler;
import javafx.beans.property.ObjectProperty;

/**
 * Created by Alex on 3/9/17.
 * This visitor checks to make sure break statements are in loops
 */
public class BreakCheckVisitor extends Visitor {
    private int loopDepth;
    private ErrorHandler errHandler;
    private Class_ currClass;

    public void checkBreakStmts(Program ast, ErrorHandler e){
        this.loopDepth = 0;
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
        this.loopDepth = 0;
        return super.visit(classNode);
    }

    /**
     * Visits the while statement and updates the loop
     * depth accordingly
     * @param whileNode
     * @return null
     */
    @Override
    public Object visit(WhileStmt whileNode) {
        this.loopDepth++;
        super.visit(whileNode);
        this.loopDepth--;
        return null;
    }

    /**
     * Visits the if statement and updates the loop
     * depth accordingly
     * @param ifNode
     * @return null
     */
    @Override
    public Object visit(IfStmt ifNode) {
        this.loopDepth++;
        super.visit(ifNode);
        this.loopDepth--;
        return null;
    }

    /**
     * Visits the for statement and updates the loop
     * depth accordingly
     * @param forNode
     * @return null
     */
    @Override
    public Object visit(ForStmt forNode) {
        this.loopDepth++;
        super.visit(forNode);
        this.loopDepth--;
        return null;
    }

    /**
     * Visits the if statement and updates the loop
     * depth accordingly
     * @param breakNode
     * @return null
     */
    @Override
    public Object visit(BreakStmt breakNode) {
        if (this.loopDepth == 0) {
            errHandler.register(
                    errHandler.SEMANT_ERROR,
                    currClass.getFilename(),
                    breakNode.getLineNum(),
                    "Break statement called outside of loop"
            );
        }
        return null;
    }


    //Nodes to terminate on
    @Override
    public Object visit(Field fieldNode) {return null;}

    @Override
    public Object visit(Formal formalNode) {return null;}

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
    public Object visit(DeclStmt declNode) { return null; }

}
