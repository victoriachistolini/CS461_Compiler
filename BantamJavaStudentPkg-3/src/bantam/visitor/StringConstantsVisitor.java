/**
 * File: StringConstantsVisitor.java
 * @author Victoria Chistolini
 * @author Edward (osan) Zhou
 * @author Alex Rinker
 * @author Vivek Sah
 * Class: CS461
 * Project: 2.5
 * Date: Feb 25, 2017
 */

package bantam.visitor;

import bantam.ast.ConstStringExpr;
import bantam.ast.Program;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates a Map whose keys are the String constants from the program and
 * values are names for the String constants.
 *
 * Value format is: "StringConst_X"; where X is a unique number.
 */

public class StringConstantsVisitor extends Visitor {


    private Map<String,String> stringConstantContainer = new HashMap<>();
    private int nameNum=-1;


    /**
     * gets string constant and value mappings
     * @param ast
     * @return
     */
    public Map<String,String> getStringConstants(Program ast){
        ast.accept(this);
        return stringConstantContainer;

    }

    /**
     * add new entry when visiting a string constant node
     * @param node the string constant expression node
     * @return visit instance
     */
    @Override
    public Object visit(ConstStringExpr node) {

        this.nameNum++;
        stringConstantContainer.put(node.getConstant(), "StringConst_" +
                                    Integer.toString(this.nameNum));
        return super.visit(node);
    }


    public String toString(){
        return stringConstantContainer.toString();
    }

    /**
     * does nothing
     * @param args unused
     */
   public static void main(String[] args) {
       return;
   }

}
