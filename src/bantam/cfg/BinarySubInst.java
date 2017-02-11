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

package bantam.cfg;

/** A class representing a three-address code binary integer subtraction instruction 
  * (BinarySubInst).  A binary subtraction instruction consists of a destination variable
  * a left source operand (inherited from BinaryInst) and a right source operand
  * (inherited from BinaryInst).  From TACInst it also inherits an opcode 
  * (BINSUB).
  * 
  * @author Marc Corliss and Lori Pietraszek
  * */
public class BinarySubInst extends BinaryInst {
    /** BinarySubInst constructor
      * @param destination destination variable
      * @param leftSource left source variable
      * @param rightSource right source variable
      * */
    public BinarySubInst(String destination, String leftSource, String rightSource) {
	super(TACInst.BINSUB, destination, leftSource, rightSource);

	// some error checking

	// opcode and destination error checked in super class
	// only need to error check source

	// check that left source is either valid variable or int constant
	if (!TACInst.checkVar(leftSource) && !TACInst.checkIntConst(leftSource))
	    throw new IllegalArgumentException("Bad left source operand '" +
					       leftSource + "' in BinarySubInst constructor; " +
					       "must be a legal variable or int constant");

	// check that right source is either valid variable or int constant
	if (!TACInst.checkVar(rightSource) && !TACInst.checkIntConst(rightSource))
	    throw new IllegalArgumentException("Bad right source operand '" +
					       rightSource + "' in BinarySubInst constructor; " +
					       "must be a legal variable or int constant");
    }

    /** Set the left soure operand
      * @param leftSource new left source operand
      * */
    public void setLeftSource(String leftSource) {
	if (!TACInst.checkVar(leftSource) && !TACInst.checkIntConst(leftSource))
	    throw new IllegalArgumentException("Bad left source operand '" +
					       leftSource + "' in BinarySubInst.setSource; " +
					       "must be a legal variable or int constant");
	this.leftSource = leftSource;
    }

    /** Set the right soure operand
      * @param rightSource new right source operand
      * */
    public void setRightSource(String rightSource) {
	if (!TACInst.checkVar(rightSource) && !TACInst.checkIntConst(rightSource))
	    throw new IllegalArgumentException("Bad right source operand '" +
					       rightSource + "' in BinarySubInst.setSource; " +
					       "must be a legal variable or int constant");
	this.rightSource = rightSource;
    }
}
