/**
 * File: LexerTest.java
 * @author djskrien
 * @author Victoria Chistolini
 * @author Edward (osan) Zhou
 * @author Alex Rinker
 * @author Vivek Sah
 * Class: CS461
 * Project: 1
 * Date: Feb 11, 2017
 */

package bantam.lexer;

import java_cup.lexer;
import java_cup.runtime.Symbol;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


/**
 * This class tests all of the Lexer token identifications
 */
public class LexerTest
{
    @BeforeClass
    public static void begin() {
        System.out.println("begin");
    }
    /**
     * Check if the class key word is recognized
     */
    @Test
    public void keywordTokens() throws Exception {
        checkToken(" class ", "CLASS");
        checkToken(" for ", "FOR");
        checkToken(" new ", "NEW");
        checkToken(" if ", "IF");
        checkToken(" extends ", "EXTENDS");
        checkToken(" return ", "RETURN");
        checkToken(" while ", "WHILE");
        checkToken(" else ", "ELSE");
        checkToken(" break ", "BREAK");
        checkToken(" instanceof ", "INSTANCEOF");
    }

    /**
     * Check if the divide symbol is recognized
     */
    @Test
    public void arithmeticTokens() throws Exception {
        checkToken(" / ", "DIVIDE");
        checkToken(" - ", "MINUS");
        checkToken(" + ", "PLUS");
        checkToken(" % ", "MODULUS");
        checkToken(" * ", "TIMES");
        checkToken(" -- ", "DECR");
        checkToken(" ++ ", "INCR");
    }

    /**
     * Check if braces and parentheses are recognized
     */
    @Test
    public void braceTokens() throws Exception {
        checkToken(" [ ", "LSQBRACE");
        checkToken(" ] ", "RSQBRACE");
        checkToken("{", "LBRACE");
        checkToken("}", "RBRACE");
        checkToken(" ( ", "LPAREN");
        checkToken(" ) ", "RPAREN");
    }

    /**
     * Check logical operators
     */
    @Test
    public void logicalTokens() throws Exception {
        checkToken(" ! ", "NOT");
        checkToken(" && ", "AND");
        checkToken(" || ", "OR");
    }

    /**
     * Check relational operators
     */
    @Test
    public void relationalTokens() throws Exception {
        checkToken(" < ", "LT");
        checkToken(" <= ", "LEQ");
        checkToken(" > ", "GT");
        checkToken(" >= ", "GEQ");
        checkToken("==","EQ");
    }

    /**
     * Check if the comma symbol is recognized
     */
    @Test
    public void commaToken() throws Exception {
        checkToken(" , ", "COMMA");
    }

    /**
     * Check if the semi colin is recognized
     */
    @Test
    public void semiToken() throws Exception {
        checkToken(" ; ", "SEMI");
    }

    /**
     * Check if the assignment symbol is recognized
     */
    @Test
    public void assignToken() throws Exception {
        checkToken(" = ", "ASSIGN");
    }

    /**
     * Check if an identifier is recognized
     * Thus we determine if the two incorrect identifiers are correctly not
     * recognized: 1 - begin with digit; 2 - begin with underscore
     */
    @Test
    public void idToken() throws Exception {
        checkToken(" abc ", "ID");
        checkToken(" 9abc ", "ILLEGAL_ID");
        checkToken(" _abc ", "ILLEGAL_ID");
    }

    /**
     * Check if the dot symbol is recognized
     */
    @Test
    public void dotToken() throws Exception {
        checkToken(" . ", "DOT");
    }

    /**
     * Check if integers are correctly recognized
     */
    @Test
    public void largeIntToken() throws Exception {
        checkToken(" 2147483647 ", "INT_CONST");
        checkToken(" 2147483648 ", "LARGE_INT");
    }

    /**
     * Check if strings are correctly identified.
     */
    @Test
    public void longStringToken() throws Exception {
        String str1 = "\"";
        String str2 = "\"";
        for(int i=0; i<4998; i++) { //4998 due to the two '\' chars which are counted
            str1+= "A";
            str2+= "B";
        }
        str1+="\"";
        str2+="B\"";
        checkToken(str1, "STRING_CONST");
        checkToken(str2, "LARGE_STRING");
    }

    /**
     * Check that comments are properly ignored
     */
    @Test
    public void commentToken() throws Exception {
        checkToken("/* this is a comment \n /* */", "EOF");
    }

    /**
     * Check if empty of file indicator token works correctly
     */
    @Test
    public void EOFToken() throws Exception {
        checkToken("","EOF");
    }

    /**
     * Check if unterminated comments, strings and multi line strings are
     * correctly handled
     */
    @Test
    public void lexErrorToken() throws Exception {
        checkToken("/*  abc", "UNTERMINATED_COMMENT");
        checkToken("\" abc", "UNTERMINATED_STRING");
        checkToken("\"abc\nabc\"", "MULTILINE_STRING");
    }

    /**
     * Check if the not equals to symbol is recognized
     */
    @Test
    public void neToken() throws Exception {
        checkToken(" != ", "NE");
    }

    /**
     * Check if true and false key words are recognized as booleans
     */
    @Test
    public void booleanConstantToken() throws Exception {
        checkToken(" true ", "BOOLEAN_CONST");
        checkToken(" false ", "BOOLEAN_CONST");
    }

    /**
     * Check if the an illegal escape is recognized
     */
    @Test
    public void illegalEscapeToken() throws Exception {
        checkToken("\"\\n \"", "STRING_CONST");
        checkToken("\"\\a \"", "ILLEGAL_ESCAPE_CHAR");
    }

    /**
     * Check if an illegal character is recognized
     */
    @Test
    public void illegalToken() throws Exception {
        checkToken("\\ ", "ILLEGAL_CHAR");
    }

    /**
     * Checks equality of String with the expected token from the Lexer
     */
    private void checkToken(String str, String expectedToken) throws Exception{
        Lexer lexer = new Lexer(new StringReader(str));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals(expectedToken,s);
    }
}