package bantam.lexer;

import java_cup.runtime.Symbol;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.assertEquals;

/*
 * File: LexerTest.java
 * Author: djskrien
 * Date: 1/8/17
 */
public class LexerTest
{
    @BeforeClass
    public static void begin() {
        System.out.println("begin");
    }
    @Test
    public void classToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("class"));
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
    public void timesToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("* "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("TIMES",s);
    }

    @Test
    public void stringToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("\"cool\" "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        System.out.println(s);
        assertEquals("STRING_CONST",s);
    }
    @Test
    public void EOFToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader(""));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("EOF",s);
    }
}