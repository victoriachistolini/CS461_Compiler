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
import bantam.util.Location;
import bantam.util.SymbolTable;
import bantam.visitor.Visitor;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Visitor for creating the .text code of mips
 */
public class CodeGeneratorVisitor extends Visitor{
    /** support class for generating code in mips */
    private MipsSupport mipsSupport;

    /** Print stream for printing to a file */
    private PrintStream out;

    /** root of the class tree */
    private ClassTreeNode root;

    /** the current class being traversed */
    private Class_ currClass;

    /** Map containing the labels associated with each class */
    private Map<String, String> classNames;

    /** Map which connects String constants to their label */
    private Map<String, String> stringLabels;

    /** the current offset being used for the fields */
    private int currOffset;

    /** a flag representing whether or not we are generating inits this runthrough */
    private boolean generatingInits;

    /** a map associating each class to a Symbol Table */
    private Map<String, SymbolTable> classSymbolTables;

    /** a String representing the exit label for the current loop being traversed */
    private String loopExit;

    /** a counter representing the number of parameters currently denoted */
    private int numParams;

    /** builtin classes */
    private String[] builtins = {"Object", "String", "TextIO", "Sys"};

    /** current method end label */
    private String currentMethodEnd;

    private final String NULL = "null";
    private final String THIS = "this";
    private final String SUPER = "super";

    /**
     * constructor method
     * @param root the root node of the program
     * @param mipsSupport the mipsSupport helper class
     * @param classNames a map of classnames and labels
     * @param stringLabels
     */
    public CodeGeneratorVisitor(
            ClassTreeNode root,
            MipsSupport mipsSupport,
            PrintStream out,
            Map<String, String> classNames,
            Map<String, String> stringLabels) {
        this.mipsSupport = mipsSupport;
        this.out = out;
        this.root = root;
        this.classNames = classNames;
        this.stringLabels = stringLabels;
        this.currOffset = -12;
        this.classSymbolTables = new HashMap<>();
    }

    /**
     * When visiting the class node, the visitor sets up a new symbol table
     * It generates the label for the init method and then proceeds to
     * traverse through the labels. After traversal, it generates the code
     * to close the init method
     * @param node
     * @return
     */
    @Override
    public Object visit(Class_ node) {
        this.currClass = node;
        if(generatingInits) {
            //Generate the init label
            this.mipsSupport.genLabel(this.currClass.getName() + "_init");

            //Handle Object's init method since it is the base case
            if (node.getName() == "Object") {
                this.mipsSupport.genComment("Object's init method is move $v0 $a0");
                this.mipsSupport.genMove(
                        this.mipsSupport.getResultReg(),
                        this.mipsSupport.getArg0Reg()
                );
            } else {
                this.mipsSupport.genComment("Push RA on the stack");
                this.pushRegister(this.mipsSupport.getRAReg());

                this.mipsSupport.genComment("Call the parent's init method");
                this.mipsSupport.genDirCall(node.getParent() + "_init");

            }
            super.visit(node);
            this.currOffset = -12; //reset the offset
            this.mipsSupport.genComment("Pop RA off of the stack");
            this.popIntoRegister(this.mipsSupport.getRAReg());
            this.mipsSupport.genRetn(); //generate a return to close the init
            return null;
        }
        else {
            for (String cl : builtins) {
                if (node.getName().equals(cl)) {
                    return null;
                }
            }
            return super.visit(node);
        }
    }

    /**
     * Visits the fields node and updates the value in memory if necessary. It
     * stores the offset in the appropriate symbol table for future use
     * @param node
     * @return
     */
    @Override
    public Object visit(Field node) {
        if (generatingInits) {
            //Add the field and location to the symbol table
            Location location = new Location(this.mipsSupport.getT0Reg(), this.currOffset);
            this.classSymbolTables.get(currClass.getName()).add(node.getName(), location);

            super.visit(node); //This will put the expr of the field in $v0

            if (node.getInit() != null) {
                //If there is an expression, we have to store the result to memory
                this.mipsSupport.genComment("Store the initial value of the field in memory");
                this.mipsSupport.genStoreWord(
                        this.mipsSupport.getResultReg(),
                        this.currOffset,
                        this.mipsSupport.getT0Reg()
                );
            }
            //update memory for the next
            this.currOffset += -4;
            return null;
        } else {
            return null;
        }
    }

