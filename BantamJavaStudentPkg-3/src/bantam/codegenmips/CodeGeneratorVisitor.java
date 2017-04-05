/**
 * File: CodeGeneratorVisitor.java
 * @author Edward (osan) Zhou
 * @author Alex Rinker
 * @author Vivek Sah
 * Class: CS461
 * Project: 4B
 * Date: April 5 2017
 */

package bantam.codegenmips;

import bantam.ast.*;
import bantam.util.ClassTreeNode;
import bantam.visitor.Visitor;

/**
 * Visitor for creating the .text code of mips
 */
public class CodeGeneratorVisitor extends Visitor{
    /** support class for generating code in mips */
    private MipsSupport mipsSupport;

    /** root of the class tree */
    private ClassTreeNode root;

    CodeGeneratorVisitor(ClassTreeNode root, MipsSupport mipsSupport) {
        this.mipsSupport = mipsSupport;
        this.root = root;
    }

    @Override
    public Object visit(Class_ node) {
        return super.visit(node);
    }

    @Override
    public Object visit(Field node) {
        return super.visit(node);
    }

    @Override
    public Object visit(Method node) {
        return super.visit(node);
    }

    @Override
    public Object visit(FormalList node) {
        return super.visit(node);
    }

    @Override
    public Object visit(Formal node) {
        return super.visit(node);
    }

    @Override
    public Object visit(StmtList node) {
        return super.visit(node);
    }

    @Override
    public Object visit(DeclStmt node) {
        return super.visit(node);
    }

    @Override
    public Object visit(ExprStmt node) {
        return super.visit(node);
    }

    @Override
    public Object visit(IfStmt node) {
        return super.visit(node);
    }

    @Override
    public Object visit(WhileStmt node) {
        return super.visit(node);
    }

    @Override
    public Object visit(ForStmt node) {
        return super.visit(node);
    }

    @Override
    public Object visit(BreakStmt node) {
        return super.visit(node);
    }

    @Override
    public Object visit(BlockStmt node) {
        return super.visit(node);
    }

    @Override
    public Object visit(ReturnStmt node) {
        return super.visit(node);
    }

    @Override
    public Object visit(ExprList node) {
        return super.visit(node);
    }

    @Override
    public Object visit(DispatchExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(NewExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(NewArrayExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(InstanceofExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(CastExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(AssignExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(ArrayAssignExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(BinaryCompEqExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(BinaryCompNeExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(BinaryCompLtExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(BinaryCompLeqExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(BinaryCompGtExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(BinaryCompGeqExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(BinaryArithPlusExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(BinaryArithMinusExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(BinaryArithTimesExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(BinaryArithDivideExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(BinaryArithModulusExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(BinaryLogicAndExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(BinaryLogicOrExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(UnaryNegExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(UnaryNotExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(UnaryIncrExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(UnaryDecrExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(VarExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(ArrayExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(ConstIntExpr node) {
        mipsSupport.genLoadImm(mipsSupport.getResultReg(), node.getIntConstant());
        return null;
    }

    @Override
    public Object visit(ConstBooleanExpr node) {
        if(node.getConstant().equals("true")) {
            mipsSupport.genLoadImm(mipsSupport.getResultReg(), 1);
        } else {
            mipsSupport.genLoadImm(mipsSupport.getResultReg(), 0);
        }
        return super.visit(node);
    }

    @Override
    public Object visit(ConstStringExpr node) {
        return super.visit(node);
    }
}
