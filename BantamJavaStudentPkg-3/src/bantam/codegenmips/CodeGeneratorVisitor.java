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

import java.util.Map;
import java.util.Set;

/**
 * Visitor for creating the .text code of mips
 */
public class CodeGeneratorVisitor extends Visitor{
    /** support class for generating code in mips */
    private MipsSupport mipsSupport;

    /** root of the class tree */
    private ClassTreeNode root;

    /** the current class being traversed */
    private Class_ currClass;

    /** Map containing the labels associated with each class */
    private Map<String, String> classNames;

    /** Map which connects String constants to their label */
    private Map<String, String> stringLabels;

    /**
     * The constructor method.
     * @param root
     * @param mipsSupport
     */
    CodeGeneratorVisitor(
            ClassTreeNode root,
            MipsSupport mipsSupport,
            Map<String, String> classNames,
            Map<String, String> stringLabels) {
        this.mipsSupport = mipsSupport;
        this.root = root;
        this.classNames = classNames;
        this.stringLabels = stringLabels;
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
        super.visit(node);
        mipsSupport.genComment("Bitwise not: " + mipsSupport.getResultReg());
        mipsSupport.genNot(mipsSupport.getResultReg(), mipsSupport.getResultReg());
        return null;
    }

    @Override
    public Object visit(UnaryIncrExpr node) {
        super.visit(node);
        mipsSupport.genComment("Increment by 1: " + mipsSupport.getResultReg());
        mipsSupport.genAdd(mipsSupport.getResultReg(), mipsSupport.getResultReg(), 1);
        return null;
    }

    @Override
    public Object visit(UnaryDecrExpr node) {
        super.visit(node);
        mipsSupport.genComment("Decrement by 1: " + mipsSupport.getResultReg());
        mipsSupport.genAdd(mipsSupport.getResultReg(), mipsSupport.getResultReg(), -1);
        return null;
    }

    @Override
    public Object visit(VarExpr node) {
        return super.visit(node);
        /// TODO: 4/5/2017 ANYTHING
    }

    @Override
    public Object visit(ConstIntExpr node) {
        mipsSupport.genComment("Load int to: " + mipsSupport.getResultReg());
        mipsSupport.genLoadImm(mipsSupport.getResultReg(), node.getIntConstant());
        return null;
    }

    @Override
    public Object visit(ConstBooleanExpr node) {
        mipsSupport.genComment("Load " + node.getConstant() + " to: "
                                + mipsSupport.getResultReg());
        if(node.getConstant().equals("true")) {
            mipsSupport.genLoadImm(mipsSupport.getResultReg(), 1);
        } else {
            mipsSupport.genLoadImm(mipsSupport.getResultReg(), 0);
        }
        return null;
    }

    @Override
    public Object visit(ConstStringExpr node) {
        mipsSupport.genComment("Load address of " + node.getConstant() + " to: "
                                + mipsSupport.getResultReg());
        mipsSupport.genLoadAddr(mipsSupport.getResultReg(),
                                this.stringLabels.get(node.getConstant()));
        return null;
    }
}
