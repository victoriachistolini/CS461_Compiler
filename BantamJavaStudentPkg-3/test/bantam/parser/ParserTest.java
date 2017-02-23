/**
 * File: ParserTest.java
 * @author djskrien
 * @author Victoria Chistolini
 * @author Edward (osan) Zhou
 * @author Alex Rinker
 * @author Vivek Sah
 * Class: CS461
 * Project: 2
 * Date: Feb 21, 2017
 */

package bantam.parser;

import bantam.ast.ClassList;
import bantam.ast.Class_;
import bantam.ast.Program;
import java_cup.parser;
import java_cup.runtime.Symbol;
import bantam.lexer.Lexer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import bantam.util.ErrorHandler;

import java.io.StringReader;

import static org.junit.Assert.*;


public class ParserTest
{
    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @BeforeClass
    public static void begin() {
        /* add here any initialization code for all test methods. For example,
         you might want to initialize some fields here. */
    }

    /**
     * A generic legality test, the input string should be a representation of
     * a legal bantam java file
     * @params legalCode a String of legal Bantam Java code.
     */
    private void legalCodetest(String legalCode) throws Exception {
        Parser parser = new Parser(new Lexer(new StringReader(legalCode)));
        boolean thrown = false;

        try {
            parser.parse();
        } catch (RuntimeException e) {
            thrown = true;
            assertEquals("Bantam parser found errors.", e.getMessage());
            for (ErrorHandler.Error err : parser.getErrorHandler().getErrorList()) {
                System.out.println(err);
            }
        }
        assertFalse(thrown);
    }

    /**
     * A generic illegality test, the input string should be a representation of
     * an illegal bantam java file
     * @params illegalCode a String of illegal Bantam Java code.
     */
    private void illegalCodetest(String illegalCode) throws Exception {
        Parser parser = new Parser(new Lexer(new StringReader(illegalCode)));
        boolean thrown = false;

        try {
            parser.parse();
        } catch (RuntimeException e) {
            thrown = true;
            assertEquals("Bantam parser found errors.", e.getMessage());
            for (ErrorHandler.Error err : parser.getErrorHandler().getErrorList()) {
                System.out.println(err);
            }
        }
        assertTrue(thrown);
    }

    /** tests the case of a Main class with no members */
    @Test
    public void emptyMainClassTest() throws Exception {
        Parser parser = new Parser(new Lexer(new StringReader("class Main {  }")));
        Symbol result = parser.parse();
        assertEquals(0, parser.getErrorHandler().getErrorList().size());
        assertNotNull(result);
        ClassList classes = ((Program) result.value).getClassList();
        assertEquals(1, classes.getSize());
        Class_ mainClass = (Class_) classes.get(0);
        assertEquals("Main", mainClass.getName());
        assertEquals(0, mainClass.getMemberList().getSize());
    }

    /**
     * tests the case of a missing right brace at end of a class def
     * using an ExpectedException Rule
     */
    @Test
    public void unmatchedLeftBraceTest1() throws Exception {
        Parser parser = new Parser(new Lexer(new StringReader("class Main {  ")));
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Bantam parser found errors.");
        parser.parse();
    }

    /**
     * tests the case of a missing right brace at end of a class def.
     * This version is like unmatchedLeftBraceTest1 except that it
     * doesn't use an ExpectedException Rule and it also prints the error messages.
     */
    @Test
    public void unmatchedLeftBraceTest2() throws Exception {
        Parser parser = new Parser(new Lexer(new StringReader("class Main {  ")));
        boolean thrown = false;

        try {
            parser.parse();
        } catch (RuntimeException e) {
            thrown = true;
            assertEquals("Bantam parser found errors.", e.getMessage());
            for (ErrorHandler.Error err : parser.getErrorHandler().getErrorList()) {
                System.out.println(err);
            }
        }
        assertTrue(thrown);
    }

    /**
     * A class with a single field
     */
    @Test
    public void shortLegalFiletest() throws Exception {
        legalCodetest("class A { int a = 4+5; }");
    }

    /**
     * A method test
     */
    @Test
    public void methodTest() throws Exception {
        legalCodetest("class A { void stuff(){ int a = 4+5; }}");
    }

    /**
     * A legal if-statment
     */
    @Test
    public void ifTest() throws Exception {
        String ifStmt = "int x = 4; if (x>10) return;";
        String ifElseStmt = "int x = 4; if (x <15) x++ ; else return;";
        String ifElseNestedStmt = "int x = 4; if (x <15) x++ ; else if (d < 5) f = 7; else  return;";
        legalCodetest("class A { void ifMethod(){ "+ ifStmt + " }}");
        legalCodetest("class A { void ifMethod(){ "+ ifElseStmt + " }}");
        legalCodetest("class A { void ifMethod(){ "+ ifElseNestedStmt + " }}");
    }

