package bantam.semant;

import bantam.ast.Program;
import bantam.lexer.Lexer;
import bantam.util.SemanticTools;
import org.junit.Test;
import bantam.parser.Parser;
import bantam.util.ErrorHandler;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
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
    public void testEmptyMainClass() throws Exception {
        boolean thrown = false;
        Parser parser = new Parser(new Lexer(new StringReader("class Main {  }")));
        Program program = (Program) parser.parse().value;
        SemanticAnalyzer analyzer = new SemanticAnalyzer(program, false);
        try {
            analyzer.analyze();
        } catch (RuntimeException e) {
            thrown = true;
            assertEquals("Bantam semantic analyzer found errors.", e.getMessage());
            for (ErrorHandler.Error err : analyzer.getErrorHandler().getErrorList()) {
                System.out.println(err);
            }
        }
        assertTrue(thrown);
    }

    @Test
    public void testReservedNameRecognition() throws Exception {
        boolean thrown = false;
        //Generate the AST from the btm file
        Parser parser = new Parser(
            new Lexer(
                new StringReader(
                    SemanticTools.generateStringFromTestfile("reservedWords.btm")
                )
            )
        );
        Program program = (Program) parser.parse().value;
        SemanticAnalyzer analyzer = new SemanticAnalyzer(program, false);
        try {
            analyzer.analyze();
        } catch (RuntimeException e) {
            thrown = true;
            Set<String> errors = new HashSet<>();
            for (ErrorHandler.Error err : analyzer.getErrorHandler().getErrorList()) {
                errors.add(err.getMessage());
                System.out.println(err);
            }
            System.out.println(errors);
            assertTrue(errors.contains("Variable Name is a Reserved Keyword: boolean"));
            assertTrue(errors.contains("Method Name is a Reserved Keyword: int"));
            assertTrue(errors.contains("Variable Name is a Reserved Keyword: void"));
            assertTrue(errors.contains("Class Name is a Reserved Keyword: null"));
            assertTrue(errors.contains("Variable Name is a Reserved Keyword: super"));
            assertTrue(errors.contains("Method Name is a Reserved Keyword: this"));
        }
        assertTrue(thrown);
    }

}