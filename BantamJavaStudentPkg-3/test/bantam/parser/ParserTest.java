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
        legalCodetest("class A { void ifMethod(){ "+ ifStmt + " }}");
        legalCodetest("class A { void ifMethod(){ "+ ifElseStmt + " }}");
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

    @Test
    public void binaryArithTest() throws Exception {
        legalCodetest("class A { void newMethod(){ int x = 4; a = blah.number(); }}");
    }
}