    @Override
    public Object visit(Method node) {
        if (generatingInits) {
            //Terminate at the method node as this means all fields are generated
            return null;
        } else {
            this.currentMethodEnd = this.mipsSupport.getLabel();
            this.currOffset = 0;
            NumLocalVarsVisitor varCounter = new NumLocalVarsVisitor();
            this.mipsSupport.genLabel(this.currClass.getName() + "." + node.getName());
            this.mipsSupport.genComment("Preamble to the method call");
            this.pushRegister(this.mipsSupport.getRAReg());
            this.pushRegister(this.mipsSupport.getFPReg());

            int numLocalVariables = varCounter.getNumLocalVars(node);
            this.mipsSupport.genComment("Add space for local variables");
            this.mipsSupport.genAdd(
                    this.mipsSupport.getFPReg(),
                    this.mipsSupport.getSPReg(),
                    -4 * numLocalVariables
            );
            this.mipsSupport.genMove(
                    this.mipsSupport.getSPReg(),
                    this.mipsSupport.getFPReg()
            );

            this.mipsSupport.genComment("Start of the method body");

            //Enter the scope for local variables for this method
            this.classSymbolTables.get(currClass.getName()).enterScope();
            super.visit(node);
            //Exit the local variable scope
            this.classSymbolTables.get(currClass.getName()).exitScope();
            this.mipsSupport.genComment("End of the method body");
            this.mipsSupport.genLabel(this.currentMethodEnd);
            this.mipsSupport.genComment("Now starts the epilogue of the method "+node.getName());
            this.mipsSupport.genComment("pop space for "+numLocalVariables+ " local vars");
            this.mipsSupport.genAdd(
                    this.mipsSupport.getSPReg(),
                    this.mipsSupport.getFPReg(),
                    4*numLocalVariables
            );
            this.mipsSupport.genComment("pop the saved $s registers and $ra and $fp");
            this.popIntoRegister(this.mipsSupport.getFPReg());
            this.popIntoRegister(this.mipsSupport.getRAReg());

            this.mipsSupport.genComment("pop actual parameters");

            this.mipsSupport.genAdd(
                    this.mipsSupport.getSPReg(),
                    this.mipsSupport.getSPReg(),
                    0
            );
            this.mipsSupport.genComment("Now return from_method " + node.getName() );
            this.mipsSupport.genRetn();
            return null;
        }
    }

    @Override
    public Object visit(FormalList node) {
        this.numParams = 1;
        return super.visit(node);
    }

    @Override
    public Object visit(Formal node) {
        //Visit the parameter
        super.visit(node);

        this.mipsSupport.genComment("Assigning params to registers");
        if (this.numParams == 1) {
            this.classSymbolTables.get(this.currClass.getName()).add(
                    node.getName(),
                    new Location(mipsSupport.getArg1Reg())
            );
        } else if (this.numParams == 2) {
            this.classSymbolTables.get(this.currClass.getName()).add(
                    node.getName(),
                    new Location(mipsSupport.getArg2Reg())
            );
        } else if (this.numParams ==3) {
            this.classSymbolTables.get(this.currClass.getName()).add(
                    node.getName(),
                    new Location("$a3")
            );
        } else {
            //Store the location of the rest of the params
            this.classSymbolTables.get(this.currClass.getName()).add(
                    node.getName(),
                    new Location(mipsSupport.getFPReg(), currOffset)
            );
            currOffset += -4;
        }
        this.numParams++;
        return null;
    }

