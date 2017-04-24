/**
 * File: PrettyVisitor.java
 * @author Edward (osan) Zhou
 * @author Alex Rinker
 * @author Vivek Sah
 * Class: CS461
 * Project: Extension 1
 * Date: April 24 2017
 */
package bantam.prettyprinter;
import bantam.util.ClassTreeNode;
import bantam.visitor.Visitor;

import java.io.PrintStream;


/**
 * Created by Alex on 4/24/17.
 */
public class PrettyVisitor extends Visitor{
    /** support class for generating properly formatted code */
    private PrettySupport prettySupport;

    /** Print stream for printing to a file */
    private PrintStream out;

    /** root of the class tree */
    private ClassTreeNode root;

    /** builtin classes */
    private String[] builtins = {"Object", "String", "TextIO", "Sys"};

    /**
     * constructor method
     * @param root the root node of the program
     * @param mipsSupport the mipsSupport helper class
     * @param out the PrintStream used to
     */
    public PrettyVisitor(
            ClassTreeNode root,
            PrettySupport mipsSupport,
            PrintStream out) {
        this.prettySupport = mipsSupport;
        this.out = out;
        this.root = root;
    }
}
