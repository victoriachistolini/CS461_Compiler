/**
 * File: SemanticAnalyzerTest.java
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
import bantam.util.SymbolTable;
import bantam.util.SemanticTools;

import java.util.Hashtable;

/**
 * Checks type semantics of the program
 */
public class TypeCheckVisitor extends Visitor {
    /** Keyword constants for assinment and expressions*/
    private final String BOOLEAN = "boolean";
    private final String STRING = "String";
    private final String INT = "int";
    private final String OBJECT = "Object";
    private final String VOID = "void";
    private final String THIS = "this";
    private final String SUPER = "super";
    private final String NULL = "null";

    /** Error Handler to register semantic errors in class declarations */
    private ErrorHandler errorHandler;
    /** The class map hashtable */
    private Hashtable<String, ClassTreeNode> classMap;


    private SymbolTable currentVarSymbolTable;
    private Method currentMethod;
    private Class_ currentClass;

    /**
     * check type semantics of the program and annotates expr types
     * undeclared variables report errors
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
    public Object visit(ClassList node) {

        for (ASTNode aNode : node) {
            if (!(SemanticTools.isBuiltin(((Class_ )aNode).getName()))) {
                aNode.accept(this);
            }
        }
        return null;
    }


    /** update current class fields*/
    @Override
    public Object visit(Class_ node) {
        this.currentVarSymbolTable = classMap.get(node.getName()).getVarSymbolTable();
        this.currentClass = node;
        super.visit(node);
        return null;
    }

    /** Make sure types for fields are compatible*/
    @Override
    public Object visit(Field node) {
        super.visit(node);
        if(this.currentVarSymbolTable.lookup(node.getName()) != null && node.getInit() != null) {
            checkType(node.getType(), node.getInit().getExprType(), node, true);
        }
        return null;
    }

    /**
     * enter a new scope
     */
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

