package pers.simuel.parser;

import pers.simuel.tokenizer.CharReader;
import pers.simuel.tokenizer.TokenList;
import pers.simuel.tokenizer.Tokenizer;

import java.io.IOException;
import java.io.StringReader;

/**
 * @Author simuel_tang
 * @Date 2021/4/3
 * @Time 16:12
 */
public class JSONParser {
    private final Tokenizer tokenizer;
    private final Parser parser;
    
    {
        tokenizer = new Tokenizer();
        parser = new Parser();
    }

    public Object fromJSON(String json) throws IOException {
        CharReader charReader = new CharReader(new StringReader(json));
        TokenList tokens = tokenizer.tokenize(charReader);
        return parser.parse(tokens);
    }
}