    @Override
    public Object visit(StmtList node) {
        return super.visit(node);
    }

    @Override
    public Object visit(DeclStmt node) {
        this.classSymbolTables.get(this.currClass.getName()).add(node.getName(), new Location(mipsSupport.getFPReg(), currOffset));
        if (node.getInit() instanceof NewExpr) {
            pushAVT();
        }
        super.visit(node);
        mipsSupport.genComment("Store variable " + node.getName() + " in local vars");
        mipsSupport.genStoreWord(mipsSupport.getResultReg(), currOffset, mipsSupport.getFPReg());
        currOffset += -4;
        if (node.getInit() instanceof NewExpr) {
            popAVT();
        }
        return null;
    }

    @Override
    public Object visit(ExprStmt node) {
        return super.visit(node);
    }

    @Override
    public Object visit(IfStmt node) {
        this.mipsSupport.genComment("Start If Statement");
        String elseLabel = mipsSupport.getLabel();
        String endLabel = mipsSupport.getLabel();
        node.getPredExpr().accept(this);
        this.mipsSupport.genComment("Branch if false");
        this.mipsSupport.genCondBeq(
                this.mipsSupport.getResultReg(),
                this.mipsSupport.getZeroReg(),
                elseLabel
        );

        this.mipsSupport.genComment("If true, execute the following:");
        node.getThenStmt().accept(this);
        this.mipsSupport.genComment("Branch to End");
        this.mipsSupport.genUncondBr(endLabel);

        this.mipsSupport.genLabel(elseLabel);
        if (node.getElseStmt() != null) {
            node.getElseStmt().accept(this);
        }

        this.mipsSupport.genComment("End of If Statements");
        this.mipsSupport.genLabel(endLabel);

        return super.visit(node);
    }

    @Override
    public Object visit(WhileStmt node) {
        this.classSymbolTables.get(this.currClass.getName()).enterScope();
        this.mipsSupport.genComment("Start While Statement");
        String start = this.mipsSupport.getLabel();
        this.mipsSupport.genLabel(start);
        String end = this.mipsSupport.getLabel();
        this.loopExit = end;
        node.getPredExpr().accept(this);

        this.mipsSupport.genComment("Branch to End if False");
        this.mipsSupport.genCondBeq(
                this.mipsSupport.getResultReg(),
                this.mipsSupport.getZeroReg(),
                end
        );

        this.mipsSupport.genComment("Execute While Loop body");
        node.getBodyStmt().accept(this);

        this.mipsSupport.genComment("Branch to start of while loop");
        this.mipsSupport.genUncondBr(start);

        this.mipsSupport.genComment("End While Statement");
        this.mipsSupport.genLabel(end);
        this.classSymbolTables.get(this.currClass.getName()).exitScope();
        return null;
    }

    @Override
    public Object visit(ForStmt node) {
        this.classSymbolTables.get(this.currClass.getName()).enterScope();
        this.mipsSupport.genComment("Start For Statement");
        if (node.getInitExpr() != null) {
            this.mipsSupport.genComment("Init Expression");
            node.getInitExpr().accept(this);
        }

        String start = this.mipsSupport.getLabel();
        String end = this.mipsSupport.getLabel();
        this.loopExit = end;
        this.mipsSupport.genLabel(start);

        if (node.getPredExpr() != null) {
            node.getPredExpr().accept(this);
            this.mipsSupport.genComment("Branch if loop condition is false");
            this.mipsSupport.genCondBeq(
                    this.mipsSupport.getResultReg(),
                    this.mipsSupport.getZeroReg(),
                    end
            );
        }

        if (node.getUpdateExpr() != null) {
            this.mipsSupport.genComment("Update the looping variable");
            node.getUpdateExpr().accept(this);
        }
        this.mipsSupport.genComment("Execute the body of the for loop");
        node.getBodyStmt().accept(this);


        this.mipsSupport.genComment("End For Statement");
        this.mipsSupport.genLabel(end);
        this.classSymbolTables.get(this.currClass.getName()).exitScope();
        return null;
    }

