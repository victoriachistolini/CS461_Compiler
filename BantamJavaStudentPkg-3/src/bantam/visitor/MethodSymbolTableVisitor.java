/**
 * File: MethodSymbolTableVisitor.java
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

import bantam.ast.Class_;
import bantam.ast.Field;
import bantam.ast.Method;
import bantam.ast.Program;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;
import bantam.util.SemanticTools;
import bantam.util.SymbolTable;
import jdk.internal.dynalink.support.ClassMap;

import java.util.Hashtable;

/**
 * Created by Alex on 3/4/17.
 */
public class MethodSymbolTableVisitor extends Visitor {
    private SymbolTable currTable;
    private ErrorHandler errHandler;
    private Class_ currClass;
    private Hashtable<String,ClassTreeNode> classMap;

    /**
     * populates a symbol table with all of the methods in the given class
     * @param ast the ASTNode forming the root of the program
     * @param classMap the classmap for the gien program
     * @param e the ErrorHandler with which to register Semantic analyzer errors
     */
    public void populateSymbolTable(Program ast,
                                    Hashtable<String,ClassTreeNode> classMap,
                                    ErrorHandler e) {
        this.errHandler = e;
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
        this.currTable = (
                this.classMap.get(this.currClass.getName()).getMethodSymbolTable()
        );
        super.visit(classNode);
        return null;
    }

    /**
     * For each method populate the symbol table
     * @param methodNode the method node
     * @return
     */
    @Override
    public Object visit(Method methodNode) {
        if (SemanticTools.isReservedWord(methodNode.getName())) {
            this.errHandler.register(
                    this.errHandler.SEMANT_ERROR,
                    currClass.getFilename(),
                    methodNode.getLineNum(),
                    "Method name is a reserved keyword: " + methodNode.getName());
        }
        if(this.currTable.peek(methodNode.getName()) != null) {
            errHandler.register(
                    errHandler.SEMANT_ERROR,
                    currClass.getFilename(),
                    methodNode.getLineNum(),
                    "Two methods declared with the same name '" +
                            methodNode.getName() + "'"
            );
        } else {
            this.currTable.add(methodNode.getName(), methodNode);
        }
        return null;
    }

    /**
     * If you hit a field, you're past all that we care about so
     * just stop this traversal.
     * @param fieldNode
     * @return
     */
    @Override
    public Object visit(Field fieldNode){
        return null;
    }
}
