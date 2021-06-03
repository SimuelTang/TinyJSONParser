# TinyJSONParser

## 使用方式

`pers.simuel.parJSONParser` 类对外提供了一个 `API`： `fromJSON` 。这个接口的参数为字符串类型，返回值为检验且格式化后的 `JSON` 字符串。

```java
public class Main {
    public static void main(String[] args) {
        JSONParser jsonParser = new JSONParser();
        Object json = jsonParser.fromJSON("{}");
    }
}
```

以测试用例来看：

```java
public class JSONParserTest {
    @Test
    public void test() throws IOException {
        JSONParser jsonParser = new JSONParser();
        Object json = jsonParser.fromJSON("{\"foods\":[18, true, {\"calories\":200}]}");
        System.out.println(json);
    }
}
```

输出结果如下：

```json
{
  "foods":
  [
    18,
    true,
    {
      "calories":200
    }
  ] 
}
```