    /**
     * make sure types of declaration and expr are compatible
     * @param node the declaration statement node
     * @return
     */
    @Override
    public Object visit(DeclStmt node) {
        super.visit(node);
        if(node.getInit() != null) {
            checkType(node.getType(), node.getInit().getExprType(), node, true);
        }
        //duplicates should already be checked
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
        if(!(boolean)node.getExpr().accept(this)) {
            errorHandler.register(errorHandler.SEMANT_ERROR,
                    this.currentClass.getFilename(),
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
                    this.currentClass.getFilename(),
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
                    this.currentClass.getFilename(),
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
                        this.currentClass.getFilename(),
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
        super.visit(node);
        if(returnType.equals(VOID)) {
            if(node.getExpr() != null) {
                errorHandler.register(errorHandler.SEMANT_ERROR,
                        this.currentClass.getFilename(),
                        node.getLineNum(),
                        "return type invalid for void method");
            }
        } else {
            if(node.getExpr() != null) {
                checkType(returnType, node.getExpr().getExprType(), node, true);
            } else {
                errorHandler.register(errorHandler.SEMANT_ERROR,
                        this.currentClass.getFilename(),
                        node.getLineNum(),
                        "Missing return value");
            }
        }
        return null;
    }

    /**
     * check reference types and existence!
     * @param node the dispatch expression node
     * @return
     */
    @Override
    public Object visit(DispatchExpr node) {
        // Evaluate type of reference
        String referenceType;
        if (node.getRefExpr() != null) {
            node.getRefExpr().accept(this);
            if(node.getRefExpr().getExprType().equals(THIS)) {
                referenceType = this.currentClass.getName();
            } else {
                referenceType = node.getRefExpr().getExprType();
            }
        } else {
            referenceType = this.currentClass.getName();
        }

        node.getActualList().accept(this);
        node.setExprType(OBJECT); //default error type. overwritten if below passes
        // Check method compatibility

        if(this.classMap.containsKey(referenceType)) {
            Method method = (Method) this.classMap.get(referenceType)
                    .getMethodSymbolTable()
                    .lookup(node.getMethodName());

            if(method == null) {
                errorHandler.register(errorHandler.SEMANT_ERROR,
                        this.currentClass.getFilename(),
                        node.getLineNum(),
                        "Undeclared method " + node.getMethodName());
            } else if(method.getFormalList().getSize() != node.getActualList().getSize()) {
                errorHandler.register(errorHandler.SEMANT_ERROR,
                        this.currentClass.getFilename(),
                        node.getLineNum(),
                        "Mismatched number of expected parameters");
            } else {
                boolean isCorrect = true;
                for(int i = 0; i < node.getActualList().getSize(); i++) {
                    isCorrect &= this.checkType(
                            ((Formal)method.getFormalList().get(i)).getType(),
                            ((Expr)node.getActualList().get(i)).getExprType(),
                            node,
                            true);
                }
                if(isCorrect) {
                    node.setExprType(method.getReturnType());
                }

            }
        } else {
            errorHandler.register(errorHandler.SEMANT_ERROR,
                    this.currentClass.getFilename(),
                    node.getLineNum(),
                    "Invalid reference in dispatch");
        }
        return true;
    }

    /**
     * sets type in parser
     * @param node the new expression node
     * @return
     */
    @Override
    public Object visit(NewExpr node) {
        node.setExprType(node.getType());
        return true;
    }

    @Override
    public Object visit(NewArrayExpr node) {
        //make sure expr evaluates to int
        node.setExprType(node.getType());
        if(node.getSize().getExprType() != INT) {
            errorHandler.register(errorHandler.SEMANT_ERROR,
                    this.currentClass.getFilename(),
                    node.getLineNum(),
                    "Expression does not evaluate to int");
        }

        return true;
    }

    @Override
    public Object visit(InstanceofExpr node) {
        super.visit(node);
        if (SemanticTools.isPrimitive(node.getExpr().getExprType()) ||
                SemanticTools.isPrimitive(node.getType())) {
            errorHandler.register(errorHandler.SEMANT_ERROR,
                    this.currentClass.getFilename(),
                    node.getLineNum(),
                    "Primitives cannot be checked for instance");
        } else {
            if (!classMap.containsKey(node.getExpr().getExprType())) {
                errorHandler.register(errorHandler.SEMANT_ERROR,
                        this.currentClass.getFilename(),
                        node.getLineNum(),
                        "Invalid type " + node.getExpr().getExprType());
            }
            if (!classMap.containsKey(node.getType())) {
                errorHandler.register(errorHandler.SEMANT_ERROR,
                        this.currentClass.getFilename(),
                        node.getLineNum(),
                        "Invalid type " + node.getType());
            }
        }
        node.setUpCheck(true); // this is always true
        node.setExprType(BOOLEAN);
        return false;
    }


    @Override
    public Object visit(CastExpr node) {
        node.getExpr().accept(this);
        if (SemanticTools.isPrimitive(node.getType()) ||
                SemanticTools.isPrimitive(node.getExpr().getExprType())) {
            errorHandler.register(errorHandler.SEMANT_ERROR,
                    this.currentClass.getFilename(),
                    node.getLineNum(),
                    "Primitives cannot be casted");
        } else {
            boolean upcast;
            boolean downcast;
            upcast = checkType(node.getType(), node.getExpr().getExprType(), node, false);
            downcast = checkType(node.getExpr().getExprType(), node.getType(), node, false);
            if (!(upcast || downcast)) {
                errorHandler.register(errorHandler.SEMANT_ERROR,
                        this.currentClass.getFilename(),
                        node.getLineNum(),
                        "Invalid types for casting");
            } else {
                node.setUpCast(upcast);
                node.setExprType(node.getType());
                return false;
            }
        }
        node.setExprType(OBJECT); //only if expr is illegal
        return false;
    }

    @Override
    public Object visit(AssignExpr node) {
        node.getExpr().accept(this);
        node.setExprType(OBJECT);
        String variableType;
        if(node.getRefName()!=null) {
            //check the class map
            if(classMap.containsKey(node.getRefName())) {
                variableType = (String)classMap.get(node.getRefName()).getVarSymbolTable().lookup(node.getName());
                if(variableType != null) {
                    boolean legal = checkType(variableType, node.getExpr().getExprType(), node, true);
                    if(legal) {
                        node.setExprType(variableType);
                    }
                } else {
                    errorHandler.register(errorHandler.SEMANT_ERROR,
                            this.currentClass.getFilename(),
                            node.getLineNum(),
                            "Undeclared field " + node.getName() + " in " + node.getRefName());
                }
            } else {
                if ((!node.getRefName().equals(THIS) && !node.getRefName().equals(SUPER))){
                    errorHandler.register(errorHandler.SEMANT_ERROR,
                            this.currentClass.getFilename(),
                            node.getLineNum(),
                            "Undeclared class " + node.getRefName());
                }
            }

        } else {
            variableType = (String) this.currentVarSymbolTable.lookup(node.getName());
            if(variableType != null) {
                boolean legal = checkType(variableType, node.getExpr().getExprType(), node, true);
                if(legal) {
                    node.setExprType(variableType);
                }
            } else {
                errorHandler.register(errorHandler.SEMANT_ERROR,
                        this.currentClass.getFilename(),
                        node.getLineNum(),
                        "Undeclared variable " + node.getName());
            }
        }
        return true;
    }

    @Override
    public Object visit(ArrayAssignExpr node) {
        node.getExpr().accept(this);
        node.setExprType(OBJECT);
        String variableType;
        if(node.getRefName()!=null) {
            //check the class map
            if(classMap.containsKey(node.getRefName())) {
                variableType = (String)classMap.get(node.getRefName()).getVarSymbolTable().lookup(node.getName());
                if(variableType != null) {
                    boolean legal = checkType(variableType, node.getExpr().getExprType(), node, true);
                    if(legal) {
                        node.setExprType(variableType);
                    }
                } else {
                    errorHandler.register(errorHandler.SEMANT_ERROR,
                            this.currentClass.getFilename(),
                            node.getLineNum(),
                            "Undeclared field " + node.getName() + " in " + node.getRefName());
                }
            } else {
                errorHandler.register(errorHandler.SEMANT_ERROR,
                        this.currentClass.getFilename(),
                        node.getLineNum(),
                        "Undeclared class " + node.getRefName());
            }

        } else {
            variableType = (String) this.currentVarSymbolTable.lookup(node.getName());
            if(variableType != null) {
                boolean legal = checkType(variableType, node.getExpr().getExprType(), node, true);
                if(legal) {
                    node.setExprType(variableType);
                }
            } else {
                errorHandler.register(errorHandler.SEMANT_ERROR,
                        this.currentClass.getFilename(),
                        node.getLineNum(),
                        "Undeclared variable " + node.getName());
            }
        }
        return true;
    }

    @Override
    public Object visit(BinaryCompEqExpr node) {
        super.visit(node);
        checkBinaryExpr(node);
        return false;
    }

    @Override
    public Object visit(BinaryCompNeExpr node) {
        super.visit(node);
        checkBinaryExpr(node);
        return false;
    }

    @Override
    public Object visit(BinaryCompLtExpr node) {
        super.visit(node);
        checkBinaryExpr(node);
        return false;
    }

    @Override
    public Object visit(BinaryCompLeqExpr node) {
        super.visit(node);
        checkBinaryExpr(node);
        return false;
    }

    @Override
    public Object visit(BinaryCompGtExpr node) {
        super.visit(node);
        checkBinaryExpr(node);
        return false;
    }

    @Override
    public Object visit(BinaryCompGeqExpr node) {
        super.visit(node);
        checkBinaryExpr(node);
        return false;
    }

    @Override
    public Object visit(BinaryArithPlusExpr node) {
        super.visit(node);
        checkBinaryExpr(node);
        return false;
    }

    @Override
    public Object visit(BinaryArithMinusExpr node) {
        super.visit(node);
        checkBinaryExpr(node);
        return false;
    }

    @Override
    public Object visit(BinaryArithTimesExpr node) {
        super.visit(node);
        checkBinaryExpr(node);
        return false;
    }

    @Override
    public Object visit(BinaryArithDivideExpr node) {
        super.visit(node);
        checkBinaryExpr(node);
        return false;
    }

    @Override
    public Object visit(BinaryArithModulusExpr node) {
        super.visit(node);
        checkBinaryExpr(node);
        return false;
    }

    @Override
    public Object visit(BinaryLogicAndExpr node) {
        super.visit(node);
        checkBinaryExpr(node);
        return false;
    }

    @Override
    public Object visit(BinaryLogicOrExpr node) {
        super.visit(node);
        checkBinaryExpr(node);
        return false;
    }

    @Override
    public Object visit(UnaryNegExpr node) {
        super.visit(node);
        checkUnaryExpr(node);
        return false;
    }

    @Override
    public Object visit(UnaryNotExpr node) {
        super.visit(node);
        checkUnaryExpr(node);
        return false;
    }

    @Override
    public Object visit(UnaryIncrExpr node) {
        super.visit(node);
        checkUnaryExpr(node);
        return true;
    }

    @Override
    public Object visit(UnaryDecrExpr node) {
        super.visit(node);
        checkUnaryExpr(node);
        return true;
    }

    /**
     * check legality of ref and expr type
     * @param node the variable expression node
     * @return
     */
    @Override
    public Object visit(VarExpr node) {
        String varType = null;
        if (node.getName().equals(NULL)) {
            node.setExprType(NULL);
            return false;
        }
        if (node.getRef() != null) {
            node.getRef().accept(this);
            if(((VarExpr) node.getRef()).getName().equals(THIS)) {
                varType =(String) this.currentVarSymbolTable.lookup(node.getName());
            } else if(((VarExpr) node.getRef()).getName().equals(SUPER)) {
                ClassTreeNode currClass = classMap.get(currentClass.getName());
                while (currClass.getParent() != null) {
                    currClass = currClass.getParent();
                    if (currClass.getVarSymbolTable().lookup(node.getName())!=null) {
                        varType = (String) currClass.getVarSymbolTable().lookup(node.getName());
                    }
                }
            } else {
                errorHandler.register(errorHandler.SEMANT_ERROR,
                        this.currentClass.getFilename(),
                        node.getLineNum(),
                        "Only super or this are allowed for variable references");
            }
        } else {
            varType = (String) currentVarSymbolTable.lookup(node.getName());
        }
        if (varType != null) {
            node.setExprType(varType);
        } else {
            if (node.getName().equals(THIS)) {
                 node.setExprType(currentClass.getName());
            } else if (node.getName().equals(SUPER)) {
                node.setExprType(currentClass.getParent());
            } else{
                errorHandler.register(errorHandler.SEMANT_ERROR,
                        this.currentClass.getFilename(),
                        node.getLineNum(),
                        "Undeclared variable " + node.getName());
            }
        }
        return false;
    }

    @Override
    public Object visit(ArrayExpr node) {
        super.visit(node);
        String varType = null;
        if (node.getRef() != null) {
            node.getRef().accept(this);
            if(((VarExpr) node.getRef()).getName().equals(THIS)) {
                varType =(String) this.currentVarSymbolTable.lookup(node.getName());
            } else if(((VarExpr) node.getRef()).getName().equals(SUPER)) {
                ClassTreeNode currClass = classMap.get(currentClass.getName());
                while (currClass.getParent() != null) {
                    currClass = currClass.getParent();
                    if (currClass.getVarSymbolTable().lookup(node.getName())!=null) {
                        varType = (String) currClass.getVarSymbolTable().lookup(node.getName());
                    }
                }
            } else {
                errorHandler.register(errorHandler.SEMANT_ERROR,
                        this.currentClass.getFilename(),
                        node.getLineNum(),
                        "Only super or this are allowed for variable references");
            }
        } else {
            varType = (String) currentVarSymbolTable.lookup(node.getName());
        }
        if (varType != null) {
            node.setExprType(varType);
        } else {
            errorHandler.register(errorHandler.SEMANT_ERROR,
                    this.currentClass.getFilename(),
                    node.getLineNum(),
                    "Undeclared variable " + node.getName());
        }

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
     * @param debug true if one wishes to report errors to the errorhandler
     */
    private boolean checkType(String type, String subtype, ASTNode ast, boolean debug) {
        if(type == null || subtype == NULL || subtype == null) {
            return true;
        }

        if(type.equals(subtype)) {
            if(!(SemanticTools.isPrimitive(subtype) && SemanticTools.isPrimitive(type))) {
                if(debug) {
                    if (!classMap.containsKey(type)) {
                        errorHandler.register(errorHandler.SEMANT_ERROR,
                                this.currentClass.getFilename(),
                                ast.getLineNum(),
                                "Invalid type " + type);
                    }
                    if (!classMap.containsKey(subtype)) {
                        errorHandler.register(errorHandler.SEMANT_ERROR,
                                this.currentClass.getFilename(),
                                ast.getLineNum(),
                                "Invalid type " + subtype);
                    }
                }
            }
            return true;
        }

        //primitive check
        if(!(SemanticTools.isPrimitive(subtype) && SemanticTools.isPrimitive(type)) &&
                !(SemanticTools.isPrimitive(subtype) || SemanticTools.isPrimitive(type))
                ) {
            //check class tree
            if (classMap.containsKey(type) && classMap.containsKey(subtype)) {
                ClassTreeNode currClass = classMap.get(subtype);
                while (currClass.getParent() != null) {
                    currClass = currClass.getParent();
                    if (currClass.getName().equals(type)) {
                        return true;
                    }
                }
                if(debug) {
                    errorHandler.register(errorHandler.SEMANT_ERROR,
                            this.currentClass.getFilename(),
                            ast.getLineNum(),
                            "Invalid subtype " + subtype + " of type " + type);
                }
            } else {
                if(debug) {
                    if (!classMap.containsKey(type)) {
                        errorHandler.register(errorHandler.SEMANT_ERROR,
                                this.currentClass.getFilename(),
                                ast.getLineNum(),
                                "Invalid type " + type);
                    }
                    if (!classMap.containsKey(subtype)) {
                        errorHandler.register(errorHandler.SEMANT_ERROR,
                                this.currentClass.getFilename(),
                                ast.getLineNum(),
                                "Invalid type " + subtype);
                    }
                }
            }
        } else {
            if(debug) {
                errorHandler.register(errorHandler.SEMANT_ERROR,
                        this.currentClass.getFilename(),
                        ast.getLineNum(),
                        "Incompatible types " + type + " and " + subtype);
            }
        }
        return false;
    }

    /**
     * Check types for a binary expr
     * reports error if not legal
     * @param node
     */
    private void checkBinaryExpr(BinaryExpr node) {
        String leftType = node.getLeftExpr().getExprType();
        String rightType = node.getRightExpr().getExprType();
        String operandType = node.getOperandType();

        // compexpr check
        if(operandType != null) {
            if(!(operandType.equals(leftType) && operandType.equals(rightType))) {
                errorHandler.register(errorHandler.SEMANT_ERROR,
                        this.currentClass.getFilename(),
                        node.getLineNum(),
                        "Operands " + leftType + " and "+ rightType
                                + " must be of both type " + operandType );
            } else {
                node.setExprType(node.getOpType());
            }
        } else {
            // if operandtype isnt defined for != and ==
            if(SemanticTools.isPrimitive(leftType) || SemanticTools.isPrimitive(rightType)) {
                if(!leftType.equals(rightType)) {
                    errorHandler.register(errorHandler.SEMANT_ERROR,
                            this.currentClass.getFilename(),
                            node.getLineNum(),
                            "Operands " + leftType + " and "+ rightType + " are incompatible");
                } else {
                    node.setExprType(BOOLEAN);
                }
            } else {
                // types
                if(!(checkType(leftType, rightType, node, false) )) {
                    errorHandler.register(errorHandler.SEMANT_ERROR,
                            this.currentClass.getFilename(),
                            node.getLineNum(),
                            "Operands " + leftType + " and "+ rightType + " are incompatible");
                } else {
                    node.setExprType(BOOLEAN);
                }
            }
        }
    }

    /**
     * check unary expression type legality
     * reports errors to error handler
     * @param node the UnaryExpr node in question
     */
    private void checkUnaryExpr(UnaryExpr node) {
        node.setExprType(node.getOperandType());
        if (!node.getOperandType().equals(node.getExpr().getExprType())) {
            errorHandler.register(errorHandler.SEMANT_ERROR,
                    this.currentClass.getFilename(),
                    node.getLineNum(),
                    "Unary operator " + node.getOpName() + " incompatible with type "
                            + node.getExpr().getExprType());
        }
    }
}
