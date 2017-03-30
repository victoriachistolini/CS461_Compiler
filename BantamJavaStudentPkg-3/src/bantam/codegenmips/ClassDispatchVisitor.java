package bantam.codegenmips;

import bantam.ast.*;
import bantam.util.ClassTreeNode;
import bantam.visitor.Visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * generates mips class templates in the .data section
 */
public class ClassDispatchVisitor extends Visitor {

    /** Object class tree node */
    private ClassTreeNode root;

    /** Class for generating mips*/
    private MipsSupport mipsSupport;

    /** Current class */
    private String currClass;

    /** List of current methods */
    private ArrayList<String> currMethods;

    ClassDispatchVisitor(ClassTreeNode root, MipsSupport mipsSupport) {
        this.root = root;
        this.mipsSupport = mipsSupport;
        currMethods = new ArrayList<>();
    }

    /**
     * Generate dispatch tables
     */
    public void generateDispatchTables() {
        generateDispatchTables(root, new ArrayList<>());
    }

    private void generateDispatchTables(ClassTreeNode parent, ArrayList<String> parentMethods) {
        parent.getASTNode().accept(this);
        ArrayList<String> finalMethods = new ArrayList<>(parentMethods);
        ArrayList<String> tempMethods = new ArrayList<>(currMethods);
        for(int i = 0; i<parentMethods.size(); i++) {
            for(int j = 0; j < currMethods.size(); j++) {
                if(parentMethods.get(i).endsWith("." + currMethods.get(j))) {
                    finalMethods.set(i, currClass + "." + currMethods.get(j));
                    tempMethods.remove(currMethods.get(j));
                }
            }
        }

        //attach current class methods to final methods
        for(int i = 0; i<tempMethods.size(); i++) {
            finalMethods.add(currClass + "." + tempMethods.get(i));
        }
        finalMethods.forEach(e -> mipsSupport.genWord(e));
        currMethods = new ArrayList<>();
        parent.getChildrenList().forEachRemaining(x -> generateDispatchTables(x, finalMethods));
    }

    @Override
    public Object visit(Class_ node) {
        this.currClass = node.getName();
        mipsSupport.genLabel(this.currClass + "_dispatch_table");
        return super.visit(node);
    }

    @Override
    public Object visit(Field node) {
        return null;
    }

    @Override
    public Object visit(Method node) {
        currMethods.add(node.getName());
        return null;
    }
}
