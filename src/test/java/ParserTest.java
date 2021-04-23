import org.junit.jupiter.api.Test;
import pers.simuel.entity.JSONObject;
import pers.simuel.parser.Parser;
import pers.simuel.tokenizer.CharReader;
import pers.simuel.tokenizer.TokenList;
import pers.simuel.tokenizer.Tokenizer;

import java.io.IOException;
import java.io.StringReader;

/**
 * @Author simuel_tang
 * @Date 2021/4/3
 * @Time 11:47
 */
public class ParserTest {
    @Test
    public void parseStringTest() throws IOException {
        Parser parser = new Parser();
        Tokenizer tokenizer = new Tokenizer();
        TokenList tokens = tokenizer.tokenize(new CharReader
                (new StringReader("{\"name\":\"saber\",\"gender\":\"female\"}")));
        JSONObject jsonObject = (JSONObject) parser.parse(tokens);
        System.out.println(jsonObject);
    }

    @Test
    public void parseNumberTest() throws IOException {
        Parser parser = new Parser();
        Tokenizer tokenizer = new Tokenizer();
        TokenList tokens = tokenizer.tokenize(new CharReader
                (new StringReader("{\"age\":0018}")));
        JSONObject jsonObject = (JSONObject) parser.parse(tokens);
        System.out.println(jsonObject);
    }

    @Test
    public void parseArrayTest() throws IOException {
        Parser parser = new Parser();
        Tokenizer tokenizer = new Tokenizer();
        TokenList tokens = tokenizer.tokenize(new CharReader
                (new StringReader("{\"foods\":[18, true, {\"calories\":200}]}")));
        JSONObject jsonObject = (JSONObject) parser.parse(tokens);
        System.out.println(jsonObject);
    }
}
