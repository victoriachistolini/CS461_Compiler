package bantam.semant;

import bantam.ast.Program;
import bantam.lexer.Lexer;
import bantam.util.SemanticTools;
import org.junit.Test;
import bantam.parser.Parser;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/*
 * File: SemanticAnalyzerTest.java
 * Author: djskrien
 * Date: 2/13/17
 */
public class SemanticAnalyzerTest
{

    /** tests the case of a Main class with no members.  This is illegal
     * because a Bantam Java program must have a Main class with a main
     * method. */
    @Test
    public void testClassHierarchy() throws Exception {
        boolean thrown = false;
        SemanticAnalyzer analyzer = setupSemanFromFile("ClassHierarchyTest.btm");
        try {
            analyzer.analyze();
        } catch (RuntimeException e) {
            thrown = true;
            Set<String> errors = new HashSet<>();
            analyzer.getErrorHandler().getErrorList().forEach( error ->
                    errors.add(error.getMessage())
            );
            assertTrue(errors.remove("Duplicate Class name Object"));
            assertTrue(errors.remove("Class String is not Extendable"));
            assertTrue(errors.remove("Class inheritance loop detected!"));
            assertTrue(errors.remove("Class name is a reserved keyword: super"));
            assertTrue(errors.remove("Class C has an invalid parent"));
            assertTrue(errors.isEmpty());
        }
        assertTrue(thrown);
    }

    /**
     * Tests the Semantic Analyzer's recognition of reserved words as class names,
     * identifiers, or method names
     * @throws Exception Can throw parser errors
     */
    @Test
    public void testReservedNameRecognition() throws Exception {
        boolean thrown = false;
        SemanticAnalyzer analyzer = setupSemanFromFile("ReservedWordsTest.btm");
        try {
            analyzer.analyze();
        } catch (RuntimeException e) {
            thrown = true;
            Set<String> errors = new HashSet<>();
            analyzer.getErrorHandler().getErrorList().forEach( error ->
                errors.add(error.getMessage())
            );
            assertTrue(errors.remove("Variable name is a reserved keyword: boolean"));
            assertTrue(errors.remove("Method name is a reserved keyword: int"));
            assertTrue(errors.remove("Variable name is a reserved keyword: void"));
            assertTrue(errors.remove("Class name is a reserved keyword: null"));
            assertTrue(errors.remove("Variable name is a reserved keyword: super"));
            assertTrue(errors.remove("Method name is a reserved keyword: this"));
            assertTrue(errors.isEmpty());
        }
        assertTrue(thrown);
    }

    /**
     * Tests all functions of the MethodSymbolTableVisitor
     * @throws Exception
     */
    @Test
    public void testMethodSymbolTableVisitor() throws Exception {
        boolean thrown = false;
        SemanticAnalyzer analyzer = setupSemanFromFile("MethodVisitorTest.btm");
        try {
            analyzer.analyze();
        } catch (RuntimeException e) {
            thrown = true;
            Set<String> errors = new HashSet<>();
            analyzer.getErrorHandler().getErrorList().forEach( error ->
                    errors.add(error.getMessage())
            );
            assertTrue(errors.remove("Method name is a reserved keyword: this"));
            assertTrue(errors.remove(
                    "Two methods declared with the same name 'sameNameSameClass'")
            );
            assertTrue(errors.isEmpty());
        }
        assertTrue(thrown);
    }

    /**
     * Tests the MainMainVisitor in its ability to locate the Main Class and
     * associated main method through inheritance
     * @throws Exception
     */
    @Test
    public void testMainMainVisitor() throws Exception {
        boolean validThrown = false;
        boolean invalidThrown = false;
        SemanticAnalyzer validAnalyzer = (
                setupSemanFromFile("MainVisitorValidTest.btm")
        );
        SemanticAnalyzer invalidAnalyzer = (
                setupSemanFromFile("MainVisitorInvalidTest.btm")
        );

        //Test the valid Main visitor
        try {
            validAnalyzer.analyze();
        } catch (RuntimeException e) {
            validThrown = true;
            validAnalyzer.getErrorHandler().getErrorList().forEach( error ->
                    System.out.println(error)
            );
        }

        //Test the invalid Main Visitor
        try {
            invalidAnalyzer.analyze();
        } catch (RuntimeException e) {
            invalidThrown = true;
            Set<String> errors = new HashSet<>();
            invalidAnalyzer.getErrorHandler().getErrorList().forEach( error ->
                    errors.add(error.getMessage())
            );
            assertTrue(errors.remove("Missing Main method in a Main Class"));
            assertTrue(errors.isEmpty());
        }

        assertFalse(validThrown);
        assertTrue(invalidThrown);
    }

    /**
     * Tests all functions of the UnaryExprVisitor
     * @throws Exception
     */
    @Test
    public void testUnaryExprVisitor() throws Exception {
        boolean thrown = false;
        SemanticAnalyzer analyzer = setupSemanFromFile("UnaryExprVisitorTest.btm");
        try {
            analyzer.analyze();
        } catch (RuntimeException e) {
            thrown = true;
            Set<String> errors = new HashSet<>();
            analyzer.getErrorHandler().getErrorList().forEach( error ->
                    errors.add(error.getMessage() + " " + error.getLineNum())
            );
            assertTrue(errors.remove("UnaryDecrExpr must have VarExpr as expression 12"));
            assertTrue(errors.remove("UnaryIncrExpr must have VarExpr as expression 10"));
            assertTrue(errors.remove("UnaryIncrExpr must have VarExpr as expression 11"));
        }
        assertTrue(thrown);
    }

    /**
     * Tests all functions of the BreakCheckVisitor
     * @throws Exception
     */
    @Test
    public void testBreakCheckVisitor() throws Exception {
        boolean thrown = false;
        SemanticAnalyzer analyzer = setupSemanFromFile("BreakCheckTest.btm");
        try {
            analyzer.analyze();
        } catch (RuntimeException e) {
            thrown = true;
            Set<String> errors = new HashSet<>();
            analyzer.getErrorHandler().getErrorList().forEach( error ->
                    errors.add(error.getMessage())
            );
            assertTrue(errors.remove("Break statement called outside of loop"));
            assertTrue(errors.isEmpty());
        }
        assertTrue(thrown);
    }

    /**
     * generates a Semantic Analyzer from the input testfile
     * @param filename the btm file
     * @return a Semantic Analyzer Object for the input file
     * @throws Exception parser errors could be thrown
     */
    private SemanticAnalyzer setupSemanFromFile(String filename) throws Exception {
        //Generate the AST from the btm file
        Parser parser = new Parser(
                new Lexer(
                        new StringReader(
                                SemanticTools.generateStringFromTestfile(filename)
                        )
                )
        );
        Program program = (Program) parser.parse().value;
        return new SemanticAnalyzer(program, false);
    }
}