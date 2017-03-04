package bantam.visitor;

import bantam.ast.Class_;
import bantam.ast.Field;
import bantam.ast.Method;
import bantam.util.ErrorHandler;
import bantam.util.SymbolTable;

/**
 * Created by Alex on 3/4/17.
 */
public class MethodSymbolTableVisitor extends Visitor {
    private SymbolTable methodTable;
    private ErrorHandler errHandler;
    private Class_ currClass;

    /**
     * returns whether a Main class exists and has a main method
     * within it
     * @param classNode the ASTNode forming the root of the class
     */
    public void populateSymbolTable(Class_ classNode,
                                           SymbolTable table,
                                           ErrorHandler e) {
        this.errHandler = e;
        this.currClass = classNode;
        this.methodTable = table;
        classNode.accept(this);
    }

    /**
     * For each method checks if it is the main method
     * @param methodNode
     * @return
     */
    @Override
    public Object visit(Method methodNode) {
        if(this.methodTable.lookup(methodNode.getName()) != null) {
            errHandler.register(
                    errHandler.SEMANT_ERROR,
                    currClass.getFilename(),
                    methodNode.getLineNum(),
                    "Two methods declared with the same name '" +
                            methodNode.getName() + "'"
            );
        }
        this.methodTable.add(methodNode.getName(), methodNode);
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