    @Override
    public Object visit(BreakStmt node) {
        this.mipsSupport.genUncondBr(this.loopExit);
        return null;
    }

    @Override
    public Object visit(BlockStmt node) {
        this.classSymbolTables.get(this.currClass.getName()).enterScope();
        super.visit(node);
        this.classSymbolTables.get(this.currClass.getName()).exitScope();
        return null;
    }

    @Override
    public Object visit(ReturnStmt node) {
        if (node.getExpr() != null) {
            mipsSupport.genComment("Load into v0 the expr");
            node.getExpr().accept(this);
        }
        this.mipsSupport.genUncondBr(this.currentMethodEnd);
        return null;
    }

    @Override
    public Object visit(ExprList node) {
        return super.visit(node);
    }

    @Override
    public Object visit(DispatchExpr node) {
        this.mipsSupport.genComment("Store all variables on the stack");
        pushAVT();

        this.mipsSupport.genComment("Calculate Reference Expression");
        //Make room on the stack for the Reference
        this.mipsSupport.genAdd(
                this.mipsSupport.getSPReg(),
                this.mipsSupport.getSPReg(),
                -4
        );

        String refName;

        if(node.getRefExpr() != null) {
            node.getRefExpr().accept(this);
            this.mipsSupport.genStoreWord(
                    this.mipsSupport.getResultReg(),
                    0,
                    this.mipsSupport.getSPReg()
            );
            refName = (node.getRefExpr()).getExprType();
        } else { //If there isn't a reference, use the 'this' pointer
            this.mipsSupport.genStoreWord(
                    this.mipsSupport.getArg0Reg(),
                    0,
                    this.mipsSupport.getSPReg()
            );
            refName = this.currClass.getName();
        }

        this.mipsSupport.genComment("Calculate Parameters and Store");
        int numberOfParams = 1;
        for(ASTNode param : node.getActualList()) {
            param.accept(this);
            if (numberOfParams == 1) {
                this.mipsSupport.genMove(mipsSupport.getArg1Reg(),
                                         mipsSupport.getResultReg());

            } else if (numberOfParams == 2) {
                this.mipsSupport.genMove(mipsSupport.getArg2Reg(),
                                         mipsSupport.getResultReg());
            } else if (numberOfParams == 3) {
                this.mipsSupport.genMove("$a3",
                                         mipsSupport.getResultReg());
            } else {
                //Store the rest of the params on the stack
                mipsSupport.genStoreWord(
                        mipsSupport.getResultReg(),
                        currOffset,
                        mipsSupport.getFPReg()
                );
                currOffset += -4;
            }
            numberOfParams++;
        }

        this.mipsSupport.genComment("Load Object Ref into a0");
        this.mipsSupport.genLoadWord(
            this.mipsSupport.getArg0Reg(), 0, this.mipsSupport.getSPReg()
        );

        this.mipsSupport.genComment("Compute the location of the method call");
        this.mipsSupport.genComment("and place it in $t0");
        this.mipsSupport.genLoadAddr(
                this.mipsSupport.getT0Reg(),
                refName + "." + node.getMethodName()
        );

        this.mipsSupport.genComment("Execute the dispatch code");
        this.mipsSupport.genInDirCall(this.mipsSupport.getT0Reg());

        this.mipsSupport.genComment("Remove the reference Expression");
        //Pop the reference variable off the stack and get rid of it
        this.mipsSupport.genAdd(
                this.mipsSupport.getSPReg(),
                this.mipsSupport.getSPReg(),
                4
        );

        this.mipsSupport.genComment("Restore the variables stored on the stack");
        popAVT();
        return null;
    }

