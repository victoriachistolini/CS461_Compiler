package bantam.visitor;

import bantam.ast.*;
import bantam.util.ErrorHandler;
import bantam.util.SemanticTools;
import bantam.util.SymbolTable;

public class VarSymbolTableVisitor extends Visitor {
    private ErrorHandler errHandler;
    private Class_ currClass;
    private SymbolTable varSymbolTable;

    /**
     * returns whether a Main class exists and has a main method
     * within it
     * @param ast the ASTNode forming the root of the tree
     * @return whether a Main class exists with a main method
     */
    public void populateSymbolTable(Class_ ast,
                                    SymbolTable table,
                                    ErrorHandler errHandler) {
        this.varSymbolTable = table;
        this.varSymbolTable.enterScope();
        this.currClass = ast;
        this.errHandler = errHandler;
        ast.accept(this);
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
        if (this.varSymbolTable.lookup(fieldNode.getName()) != null){
            errHandler.register(
                    errHandler.SEMANT_ERROR,
                    currClass.getFilename(),
                    fieldNode.getLineNum(),
                    "Two methods declared with the same name '" +
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
                    "Variable Name is a Reserved Keyword: " + declStmt.getName());
        }
        if (this.varSymbolTable.lookup(declStmt.getName()) != null){
            errHandler.register(
                    errHandler.SEMANT_ERROR,
                    currClass.getFilename(),
                    declStmt.getLineNum(),
                    "Variable with the same name already declared '" +
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
        if (SemanticTools.isReservedWord(formal.getName())) {
            this.errHandler.register(
                    this.errHandler.SEMANT_ERROR,
                    currClass.getFilename(),
                    formal.getLineNum(),
                    "Variable Name is a Reserved Keyword: " + formal.getName());
        }

        if (this.varSymbolTable.peek(formal.getName()) != null){
            errHandler.register(
                    errHandler.SEMANT_ERROR,
                    currClass.getFilename(),
                    formal.getLineNum(),
                    "Two parameters declared with the same name '" +
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


