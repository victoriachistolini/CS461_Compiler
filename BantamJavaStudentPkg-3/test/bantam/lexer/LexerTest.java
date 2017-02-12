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
    @Test
    public void classToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader(" class"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("CLASS",s);
    }

    @Test
    public void forToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("for "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("FOR",s);
    }

    @Test
    public void newToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("new "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("NEW",s);
    }

    @Test
    public void ifToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("if "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("IF",s);
    }

    @Test
    public void extendsToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("extends "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("EXTENDS",s);
    }

    @Test
    public void returnToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("return "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("RETURN",s);
    }

    @Test
    public void whileToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("while "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("WHILE",s);
    }

    @Test
    public void elseToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("else "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("ELSE",s);
    }

    @Test
    public void breakToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("break "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("BREAK",s);
    }

    @Test
    public void instanceofToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("instanceof "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("INSTANCEOF",s);
    }

    @Test
    public void divideToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("/ "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("DIVIDE",s);
    }

    @Test
    public void semiToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("; "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("SEMI",s);
    }

    @Test
    public void lparenToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("( "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("LPAREN",s);
    }

    @Test
    public void rparenToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader(") "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("RPAREN",s);
    }

    @Test
    public void decrToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("-- "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("DECR",s);
    }

    @Test
    public void incrToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("++ "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("INCR",s);
    }

    @Test
    public void minusToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("- "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("MINUS",s);
    }

    @Test
    public void notToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("! "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("NOT",s);
    }

    @Test
    public void andToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("&& "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("AND",s);
    }

    @Test
    public void ltToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("< "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("LT",s);
    }

    @Test
    public void gtToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("> "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("GT",s);
    }

    @Test
    public void orToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("| "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("OR",s);
    }

    @Test
    public void geqToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader(">= "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("GEQ",s);
    }

    @Test
    public void leqToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("<= "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("LEQ",s);
    }

    @Test
    public void commaToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader(", "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("COMMA",s);
    }

    @Test
    public void plusToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("+ "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("PLUS",s);
    }

    @Test
    public void assignToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("= "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("ASSIGN",s);
    }

    @Test
    public void lsqbraceToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("[ "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("LSQBRACE",s);
    }

    @Test
    public void rsqbraceToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("] "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("RSQBRACE",s);
    }

    @Test
    public void idToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("abc "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("ID",s);

        //check for id starting with integer
        Lexer lexer2 = new Lexer(new StringReader("9hjbhj "));
        Symbol token2 = lexer.next_token();
        String s2 = ((Token)token2.value).getName();
        assertNotEquals("ID",s2);

        //check for id starting with _
        Lexer lexer3 = new Lexer(new StringReader("_hjbhj "));
        Symbol token3 = lexer.next_token();
        String s3 = ((Token)token3.value).getName();
        assertNotEquals("ID",s3);

        //other tests
    }

    @Test
    public void dotToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader(". "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("DOT",s);
    }

    @Test
    public void intToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("67578 "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("INT_CONST",s);
    }

    @Test
    public void stringToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("\"this is a string\""));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("STRING_CONST",s);
    }

    @Test
    public void stringErrorToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("\"this is a \n string\""));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("MULTI_LINE_STRING",s);

    }

    @Test
    public void commentToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("/* this is a comment \n /* */"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("EOF",s);
    }

    @Test
    public void EOFToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader(""));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("EOF",s);
    }


    @Test
    public void eqToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("=="));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("EQ",s);
    }


    @Test
    public void lexErrorToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("/*   rxdrh"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("LEX_ERROR",s);


        Lexer lexer2 = new Lexer(new StringReader("\"hvbjbj"));
        Symbol token2 = lexer.next_token();
        String s2 = ((Token)token.value).getName();
        assertEquals("LEX_ERROR",s2);

        Lexer lexer3 = new Lexer(new StringReader("\"hvbjbj \n jhbj \""));
        Symbol token3 = lexer.next_token();
        String s3 = ((Token)token.value).getName();
        assertEquals("LEX_ERROR",s3);
    }

    @Test
    public void lbraceToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("{"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("LBRACE",s);
    }

    @Test
    public void rbraceToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("}"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("RBRACE",s);
    }

    @Test
    public void modulusToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("% "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("MODULUS",s);
    }

    @Test
    public void neToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("!= "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("NE",s);
    }

    @Test
    public void booleanConstantToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("true "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("BOOLEAN_CONST",s);

        Lexer lexer2 = new Lexer(new StringReader("false "));
        Symbol token2 = lexer.next_token();
        String s2 = ((Token)token.value).getName();
        assertEquals("BOOLEAN_CONST",s2);
    }
}