    /**
     * Pushes all $a $v and $t registers to the stack in
     * an overwhelmingly unnecessary waste of memory.
     */
    private void pushAVT() {
        String[] registers =
                {"$v0", "$v1", "$a0", "$a1", "$a2", "$a3",
                        "$t0", "$t1", "$t2", "$t3", "$t4",
                        "$t5", "$t6", "$t7", "$t8", "$t9",
                };
        for(int i=0; i < registers.length; i++) {
            this.pushRegister(registers[i]);
        }
    }

    /**
     * Pushes all $a $v and $t registers to the stack in
     * an overwhelmingly unnecessary waste of memory.
     */
    private void popAVT() {
        String[] registers =
                {"$v0", "$v1", "$a0", "$a1", "$a2", "$a3",
                        "$t0", "$t1", "$t2", "$t3", "$t4",
                        "$t5", "$t6", "$t7", "$t8", "$t9",
                };
        for(int i=registers.length-1; i > -1; i--) {
            this.popIntoRegister(registers[i]);
        }
    }

    @Override
    public Object visit(NewExpr node) {
        mipsSupport.genLoadAddr(mipsSupport.getT0Reg(), node.getType() + "_template");
        mipsSupport.genDirCall("Object.clone");
        mipsSupport.genDirCall(node.getType() + "_init");
        return null;
    }

    @Override
    public Object visit(InstanceofExpr node) {
        int expr_type = Integer.parseInt(this.classNames.get(node.getExpr().getExprType()));
        int dest_type = Integer.parseInt(this.classNames.get(node.getType()));
        int num_children = this.root.getClassMap().get(node.getType()).getNumChildren();
        int result = 1;
        if(expr_type >= dest_type && expr_type <= dest_type+num_children){
            result = 0;
        }
        mipsSupport.genLoadImm(mipsSupport.getResultReg(), result);
        return null;

    }

    @Override
    public Object visit(CastExpr node) {
        //only do something if downcast
        if (!node.getUpCast()) {
            int cast_to_type_id = Integer.parseInt(this.classNames.get(node.getExpr().getExprType()));
            int expr_type_ID = Integer.parseInt(this.classNames.get(node.getExprType()));
            int num_children = this.root.getClassMap().get(node.getType()).getNumChildren();
            //check of expr type is child of cast_to type
            if (!(expr_type_ID >= cast_to_type_id && expr_type_ID <= cast_to_type_id + num_children)){
                this.mipsSupport.genComment("Push RA on the stack");
                this.pushRegister(this.mipsSupport.getRAReg());

                this.mipsSupport.genComment("Call the _class cast error");
                this.mipsSupport.genDirCall("_class_cast_error");

                this.mipsSupport.genComment("Pop RA off of the stack");
                this.popIntoRegister(this.mipsSupport.getRAReg());
            }

        }
        return null;
    }

    @Override
    public Object visit(AssignExpr node) {
        super.visit(node);
        Location loc = (Location) this.classSymbolTables.get(currClass.getName()).lookup(node.getName());
        if (loc.isInMemory()) {
            mipsSupport.genStoreWord(mipsSupport.getResultReg(), loc.getOffset(), loc.getBaseReg());
        } else {
            mipsSupport.genMove(loc.getReg(), mipsSupport.getResultReg());
        }
        return null;
    }

    @Override
    public Object visit(BinaryCompEqExpr node) {
        mipsSupport.genComment("-Begin " + node.getOpName() + "-");
        visitLRExpressions(node);
        mipsSupport.genComment(mipsSupport.getResultReg() + " " + node.getOpName() +" $v1 -> " + mipsSupport.getResultReg());
        mipsSupport.genSub(mipsSupport.getResultReg(), mipsSupport.getResultReg(), "$v1");
        out.println("\tseq " + mipsSupport.getResultReg() + " " + mipsSupport.getResultReg() + " $zero");
        mipsSupport.genComment("-End " + node.getOpName() + "-");
        return null;
    }

