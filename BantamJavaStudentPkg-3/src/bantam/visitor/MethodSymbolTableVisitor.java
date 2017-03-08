package bantam.visitor;

import bantam.ast.Class_;
import bantam.ast.Field;
import bantam.ast.Method;
import bantam.util.ErrorHandler;
import bantam.util.SemanticTools;
import bantam.util.SymbolTable;

/**
 * Created by Alex on 3/4/17.
 */
public class MethodSymbolTableVisitor extends Visitor {
    private SymbolTable methodTable;
    private ErrorHandler errHandler;
    private Class_ currClass;

    /**
     * populates a symbol table with all of the methods in the given class
     * @param classNode the ASTNode forming the root of the class
     * @param table the SymbolTable to populate
     * @param e the ErrorHandler with which to register Semantic analyzer errors
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
        if(this.methodTable.peek(methodNode.getName()) != null) {
            errHandler.register(
                    errHandler.SEMANT_ERROR,
                    currClass.getFilename(),
                    methodNode.getLineNum(),
                    "Two methods declared with the same name '" +
                            methodNode.getName() + "'"
            );
        } else {
            this.methodTable.add(methodNode.getName(), methodNode);
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
