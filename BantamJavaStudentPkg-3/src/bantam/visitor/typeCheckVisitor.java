package bantam.visitor;

import bantam.ast.*;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;
import bantam.util.SymbolTable;
import bantam.util.SemanticTools;

import java.util.Hashtable;

/**
 * Checks type semantics of the program
 */
public class typeCheckVisitor extends Visitor {
    private final String BOOLEAN = "boolean";
    private final String STRING = "String";
    private final String INT = "int";
    private final String VOID = "void";
    private final String THIS = "this";

    /** Error Handler to register semantic errors in class declarations */
    private ErrorHandler errorHandler;
    /** The class map hashtable */
    private Hashtable<String, ClassTreeNode> classMap;


    private SymbolTable currentVarSymbolTable;
    private SymbolTable currentMethodSymbolTable;
    private Method currentMethod;
    private String currentClass;
    /**
     * check type semantics of the program
     * @param program root AST node of the program
     * @param classMap global class map
     * @param errorHandler errorhandler to report errors
     */
    public void analyzeTypes(Program program,
                           Hashtable<String, ClassTreeNode> classMap,
                           ErrorHandler errorHandler) {
        this.classMap = classMap;
        this.errorHandler = errorHandler;
        program.accept(this);
    }

    @Override
    public Object visit(ASTNode node) {
        return super.visit(node);
    }

    @Override
    public Object visit(ListNode node) {
        return super.visit(node);
    }

    @Override
    public Object visit(Program node) {
        return super.visit(node);
    }

    @Override
    public Object visit(ClassList node) {
        return super.visit(node);
    }

    @Override
    public Object visit(Class_ node) {
        this.currentVarSymbolTable = classMap.get(node.getName()).getVarSymbolTable();
        this.currentMethodSymbolTable = classMap.get(node.getName()).getMethodSymbolTable();
        this.currentClass = node.getName();
        return super.visit(node);
    }

    @Override
    public Object visit(Field node) {
        return super.visit(node);
    }

    @Override
    public Object visit(Method node) {
        this.currentVarSymbolTable.enterScope();
        this.currentMethod = node;
        super.visit(node);
        this.currentVarSymbolTable.exitScope();
        return null;
    }

    @Override
    public Object visit(Formal node) {
        this.currentVarSymbolTable.add(node.getName(), node.getType());
        return super.visit(node);
    }

    @Override
    public Object visit(DeclStmt node) {
        super.visit(node);

        if(node.getInit() != null) {
            checkType(node.getType(), node.getInit().getExprType(), node);
        }
        this.currentVarSymbolTable.add(node.getName(), node.getType());
        return null;
    }


    @Override
    /**
     * Expr stmt should only accept some types of expr
     * assignment
     * incr/decr
     * method call
     * new object constructions
     */
    public Object visit(ExprStmt node) {
        if(!(boolean)super.visit(node)) {
            errorHandler.register(errorHandler.SEMANT_ERROR,
                    this.currentClass,
                    node.getLineNum(),
                    "Invalid expression for expression statement");
        }
        return null;
    }

    @Override
    public Object visit(IfStmt node) {
        node.getPredExpr().accept(this);
        if(!node.getPredExpr().getExprType().equals(BOOLEAN)) {
            errorHandler.register(errorHandler.SEMANT_ERROR,
                    this.currentClass,
                    node.getLineNum(),
                    "Predicate does not evaluate to boolean");
        }
        this.currentVarSymbolTable.enterScope();
        node.getThenStmt().accept(this);
        this.currentVarSymbolTable.exitScope();
        if(node.getElseStmt() != null) {
            this.currentVarSymbolTable.enterScope();
            node.getElseStmt().accept(this);
            this.currentVarSymbolTable.exitScope();
        }
        return null;
    }

    @Override
    public Object visit(WhileStmt node) {
        node.getPredExpr().accept(this);
        if(!node.getPredExpr().getExprType().equals(BOOLEAN)) {
            errorHandler.register(errorHandler.SEMANT_ERROR,
                    this.currentClass,
                    node.getLineNum(),
                    "Predicate does not evaluate to boolean");
        }
        this.currentVarSymbolTable.enterScope();
        node.getBodyStmt().accept(this);
        this.currentVarSymbolTable.exitScope();
        return null;
    }

    @Override
    public Object visit(ForStmt node) {
        this.currentVarSymbolTable.enterScope();
        if(node.getInitExpr() != null) {
            node.getInitExpr().accept(this);
        }
        if(node.getPredExpr() != null) {
            node.getPredExpr().accept(this);
            if(!node.getPredExpr().getExprType().equals(BOOLEAN)) {
                errorHandler.register(errorHandler.SEMANT_ERROR,
                        this.currentClass,
                        node.getLineNum(),
                        "Predicate does not evaluate to boolean");
            }
        }
        if (node.getUpdateExpr() != null) {
            node.getUpdateExpr().accept(this);
        }
        node.getBodyStmt().accept(this);
        this.currentVarSymbolTable.exitScope();
        return null;
    }

    @Override
    public Object visit(BlockStmt node) {
        this.currentVarSymbolTable.enterScope();
        node.getStmtList().accept(this);
        this.currentVarSymbolTable.exitScope();
        return null;
    }