    @Override
    public Object visit(BinaryCompNeExpr node) {
        mipsSupport.genComment("-Begin " + node.getOpName() + "-");
        visitLRExpressions(node);
        mipsSupport.genComment(mipsSupport.getResultReg() + " " + node.getOpName() +" $v1 -> " + mipsSupport.getResultReg());
        mipsSupport.genSub(mipsSupport.getResultReg(), mipsSupport.getResultReg(), "$v1");
        mipsSupport.genComment("-End " + node.getOpName() + "-");
        return null;
    }

    @Override
    public Object visit(BinaryCompLtExpr node) {
        visitBinaryCompExpr(node);
        return null;
    }

    @Override
    public Object visit(BinaryCompLeqExpr node) {
        visitBinaryCompExpr(node);
        return null;
    }

    @Override
    public Object visit(BinaryCompGtExpr node) {
        visitBinaryCompExpr(node);
        return null;
    }

    @Override
    public Object visit(BinaryCompGeqExpr node) {
        visitBinaryCompExpr(node);
        return null;
    }

    /** boiler plate code generator for binary comp expressions */
    private void visitBinaryCompExpr(BinaryCompExpr node) {
        String t = mipsSupport.getLabel(); //true
        String end = mipsSupport.getLabel();
        mipsSupport.genComment("-Begin " + node.getOpName() + "-");
        visitLRExpressions(node);
        mipsSupport.genComment("Compare $v0 and $v1");
        if (node.getOpName().equals(">=")) {
            mipsSupport.genCondBgeq(mipsSupport.getResultReg(), "$v1", t);
        } else if (node.getOpName().equals(">")) {
            mipsSupport.genCondBgt(mipsSupport.getResultReg(), "$v1", t);
        } else if (node.getOpName().equals("<=")) {
            mipsSupport.genCondBleq(mipsSupport.getResultReg(), "$v1", t);
        } else if (node.getOpName().equals("<")) {
            mipsSupport.genCondBlt(mipsSupport.getResultReg(), "$v1", t);
        }
        mipsSupport.genComment("If not " + node.getOpName() + " set $v0 to 0");
        mipsSupport.genLoadImm(mipsSupport.getResultReg(), 0);
        mipsSupport.genUncondBr(end);
        mipsSupport.genComment("If "+ node.getOpName() +" set $v0 to 1");
        mipsSupport.genLabel(t);
        mipsSupport.genLoadImm(mipsSupport.getResultReg(), 1);
        mipsSupport.genLabel(end);
        mipsSupport.genComment("-End " + node.getOpName() + "-");
    }

    @Override
    public Object visit(BinaryArithPlusExpr node) {
        mipsSupport.genComment("-Begin PLUS-");
        visitLRExpressions(node);
        mipsSupport.genComment(mipsSupport.getResultReg() + " " + node.getOpName() +" $v1 -> " + mipsSupport.getResultReg());
        mipsSupport.genAdd(mipsSupport.getResultReg(), mipsSupport.getResultReg(), "$v1");
        mipsSupport.genComment("-End PLUS-");
        return null;
    }

    @Override
    public Object visit(BinaryArithMinusExpr node) {
        mipsSupport.genComment("-Begin MINUS-");
        visitLRExpressions(node);
        mipsSupport.genComment(mipsSupport.getResultReg() + " " + node.getOpName() +" $v1 -> " + mipsSupport.getResultReg());
        mipsSupport.genSub(mipsSupport.getResultReg(), mipsSupport.getResultReg(), "$v1");
        mipsSupport.genComment("-End MINUS-");
        return null;
    }

    @Override
    public Object visit(BinaryArithTimesExpr node) {
        mipsSupport.genComment("-Begin MULTIPLY-");
        visitLRExpressions(node);
        mipsSupport.genComment(mipsSupport.getResultReg() + " " + node.getOpName() +" $v1 -> " + mipsSupport.getResultReg());
        mipsSupport.genMul(mipsSupport.getResultReg(), mipsSupport.getResultReg(), "$v1");
        mipsSupport.genComment("-End MULTIPLY-");
        return null;
    }

