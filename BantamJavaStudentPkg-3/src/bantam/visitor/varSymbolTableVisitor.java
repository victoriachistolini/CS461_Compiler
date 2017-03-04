package bantam.visitor;

import bantam.ast.*;
import bantam.util.ErrorHandler;
import bantam.util.SymbolTable;

import java.util.HashMap;
import java.util.Map;


public class varSymbolTableVisitor extends  Visitor {
    private ErrorHandler errHandler;
    private Class_ currClass;
    private SymbolTable varSymbolTable;

    /**
     * returns whether a Main class exists and has a main method
     * within it
     * @param ast the ASTNode forming the root of the tree
     * @return whether a Main class exists with a main method
     */
    public void populateSymbolTable(Class_ ast, SymbolTable table) {
        this.varSymbolTable = table;
        this.varSymbolTable.enterScope();
        this.currClass = ast;
        ast.accept(this);
    }
    @Override

    /***
     * visits a Field node and adds it to the symbol table
     * @param fieldNode field node
     */

    public Object visit(Field fieldNode) {
        if (this.varSymbolTable.lookup(fieldNode.getName()) != null){
            errHandler.register(
                    errHandler.SEMANT_ERROR,
                    currClass.getFilename(),
                    fieldNode.getLineNum(),
                    "Two methods declared with the same name '" +
                            fieldNode.getName() + "'"
            );


        }
        this.varSymbolTable.add(fieldNode.getName(), fieldNode.getType());
        return  null;

    }

    /***
     * visits a Method node and adds all the variable & the type to the symbol table
     * @param methodNode method node
     */
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
    public Object visit(DeclStmt declStmt) {
        if (this.varSymbolTable.lookup(declStmt.getName()) == null){
            this.varSymbolTable.add(declStmt.getName(), declStmt.getType());
        }
        return  null;
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