    /**
     * A legal if-statment
     */
    @Test
    public void fieldTest() throws Exception {
        legalCodetest("class A { void ifMethod(){ int a = 4; if (x <15) x++ ; }}");
    }
    // Illegal if-statment

    /**
     * A test to ensure that the lex error messages provide the
     * user with relevant debugging information
     */
    @Test
    public void lexErrorMessageTest() throws Exception {
        illegalCodetest("class A { \nint 9a = 3; }");
    }



    /**
     * A legal for-loop
     */
    @Test
    public void forTest() throws Exception {
        legalCodetest("class A { void ifMethod(){ for(;;) x=2;  }}");
        legalCodetest("class A { void ifMethod(){ for(;;x=2) x=2;  }}");

        legalCodetest("class A { void ifMethod(){ for(;x=2;) x=2;  }}");

        legalCodetest("class A { void ifMethod(){ for(;x=2;x=3) x=2;  }}");
        legalCodetest("class A { void ifMethod(){ for(x=2;;) x=2;  }}");

        legalCodetest("class A { void ifMethod(){ for(x=2;;x=2) x=2;  }}");

        legalCodetest("class A { void ifMethod(){ for(x=2;x=2;) x=2;  }}");
        legalCodetest("class A { void ifMethod(){ for(x=2;x< 3;x++) x=2;  }}");
    }

    /**
     * Legal while statement
     */
    @Test
    public void whileTest() throws Exception {
        legalCodetest("class A { void whileMethod(){ while(x=2 ) x++;  }}");
    }

    /**
     * Legal break statement
     */
    @Test
    public void breakTest() throws Exception {
        legalCodetest("class A { void breakMethod(){ break; }}");
    }

    /**
     * Legal block statement
     */
    @Test
    public void blockTest() throws Exception {
        legalCodetest("class A { void blockMethod(){ while(a < 3){ a=3;b=4;c=9;} }}");
    }

    /**
     * Legal new expression
     */
    @Test
    public void newTest() throws Exception {
        legalCodetest("class A { void newMethod(){ a = new Stuff(); }}");
        legalCodetest("class A { void newMethod(){ a = new String[5+6]; }}");
    }


    /**
     * Legal cast expression
     */
    @Test
    public void castTest() throws Exception {
        legalCodetest("class A { void newMethod(){ a = (int)( 6+9); }}");
        legalCodetest("class A { void newMethod(){ a = (int[])(\"String\"); }}");
    }


    /**
     * binary arith expression, with all possible types of expressions
     */
    @Test
    public void binaryArithTest() throws Exception {

        // try minus binary arith test with stringConstExpr and newExpr
        legalCodetest("class A { void newMethod(){ a = \"String\"++ - new array[10]; }}");

        // try modulus bin arith with an instanceof expr
        legalCodetest("class A { void newMethod(){ q = thing1 instanceof A % thing2 instanceof B; }}");

        // try multiplication bin arith with a dispatch and a varExpr
        //legalCodetest("class A { void newMethod(int x,int y){result =  a.shape() * q.shape ; }}");

        // try addition bin arith with dynamic dispatches
        legalCodetest("class A { void newMethod(){ this.Stuff[x++] + x=5.number(); }}");

        // try divide bin arith with
        legalCodetest("class A { void newMethod(){ q = (((thing[]) (junk)) / ((cooolerJunk) (intJunk) )); }}");
    }



    /**
     * binary comparison expression, with binary logic
     */
    @Test
    public void binaryCompTest() throws Exception {
        // testing all binary comparisons with the binary logic OR
        legalCodetest("class A { void newCompMethod(){ int a =0; if ( (--a == b) || (b !=c) ) { b = ( (c < d) || (d <= e) ); f = ((g > h) || (h >= i));} } }");
        // testing all binary comparisions with binary logic OR / AND
        legalCodetest("class A { void newCompMethod(){ int a =0; if ( (a == b) || (b !=c) ) { b = ( (c < d) && (d <= e) ); f = ((g > h) || (h >= i));} } }");


    }


    @Test
    public void unaryTest() throws Exception {
        // testing negative unary
        legalCodetest("class A { void unaryMethod(){ a=-b; b=!a; } }");
        // unary incr
        legalCodetest("class A { void unaryMethod(){ a=++b; b=a++; c = --d; d = c--; } }");


    }

}