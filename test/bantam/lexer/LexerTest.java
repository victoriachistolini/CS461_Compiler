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
    public void stringToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("\"cool\" "));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
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