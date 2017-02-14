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
    public void classToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader(" class"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("CLASS",s);
    }

    /**
     * Check if the for key word is recognized
     */
    @Test
    public void forToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("for "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("FOR",s);
    }

    /**
     * Check if the new key word is recognized
     */
    @Test
    public void newToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("new "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("NEW",s);
    }

    /**
     * Check if the if key word is recognized
     */
    @Test
    public void ifToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("if "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("IF",s);
    }

    /**
     * Check if the extends key word is recognized
     */
    @Test
    public void extendsToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("extends "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("EXTENDS",s);
    }

    /**
     * Check if the return key word is recognized
     */
    @Test
    public void returnToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("return "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("RETURN",s);
    }

    /**
     * Check if the while key word is recognized
     */
    @Test
    public void whileToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("while "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("WHILE",s);
    }


    /**
     * Check if the else key word is recognized
     */
    @Test
    public void elseToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("else "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("ELSE",s);
    }

    /**
     * Check if the class break word is recognized
     */
    @Test
    public void breakToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("break "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("BREAK",s);
    }

    /**
     * Check if the instanceof key word is recognized
     */
    @Test
    public void instanceofToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader(" instanceof "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("INSTANCEOF",s);
    }

    /**
     * Check if the divide symbol is recognized
     */
    @Test
    public void divideToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("/ "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("DIVIDE",s);
    }

    /**
     * Check if the semi colin is recognized
     */
    @Test
    public void semiToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("; "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("SEMI",s);
    }

    /**
     * Check if the left parenthesis is recognized
     */
    @Test
    public void lparenToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("( "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("LPAREN",s);
    }

    /**
     * Check if the right parenthesis is recognized
     */
    @Test
    public void rparenToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader(") "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("RPAREN",s);
    }

    /**
     * Check if the decrement by 1 symbol is recognized
     */
    @Test
    public void decrToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("-- "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("DECR",s);
    }

    /**
     * Check if the increment by 1 symbol is recognized
     */
    @Test
    public void incrToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("++ "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("INCR",s);
    }

    /**
     * Check if the minus symbol is recognized
     */
    @Test
    public void minusToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("- "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("MINUS",s);
    }

    /**
     * Check if the not symbol is recognized
     */
    @Test
    public void notToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("! "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("NOT",s);
    }

    @Test
    /**
     * Check if the and symbol is recognized
     */
    public void andToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("&& "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("AND",s);
    }

    /**
     * Check if the less than symbol is recognized
     */
    @Test
    public void ltToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("< "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("LT",s);
    }

    /**
     * Check if the greater than symbol is recognized
     */
    @Test
    public void gtToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("> "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("GT",s);
    }

    /**
     * Check if the or symbol is recognized
     */
    @Test
    public void orToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("| "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("OR",s);
    }

    /**
     * Check if the greater than or equal to symbol is recognized
     */
    @Test
    public void geqToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader(">= "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("GEQ",s);
    }

    /**
     * Check if the less than or equal to symbol is recognized
     */
    @Test
    public void leqToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("<= "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("LEQ",s);
    }

    /**
     * Check if the comma symbol is recognized
     */
    @Test
    public void commaToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader(", "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("COMMA",s);
    }

    /**
     * Check if the plus sign symbol is recognized
     */
    @Test
    public void plusToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("+ "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("PLUS",s);
    }

    /**
     * Check if the assignment symbol is recognized
     */
    @Test
    public void assignToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("= "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("ASSIGN",s);
    }

    /**
     * Check if the left square bracket symbol is recognized
     */
    @Test
    public void lsqbraceToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("[ "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("LSQBRACE",s);
    }

    /**
     * Check if the right square bracket symbol is recognized
     */
    @Test
    public void rsqbraceToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("] "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("RSQBRACE",s);
    }

    /**
     * Check if an identifier is recognized
     * Thus we determine if the two incorrect identifiers are correctly not
     * recognized: 1 - begin with digit; 2 - begin with underscore
     */
    @Test
    public void idToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("abc "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("ID",s);

        //check for id starting with integer
        Lexer lexer2 = new Lexer(new StringReader("9hjbhj "));
        Symbol token2 = lexer2.next_token();
        String s2 = ((Token)token2.value).getName();
        assertNotEquals("ID",s2);

        //check for id starting with _
        Lexer lexer3 = new Lexer(new StringReader("_hjbhj "));
        Symbol token3 = lexer3.next_token();
        String s3 = ((Token)token3.value).getName();
        assertNotEquals("ID",s3);

    }

    /**
     * Check if the dot symbol is recognized
     */
    @Test
    public void dotToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader(". "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("DOT",s);
    }

    /**
     * Check if and integer is correctly recognized
     */
    @Test
    public void intToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("67578 "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("INT_CONST",s);
    }

    /**
     * Check if the integers larger than the range are correctly recognized
     */
    @Test
    public void largeIntToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("2147483647"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("INT_CONST",s);

        Lexer lexer2 = new Lexer(new StringReader("2147483648"));
        Symbol token2 = lexer2.next_token();
        String s2 = ((Token)token2.value).getName();
        assertEquals("LARGE_INT",s2);
    }

    /**
     * Check if integer is negative
     */
    @Test
    public void negativeIntToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("-99"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("NEGATIVE_INT",s);
    }

    /**
     * Check if strings are correctly identified.
     */
    @Test
    public void stringToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("\"this is \\\"a string\""));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("STRING_CONST",s);
    }

    /**
     * Check if multiline strings are correctly identified
     */
    @Test
    public void stringErrorToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("\"this is a \n string\""));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("MULTILINE_STRING",s);
    }

    /**
     * Check if String larger than 5000 characters are idenitified as long
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
        Lexer lexer = new Lexer(new StringReader(str1));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("STRING_CONST",s);

        Lexer lexer2 = new Lexer(new StringReader(str2));
        Symbol token2 = lexer2.next_token();
        String s2 = ((Token)token2.value).getName();
        assertEquals("LARGE_STRING",s2);
    }

    /**
     * Check that comments are properly ignored
     */
    @Test
    public void commentToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("/* this is a comment \n /* */"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("EOF",s);
    }

    /**
     * Check if empty of file indicator token works correctly
     */
    @Test
    public void EOFToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader(""));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("EOF",s);
    }



    /**
     * Check the equals operation works
     */
    @Test
    public void eqToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("=="));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("EQ",s);
    }

    /**
     * Check if unterminated comments, strings and multi line strings are
     * correctly handled
     */
    @Test
    public void lexErrorToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("/*   rxdrh"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("UNTERMINATED_COMMENT",s);

        Lexer lexer2 = new Lexer(new StringReader("\"reg more code"));
        Symbol token2 = lexer2.next_token();
        String s2 = ((Token)token2.value).getName();
        assertEquals("UNTERMINATED_STRING",s2);

        Lexer lexer3 = new Lexer(new StringReader("\"hvbjb\nj \n jhb\nj \""));
        Symbol token3 = lexer3.next_token();
        String s3 = ((Token)token3.value).getName();
        assertEquals("MULTILINE_STRING",s3);
    }

    /**
     * Check if the left brace symbol is recognized
     */
    @Test
    public void lbraceToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("{"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("LBRACE",s);
    }

    /**
     * Check if the right brace symbol is recognized
     */
    @Test
    public void rbraceToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("}"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("RBRACE",s);
    }

    /**
     * Check if the modulus symbol is recognized
     */
    @Test
    public void modulusToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("% "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("MODULUS",s);
    }

    /**
     * Check if the not equals to symbol is recognized
     */
    @Test
    public void neToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("!= "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("NE",s);
    }

    /**
     * Check if true and false key words are recognized as booleans
     */
    @Test
    public void booleanConstantToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("true "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("BOOLEAN_CONST",s);

        Lexer lexer2 = new Lexer(new StringReader("false "));
        Symbol token2 = lexer2.next_token();
        String s2 = ((Token)token2.value).getName();
        assertEquals("BOOLEAN_CONST",s2);
    }

    /**
     * Check if the an illegal escape is recognized
     */
    @Test
    public void illegalEscapeToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("\"\\n \""));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("STRING_CONST",s);

        Lexer lexer2 = new Lexer(new StringReader("\"\\a \""));
        Symbol token2 = lexer2.next_token();
        String s2 = ((Token)token2.value).getName();
        assertEquals("ILLEGAL_ESCAPE_CHAR",s2);
    }

    /**
     * Check if an illegal character is recognized
     */
    @Test
    public void illegalToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("\\ "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("ILLEGAL_CHAR",s);

        Lexer lexer2 = new Lexer(new StringReader("??@%@@`~ "));
        Symbol token2 = lexer2.next_token();
        String s2 = ((Token)token2.value).getName();
        assertEquals("ILLEGAL_CHAR",s2);
    }
}