    @Override
    public Object visit(BinaryArithDivideExpr node) {
        mipsSupport.genComment("-Begin DIVIDE-");
        visitLRExpressions(node);
        mipsSupport.genComment(mipsSupport.getResultReg() + " " + node.getOpName() +" $v1 -> " + mipsSupport.getResultReg());
        mipsSupport.genDiv(mipsSupport.getResultReg(), mipsSupport.getResultReg(), "$v1");
        mipsSupport.genComment("-End DIVIDE-");
        return null;
    }

    @Override
    public Object visit(BinaryArithModulusExpr node) {
        mipsSupport.genComment("-Begin MODULUS-");
        visitLRExpressions(node);
        mipsSupport.genComment(mipsSupport.getResultReg() + " " + node.getOpName() +" $v1 -> " + mipsSupport.getResultReg());
        mipsSupport.genMod(mipsSupport.getResultReg(), mipsSupport.getResultReg(), "$v1");
        mipsSupport.genComment("-End MODULUS-");
        return null;
    }

    /** Generate boiler plate code for visiting a left (= v0) and right (= v1) expression */
    private void visitLRExpressions(BinaryExpr node) {
        node.getLeftExpr().accept(this);
        mipsSupport.genComment("Push $v0 to the stack");
        mipsSupport.genSub(mipsSupport.getSPReg(), mipsSupport.getSPReg(), mipsSupport.getWordSize());
        mipsSupport.genStoreWord(mipsSupport.getResultReg(), 0, mipsSupport.getSPReg());
        node.getRightExpr().accept(this);
        mipsSupport.genComment("Move $v0 to $v1");
        mipsSupport.genMove("$v1", mipsSupport.getResultReg());
        mipsSupport.genComment("Pop the stack to $v0");
        mipsSupport.genLoadWord(mipsSupport.getResultReg(), 0, mipsSupport.getSPReg());
        mipsSupport.genAdd(mipsSupport.getSPReg(), mipsSupport.getSPReg(), mipsSupport.getWordSize());
    }

    @Override
    public Object visit(BinaryLogicAndExpr node) {
        String uniqueLabel = mipsSupport.getLabel();
        mipsSupport.genComment("-Begin AND-");
        node.getLeftExpr().accept(this);
        mipsSupport.genComment("Short circuit jump to " + uniqueLabel + " for AND");
        mipsSupport.genCondBeq(mipsSupport.getResultReg(), mipsSupport.getZeroReg(), uniqueLabel);
        node.getRightExpr().accept(this);
        mipsSupport.genComment("-End AND-");
        mipsSupport.genLabel(uniqueLabel);
        return null;
    }

    @Override
    public Object visit(BinaryLogicOrExpr node) {
        String uniqueLabel = mipsSupport.getLabel();
        mipsSupport.genComment("-Begin OR-");
        node.getLeftExpr().accept(this);
        mipsSupport.genComment("Short circuit jump to " + uniqueLabel + " for OR");
        mipsSupport.genCondBne(mipsSupport.getResultReg(), mipsSupport.getZeroReg(), uniqueLabel);
        node.getRightExpr().accept(this);
        mipsSupport.genComment("-End OR-");
        mipsSupport.genLabel(uniqueLabel);
        return null;
    }

    @Override
    public Object visit(UnaryNegExpr node) {
        mipsSupport.genComment("Negate: " + mipsSupport.getResultReg());
        mipsSupport.genNeg(mipsSupport.getResultReg(), mipsSupport.getResultReg());
        return null;
    }

