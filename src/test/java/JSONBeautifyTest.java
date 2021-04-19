import org.junit.jupiter.api.Test;
import pers.simuel.entity.JSONObject;
import pers.simuel.parser.JSONParser;

/**
 * @Author simuel_tang
 * @Date 2021/4/4
 * @Time 15:01
 */
public class JSONBeautifyTest {
    
    @Test
    public void beautifyJSONTest01() throws Exception {
        String json = "{\"foods\":[{\"name\":\"fish\"}]}";
        JSONParser parser = new JSONParser();
        Object ret = parser.fromJson(json);
        System.out.println(ret);
    }
    
    @Test
    public void beautifyJSONTest02() throws Exception {
        String json = "{\"name\": \"狄仁杰\", \"type\": \"射手\", \"ability\":[\"六令追凶\",\"逃脱\",\"王朝密令\"],\"history\":{\"DOB\":630,\"DOD\":700,\"position\":\"宰相\",\"dynasty\":\"唐朝\"}}";
        System.out.println("原 JSON 字符串：");
        System.out.println(json);
        System.out.println("\n");
        System.out.println("美化后的 JSON 字符串：");
        JSONParser jsonParser = new JSONParser();
        JSONObject drj = (JSONObject) jsonParser.fromJson(json);
        System.out.println(drj);
    }
}
