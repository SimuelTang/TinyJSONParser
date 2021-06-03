import java.io.IOException;

import org.junit.Test;

import pers.simuel.parser.JSONParser;

public class JSONParserTest {
    @Test
    public void test() throws IOException {
        JSONParser jsonParser = new JSONParser();
        Object json = jsonParser.fromJSON("{\"foods\":[18, true, {\"calories\":200}]}");
        System.out.println(json);
    }
}
