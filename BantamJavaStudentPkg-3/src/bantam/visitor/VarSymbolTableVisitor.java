/**
 * File: VarSymbolTableVisitor.java
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
import bantam.util.SemanticTools;
import bantam.util.SymbolTable;

import java.util.Hashtable;

public class VarSymbolTableVisitor extends Visitor {
    private ErrorHandler errHandler;
    private Class_ currClass;
    private SymbolTable varSymbolTable;
    private Hashtable<String,ClassTreeNode> classMap;
    private int fieldLevel;

    /**
     * Populates variable symbol tables of classes
     * @param ast the root node
     * @param classMap the class map
     * @param errHandler the error handler to report any errors
     */
    public void populateSymbolTable(Program ast,
                                    Hashtable<String, ClassTreeNode> classMap,
                                    ErrorHandler errHandler) {
        this.errHandler = errHandler;
        this.classMap = classMap;
        ast.accept(this);
    }

    /**
     * updates the current class and symbol table pointers and then
     * Visits the Class node
     * @param classNode
     * @return
     */
    @Override
    public Object visit(Class_ classNode) {
        this.currClass = classNode;
//        System.out.println("class level");
        this.varSymbolTable = (
                this.classMap.get(this.currClass.getName()).getVarSymbolTable()
        );
//        System.out.println("class "+classNode.getName()+""+this.varSymbolTable.getCurrScopeLevel());
        this.fieldLevel = this.varSymbolTable.getCurrScopeLevel();
        super.visit(classNode);
        return null;
    }

    /***
     * visits a Field node and adds it to the symbol table
     * @param fieldNode field node
     */
    @Override
    public Object visit(Field fieldNode) {

        if (SemanticTools.isReservedWord(fieldNode.getName())) {
            this.errHandler.register(
                    this.errHandler.SEMANT_ERROR,
                    currClass.getFilename(),
                    fieldNode.getLineNum(),
                    "Field Name is a Reserved Keyword: " + fieldNode.getName());
        }
        if (this.varSymbolTable.peek(fieldNode.getName()) != null){
            errHandler.register(
                    errHandler.SEMANT_ERROR,
                    currClass.getFilename(),
                    fieldNode.getLineNum(),
                    "Field with the same name already declared: '" +
                            fieldNode.getName() + "'"
            );
        } else {
            this.varSymbolTable.add(fieldNode.getName(), fieldNode.getType());
        }
        return  null;
    }

    /***
     * visits a Method node and adds all the variable & the type to the symbol table
     * @param methodNode method node
     */
    @Override
    public Object visit(Method methodNode) {
        this.varSymbolTable.enterScope();
        super.visit(methodNode);
        this.varSymbolTable.exitScope();
        return null;
    }

    /**
     * visits a declaration statement and pushes variable & type to the symbol table
     * @param declStmt declarartion statement
     * @return
     */
    @Override
    public Object visit(DeclStmt declStmt) {
        if (SemanticTools.isReservedWord(declStmt.getName())) {
            this.errHandler.register(
                    this.errHandler.SEMANT_ERROR,
                    currClass.getFilename(),
                    declStmt.getLineNum(),
                    "Variable name is a reserved keyword: " + declStmt.getName());
        }

        if (this.varSymbolTable.getScopeLevel(declStmt.getName()) > this.fieldLevel){
//            System.out.println(declStmt.getName());
//            System.out.println("level"+this.varSymbolTable.getScopeLevel(declStmt.getName()));
//            System.out.println("level now"+this.varSymbolTable.getCurrScopeLevel());
            errHandler.register(
                    errHandler.SEMANT_ERROR,
                    currClass.getFilename(),
                    declStmt.getLineNum(),
                    "Variable with the same name already declared: '" +
                            declStmt.getName() + "'"
            );
        } else {
            this.varSymbolTable.add(declStmt.getName(), declStmt.getType());
        }
        return null;
    }


    /**
     * visits a formal parameters and adds it to the symbol table
     * @param formal
     * @return
     */
    @Override
    public Object visit(Formal formal){
        //Reserved Keyword Check
        if (SemanticTools.isReservedWord(formal.getName())) {
            this.errHandler.register(
                    this.errHandler.SEMANT_ERROR,
                    currClass.getFilename(),
                    formal.getLineNum(),
                    "Variable Name is a Reserved Keyword: " + formal.getName());
        }

        //Already declared variable
        if (this.varSymbolTable.peek(formal.getName()) != null){
            errHandler.register(
                    errHandler.SEMANT_ERROR,
                    currClass.getFilename(),
                    formal.getLineNum(),
                    "Parameter with the same name already declared: '" +
                            formal.getName() + "'"
            );
        } else {
            this.varSymbolTable.add(formal.getName(),formal.getType());
        }

        return null;
    }

    /**
     * Visits a while statement and adds to the symbol table
     * @param whileStmt
     * @return
     */
    @Override
    public Object visit(WhileStmt whileStmt){
        this.varSymbolTable.enterScope();
        super.visit(whileStmt);
        this.varSymbolTable.exitScope();
        return  null;
    }

    /**
     * visits a if statement and adds the variables to the symbol table
     * @param ifStmt
     * @return
     */
    @Override
    public Object visit(IfStmt ifStmt){
        this.varSymbolTable.enterScope();
        super.visit(ifStmt);
        this.varSymbolTable.exitScope();
        return null;
    }

    /**
     * visits a for loop and adds the variables to a symbol table
     * @param forStmt
     * @return
     */
    @Override
    public Object visit(ForStmt forStmt){
        this.varSymbolTable.enterScope();
        super.visit(forStmt);
        this.varSymbolTable.exitScope();
        return null;
    }

    /**
     * visits a block statement
     * @param blockStmt
     * @return
     */
    public Object visit(BlockStmt blockStmt){
        this.varSymbolTable.enterScope();
        super.visit(blockStmt);
        this.varSymbolTable.exitScope();
        return null;

    }


    //The following handle Nodes to terminate traversal on
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


