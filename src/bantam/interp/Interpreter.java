/* Bantam Java Compiler and Language Toolset.

   Copyright (C) 2009 by Marc Corliss (corliss@hws.edu) and 
                         David Furcy (furcyd@uwosh.edu) and
                         E Christopher Lewis (lewis@vmware.com).
   ALL RIGHTS RESERVED.

   The Bantam Java toolset is distributed under the following 
   conditions:

     You may make copies of the toolset for your own use and 
     modify those copies.

     All copies of the toolset must retain the author names and 
     copyright notice.

     You may not sell the toolset or distribute it in 
     conjunction with a commerical product or service without 
     the expressed written consent of the authors.

   THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS 
   OR IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE 
   IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A 
   PARTICULAR PURPOSE. 
*/

package bantam.interp;

import bantam.util.*;

/** The <tt>Interpreter</tt> class interprets Bantam Java programs.  It
  * takes the AST representation of the program (which was previously
  * checked for correctness and annotated with type information) and
  * runs the program.
  * 
  * The interpreter package was originally written by Josh Davis with 
  * some modifications by Marc Corliss.
  * 
  * This class is incomplete and will need to be implemented by the student.
  * */
public class Interpreter {
    /** Root of the class hierarchy tree */
    private ClassTreeNode root;
    
    /** Boolean indicating whether debugging is enabled */
    private boolean debug = false;

    /** Interpreter constructor
      * @param root root of the class hierarchy tree
      * @param debug boolean indicating whether debugging is enabled
      * */
    public Interpreter(ClassTreeNode root, boolean debug) {
	this.root = root;
	this.debug = debug;
    }

    /** Interpret the program
      * See the lab manual for the details.
      * */
    public void interpret() {
	// comment out
	throw new RuntimeException("Interpreter unimplemented");

	// add code below...
    }
}


