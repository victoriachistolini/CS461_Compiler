/**
 * File: CodeGeneratorVisitor.java
 * @author Edward (osan) Zhou
 * @author Alex Rinker
 * @author Vivek Sah
 * Class: CS461
 * Project: 4B
 * Date: April 6 2017
 */
package bantam.codegenmips;

import bantam.ast.Class_;
import bantam.ast.Field;
import bantam.ast.Method;
import bantam.util.ClassTreeNode;
import bantam.util.Location;
import bantam.util.SymbolTable;

import java.io.PrintStream;
import java.util.Map;

/**
 * Visitor for creating the init methods in mips
 */
public class CodeGeneratorInitVisitor extends CodeGeneratorVisitor {

    /** support class for generating code in mips */
    private MipsSupport mipsSupport;

    /** root of the class tree */
    private ClassTreeNode root;

    /** Print stream for printing to a file */
    private PrintStream out;

    /** the name of the current class being traversed */
    private String currClassName;

    /** the current varSymbolTable being used */
    private SymbolTable currClassVarTable;

    /** the current offset being used for the fields */
    private int currOffset;

    /** Map containing the labels associated with each class */
    private Map<String, String> classNames;

    /** Map which connects String constants to their label */
    private Map<String, String> stringLabels;

    /**
     * constructor method
     * @param root the root node of the program
     * @param mipsSupport the mipsSupport helper class
     * @param classNames a map of classnames and labels
     * @param stringLabels
     */
    public CodeGeneratorInitVisitor(
            ClassTreeNode root,
            MipsSupport mipsSupport,
            PrintStream out,
            Map<String, String> classNames,
            Map<String, String> stringLabels) {
        super(root, mipsSupport, out, classNames, stringLabels);
        this.mipsSupport = mipsSupport;
        this.root = root;
        this.out = out;
        this.classNames = classNames;
        this.stringLabels = stringLabels;
        this.currOffset = 12;
    }

    /**
     * Generates init methods for each class in the program
     * @return
     */
    public void generateInits() {
        generateInits(this.root);
    }

    /**
     * Helper method for generateInits. Generates the init methods for
     * the parent class and all of its immediate children
     * @param parent
     */
    private void generateInits(ClassTreeNode parent) {
        //Update the symbol table and symbol table for future use
        this.currClassVarTable = parent.getVarSymbolTable();
        parent.getASTNode().accept(this);

        //Generate inits for each of the child classes
        parent.getChildrenList().forEachRemaining( child ->
            generateInits(child)
        );
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
        //Update the current class name
        this.currClassName = node.getName();
        this.initializeSymbolTable();

        //Generate the init label
        this.mipsSupport.genLabel(this.currClassName + "_init");

        //Handle Object's init method since it is the base case
        if(node.getName() == "Object") {
            this.mipsSupport.genComment("Object's init method is move $v0 $a0");
            this.mipsSupport.genMove(
                    this.mipsSupport.getResultReg(),
                    this.mipsSupport.getArg0Reg()
            );
        } else {
            this.mipsSupport.genComment("Call the parent's init method");
            this.mipsSupport.genDirCall(node.getParent() + "_init");
        }
        super.visit(node);
        this.currOffset = 12; //reset the offset
        this.mipsSupport.genRetn(); //generate a return to close the init
        return null;
    }


    /**
     * Visits the fields node and updates the value in memory if necessary. It
     * stores the offset in the appropriate symbol table for future use
     * @param node
     * @return
     */
    @Override
    public Object visit(Field node) {
        super.visit(node); //This will put the expr of the field in $v0
        Location location = new Location(this.mipsSupport.getArg0Reg(), this.currOffset);
        if(node.getInit() != null) {
            //If there is an expression, we have to store the result to memory
            this.mipsSupport.genComment("Store the initial value of the field in memory");
            this.mipsSupport.genStoreWord(
                    this.mipsSupport.getResultReg(),
                    this.currOffset,
                    this.mipsSupport.getArg0Reg()
            );
        }
        //Add the field and location to the symbol table
        this.currClassVarTable.add(node.getName(), location);
        //update memory for the next
        this.currOffset += 4;
        return null;
    }

    //Terminate at the method node as this means all fields are generated
    @Override
    public Object visit(Method node) {
        return null;
    }

    /**
     * Sets up a symbol table for the current class node
     */
    private void initializeSymbolTable() {
        //Set up the class' symbol table
        this.currClassVarTable = new SymbolTable();
        this.currClassVarTable.enterScope();
    }
}