    @Override
    public Object visit(ReturnStmt node) {
        String returnType = this.currentMethod.getReturnType();
        if(returnType.equals(VOID)) {
            if(node.getExpr() != null) {
                errorHandler.register(errorHandler.SEMANT_ERROR,
                        this.currentClass,
                        node.getLineNum(),
                        "return type invalid for void method");
            }
        } else {
            if(node.getExpr() != null) {
                checkType(returnType, node.getExpr().getExprType(), node);
            } else {
                errorHandler.register(errorHandler.SEMANT_ERROR,
                        this.currentClass,
                        node.getLineNum(),
                        "Missing return value");
            }
        }
        return super.visit(node);
    }

    @Override
    public Object visit(DispatchExpr node) {
        // Evaluate type of reference
        String referenceType;
        if (node.getRefExpr() != null) {
            node.getRefExpr().accept(this);
            referenceType = node.getRefExpr().getExprType();
        } else {
            if(((VarExpr) node.getRefExpr()).getName().equals(THIS)) {
                referenceType = this.currentClass;
            } else {
            referenceType = ((VarExpr) node.getRefExpr()).getName();
            }
        }

        node.getActualList().accept(this);

        // Check method compatibility
        if(this.classMap.contains(referenceType)) {
            Method method = (Method) this.classMap.get(referenceType)
                    .getMethodSymbolTable()
                    .lookup(node.getMethodName());
            if(method == null) {
                errorHandler.register(errorHandler.SEMANT_ERROR,
                        this.currentClass,
                        node.getLineNum(),
                        "Undeclared method " + node.getMethodName());
            } else if(method.getFormalList().getSize() != node.getActualList().getSize()) {
                errorHandler.register(errorHandler.SEMANT_ERROR,
                        this.currentClass,
                        node.getLineNum(),
                        "Mismatched number of expected parameters");
            } else {
                for(int i = 0; i < node.getActualList().getSize(); i++) {
                    this.checkType(
                            ((Formal)method.getFormalList().get(i)).getType(),
                            ((Expr)node.getActualList().get(i)).getExprType(),
                            node);
                }
            }
        } else {
            errorHandler.register(errorHandler.SEMANT_ERROR,
                    this.currentClass,
                    node.getLineNum(),
                    "Invalid reference in dispatch");
        }
        return true;
    }

    @Override
    public Object visit(NewExpr node) {
        return true;
    }

    @Override
    public Object visit(NewArrayExpr node) {
        //make sure expr evaluates to int
        if(node.getSize().getExprType() != INT) {
            errorHandler.register(errorHandler.SEMANT_ERROR,
                    this.currentClass,
                    node.getLineNum(),
                    "Expression does not evaluate to int");
        }

        return true;
    }

    @Override
    public Object visit(InstanceofExpr node) {
        super.visit(node);

        if (SemanticTools.isPrimitive(node.getExpr().getExprType()) ||
                SemanticTools.isPrimitive(node.getExpr().getExprType())) {
            errorHandler.register(errorHandler.SEMANT_ERROR,
                    this.currentClass,
                    node.getLineNum(),
                    "Primitives cannot be checked for instance");
        }
        node.setUpCheck(true);
        return false;
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
        if (node.getRef() != null) {
            node.getRef().accept(this);
            //node.setExprType(classMap.get());
        }
        return null;
    }

    @Override
    public Object visit(ArrayExpr node) {
        super.visit(node);
        node.setExprType(node.getIndex().getExprType());
        return null;
    }

    @Override
    public Object visit(ConstIntExpr node) {
        node.setExprType(INT);
        return false;
    }

    @Override
    public Object visit(ConstBooleanExpr node) {
        node.setExprType(BOOLEAN);
        return false;
    }

    @Override
    public Object visit(ConstStringExpr node) {
        node.setExprType(STRING);
        return false;
    }

    /**
     * Checks if a subtype is a subclass of type or equal types
     * else registers error
     * @param type parent type
     * @param subtype possible subtype
     * @param ast the associated ast node for error printing
     */
    private void checkType(String type, String subtype, ASTNode ast) {
        if(type.equals(subtype)) {
            return;
        }

        //primitive check
        if(!(SemanticTools.isPrimitive(subtype) && SemanticTools.isPrimitive(type))) {
            //check class tree
            if (classMap.containsKey(type) && classMap.containsKey(subtype)) {
                ClassTreeNode currClass = classMap.get(subtype);
                while (currClass.getParent() != null) {
                    currClass = currClass.getParent();
                    if (currClass.getName().equals(type)) {
                        return;
                    }
                }
                errorHandler.register(errorHandler.SEMANT_ERROR,
                        this.currentClass,
                        ast.getLineNum(),
                        "Invalid subtype " + subtype + " of type " + type);
            } else {
                if (!classMap.containsKey(type)) {
                    errorHandler.register(errorHandler.SEMANT_ERROR,
                            this.currentClass,
                            ast.getLineNum(),
                            "Invalid type " + type);
                }
                if (!classMap.containsKey(subtype)) {
                    errorHandler.register(errorHandler.SEMANT_ERROR,
                            this.currentClass,
                            ast.getLineNum(),
                            "Invalid type " + subtype);
                }
            }
        } else {
            errorHandler.register(errorHandler.SEMANT_ERROR,
                    this.currentClass,
                    ast.getLineNum(),
                    "Incompatible types " + type + " and " + subtype);
        }
    }



}