    @Override
    public Object visit(UnaryNotExpr node) {
        super.visit(node);
        mipsSupport.genComment("Not: " + mipsSupport.getResultReg());
        out.println("\tseq $v0 $v0 $zero");
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

    @Override
    public Object visit(VarExpr node) {
        //Visit ref if not null
        if (node.getRef() != null) {
            node.getRef().accept(this);
        }
        if (node.getName().equals(NULL)) {
            this.mipsSupport.genLoadImm(mipsSupport.getResultReg(), 0);
            return null;
        } else if (node.getName().equals(THIS)) {
            node.setExprType(currClass.getName());
            return null;
        } else if (node.getName().equals(SUPER)) {
            node.setExprType(this.currClass.getParent());
            return null;
        }
        Location loc;
        if (node.getRef() != null) {
            if (node.getRef().getExprType().equals(SUPER)) {
                loc = (Location) this.classSymbolTables.get(
                        node.getRef().getExprType()).lookup(node.getName()
                );
            } else {
                loc = (Location) this.classSymbolTables.get(
                        this.currClass.getName()).lookup(node.getName()
                );
            }
        } else {
            //Get the location of the VarExpr
            loc = (Location) this.classSymbolTables.get(
                    this.currClass.getName()).lookup(node.getName()
            );
        }
        this.mipsSupport.genComment("Load Variable");
        if(loc.isInMemory()) {
            this.mipsSupport.genLoadWord(
                    this.mipsSupport.getResultReg(),
                    loc.getOffset(),
                    loc.getBaseReg()
            );
        } else { //If the data is stored in a register rather than memory
            this.mipsSupport.genMove(
                    this.mipsSupport.getResultReg(),
                    loc.getReg()
            );
        }
        return null;
    }

    //Text generation methods
    /**
     * This method starts up the visitor which generates the
     * entire text section of the MIPS file
     * @return
     */
    public void generateText() {
        //The first pass will generate the init methods
        this.generatingInits = true;
        generateInit(this.root);

        //The second pass will generate the code
        this.generatingInits = false;
        generateText(this.root);
    }

    /**
     * Visits the parent node and proceeds to visit each of it's children
     * thus traversing the parent node's tree as well as its children's
     * generating the init methods for each
     * @param parent
     */
    private void generateInit(ClassTreeNode parent) {
        //Update the symbol table and symbol table for future use
        this.classSymbolTables.put(parent.getName(), new SymbolTable());
        this.classSymbolTables.get(parent.getName()).enterScope();
        parent.getASTNode().accept(this);

        //Generate inits for each of the child classes
        parent.getChildrenList().forEachRemaining( child -> {
            generateInit(child);
            this.classSymbolTables.get(child.getName()).setParent(this.classSymbolTables.get(parent.getName()));
        });
    }

    /**
     * Visits the parent node and proceeds to visit each of it's children
     * generating the appropriate .text code for each
     * @param parent
     */
    private void generateText(ClassTreeNode parent) {
        parent.getASTNode().accept(this);

        //Generate inits for each of the child classes
        parent.getChildrenList().forEachRemaining( child ->
                generateText(child)
        );
    }

    /**
     * pushes the value of the input register onto the
     * stack
     * @param register
     */
    private void pushRegister(String register) {
        this.mipsSupport.genComment("Pushing " + register + " onto the stack");
        this.mipsSupport.genAdd(
                this.mipsSupport.getSPReg(),
                this.mipsSupport.getSPReg(),
                -4
        );
        this.mipsSupport.genStoreWord(
                register,
                0,
                this.mipsSupport.getSPReg()
        );
    }

    /**
     * Pops the top value on the stack into the input
     * register
     * @param register
     */
    private void popIntoRegister(String register) {
        this.mipsSupport.genComment("Popping from the stack into " + register);
        this.mipsSupport.genLoadWord(
                register,
                0,
                this.mipsSupport.getSPReg()
        );

        this.mipsSupport.genAdd(
                this.mipsSupport.getSPReg(),
                this.mipsSupport.getSPReg(),
                4
        );
    }
}
