import org.junit.jupiter.api.Test;
import pers.simuel.tokenizer.CharReader;
import pers.simuel.tokenizer.TokenList;
import pers.simuel.tokenizer.Tokenizer;

import java.io.IOException;
import java.io.StringReader;

/**
 * @Author simuel_tang
 * @Date 2021/3/31
 * @Time 14:39
 */
public class TokenizerTest {

    @Test
    public void readObjectTest() {
        Tokenizer tokenizer = new Tokenizer();
        CharReader reader = new CharReader(new StringReader("{}"));
        TokenList tokens = null;
        try {
            tokens = tokenizer.tokenize(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(tokens);
    }

    @Test
    public void readStringTest() {
        Tokenizer tokenizer = new Tokenizer();
        CharReader reader = new CharReader(new StringReader("{\"name\":\"saber\", \"gender\":\"female\"}"));
        TokenList tokens = null;
        try {
            tokens = tokenizer.tokenize(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(tokens);
    }

    @Test
    public void readBooleanTest() {
        Tokenizer tokenizer = new Tokenizer();
        CharReader reader = new CharReader(new StringReader("{\"flag\":true}"));
        TokenList tokens = null;
        try {
            tokens = tokenizer.tokenize(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(tokens);
    }

    @Test
    public void readPosAndNegNumberTest() {
        Tokenizer tokenizer = new Tokenizer();
        CharReader reader = new CharReader(new StringReader("{\"number\":-20}"));
        TokenList tokens = null;
        try {
            tokens = tokenizer.tokenize(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(tokens);
    }

    @Test
    public void readFracAndExpTest() {
        Tokenizer tokenizer = new Tokenizer();
        CharReader reader = new CharReader(new StringReader("{\"number\":-20.21e+10}"));
        TokenList tokens = null;
        try {
            tokens = tokenizer.tokenize(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(tokens);
    }

    @Test
    public void readArrayTest() {
        Tokenizer tokenizer = new Tokenizer();
        CharReader reader = new CharReader(new StringReader("[[1,2,3,\"\u4e2d\"]]"));
        TokenList tokens = null;
        try {
            tokens = tokenizer.tokenize(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(tokens);
    }
}
