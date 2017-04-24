/**
 * File: PrettySupports.java
 * @author Edward (osan) Zhou
 * @author Alex Rinker
 * @author Vivek Sah
 * Class: CS461
 * Project: Extension 1
 * Date: April 24 2017
 */

package bantam.prettyprinter;

import java.io.PrintStream;

/**
 /**
 * Mips assembly support
 * create an object from this class for use in generating Mips code
 */
public class PrettySupport {
    /**
     * The print stream for printing to an assembly file
     */
    private PrintStream out;

    /** the current column the printer is on */
    private int currColNum;

    /**
     * MipsSupport constructor
     *
     * @param out print stream
     */
    public PrettySupport(PrintStream out) {
        this.out = out;
    }
}
