/**
 *
 */

package bantam.visitor;

import bantam.ast.*;

/**
 * Created by Alex on 2/25/17.
 */
public class MainMainVisitor extends Visitor {
    private boolean hasClass;
    private boolean hasMethod;

    /**
     * set up the classes fields
     */
    public MainMainVisitor() {
        this.hasClass = false;
        this.hasMethod = false;
    }

    /**
     * returns whether a Main class exists and has a main method
     * within it
     * @param ast the ASTNode forming the root of the tree
     * @return whether a Main class exists with a main method
     */
    public boolean hasMain(Program ast) {
        ast.accept(this);
        return hasClass && hasMethod;
    }

    /**
     * Checks for the class' name and if it is "Main" then continue the
     * search for a main method
     * @param classNode
     * @return
     */
    @Override
    public Object visit(Class_ classNode) {
        if(classNode.getName().equals("Main")) {
            this.hasClass = true;
            super.visit(classNode);
        }
        return null;
    }

    @Override
    public Object visit(Method methodNode) {
        if(methodNode.getName().equals("main")) {
            this.hasMethod = true;
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

    /**
     * very simple test
     * @param args unused
     */
    public static void main(String[] args) {
        return;
    }
}
