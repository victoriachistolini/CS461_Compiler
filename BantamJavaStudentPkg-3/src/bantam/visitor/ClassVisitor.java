/**
 * File: ClassVisitor.java
 * @author Edward (osan) Zhou
 * @author Alex Rinker
 * @author Vivek Sah
 * Class: CS461
 * Project: 3
 * Date: Mar 4, 2017
 */

package bantam.visitor;

import bantam.ast.*;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;
import bantam.util.SemanticTools;

import java.util.Hashtable;
import java.util.Map;

/**
 * Populates the class map hierarchy with classtreenodes
 */
public class ClassVisitor extends Visitor{

    /** Error Handler to register semantic errors in class declarations */
    private ErrorHandler errorHandler;
    /** The class map hashtable */
    private Hashtable<String, ClassTreeNode> classMap;

    /**
     * Build the class map hierarchy
     * @param program root AST node of the program
     * @param classMap global class map
     * @param errorHandler errorhandler to report errors
     */
    public void buildClassHierarchy(Program program,
                                    Hashtable<String, ClassTreeNode> classMap,
                                    ErrorHandler errorHandler) {
        this.errorHandler=errorHandler;
        this.classMap=classMap;
        program.accept(this);

        establishHierarchy();
    }

    @Override
    public Object visit(Class_ node) {
        if (this.classMap.containsKey(node.getName())) {
            this.errorHandler.register(
                    this.errorHandler.SEMANT_ERROR,
                    node.getFilename(),
                    node.getLineNum(),
                    "Duplicate Class name -" + node.getName());
        }else if (SemanticTools.isKeyword(node.getName())) {
            this.errorHandler.register(
                    this.errorHandler.SEMANT_ERROR,
                    node.getFilename(),
                    node.getLineNum(),
                    "Class Name is a Reserved Keyword: " + node.getName());
        }
        else {
            this.classMap.put(node.getName(),
                    new ClassTreeNode(node, false, true, this.classMap));
        }

        return null;
    }

    /**
     * Build parent and child relations between class tree nodes and
     * Check classMap for errors after all classes have been established
     * -Errors
     * -extending nonextendables
     * -nonexisting class
     * -inheritance loop
     * Bad inheritance declarations are given Object as a default parent
     */
    private void establishHierarchy() {
        for (Map.Entry<String, ClassTreeNode> entry : classMap.entrySet()) {
            String parentName = entry.getValue().getASTNode().getParent();
            if (parentName !=null) {
                //Set Parents
                if (classMap.containsKey(parentName)) {
                    if (classMap.get(parentName).isExtendable()) {
                        entry.getValue().setParent(classMap.get(parentName));
                    } else {
                        errorHandler.register(errorHandler.SEMANT_ERROR,
                                "Class " + parentName + " is not Extendable");
                        entry.getValue().setParent(classMap.get("Object"));
                    }
                } else {
                    errorHandler.register(errorHandler.SEMANT_ERROR,
                            "Class " + entry.getKey() + " has an invalid parent");
                    entry.getValue().setParent(classMap.get("Object"));
                }
            }
        }

        //Check for inheritance loops
        if (classMap.get("Object").getNumDescendants() != (classMap.size()-1)) {
            errorHandler.register(errorHandler.SEMANT_ERROR, "Inheritance loop detected!");
        }
    }
}
