# TinyJSONParser

> 这个 `readme` 用来记录自己写这个轻量级 `JSON` 解析器时的步骤，因为有些地方比较tricky，平常写CRUD项目是感受不到的，所以以此来记录。 

### 词法分析

对于 `JSON` 格式的字符串序列，我们可以把它解析成一定的符号，如：

```json
{
    "name" : "saber"
}
```

可以被解析成以下五种符号：

`{`	 `name`	 `:`	 `saber`	 `}`

这里，我们不用考虑是否要将 `"` 解析进来，因为之后我们会将符号 `name` 和 `saber` 标记成 `string` 类型。同样依靠这种标记方式，我们可以将 `{` 和 `}` 等也进行标记。

**NOTE：**后续，这些符号会被称为 `token` 。

### 语义分析

在解析过程中，如果出现了非预期的 `token` ，则应该停止解析，向外抛出异常或者错误。比如：

```json
{
    "name", "saber"
}
```

在我们读取完了名字为 `name` 的 `token` 后，我们希望下一个能读取到 `:` ，而实际却是 `,` ，此时，应该有足够的检查方式，提醒我们输入的 `JSON` 字符串不合法。

**JSON的数据类型**

主要有以下标准：

- BEGIN_OBJECT（{）
- END_OBJECT（}）
- BEGIN_ARRAY（[）
- END_ARRAY（]）
- NULL（null）
- NUMBER（数字）
- STRING（字符串）
- BOOLEAN（true/false）
- SEP_COLON（:）
- SEP_COMMA（,）

以刚才的例子来说，当我们解析完了 `name` 后，我们知道它是一个 `STRIGN` 类型的 `token` ，所以，我们会期待下一个 `token` 类型为 `SEP_COLON` 但是却得到了 `SEP_COMMA` ，此时，就可以进行错误处理了。

### Token

到这里，我们大致可以知道，我们需要一个类用来表示 `token` ，这个类既能表示出 `name` 、`saber` 等字段，又能对它们进行标识。所以，可以设计如下类结构：

```java
public class Token {
    private final String value;
    private final TokenType tokenType;
	...
}
```

这里的 `value` 就是我们 `token` 的具体值，`TokenType`就是上方列出的标准，用于表示我们 `value` 的 `token` 类型。

### 读取字符串

正常读取字符串时，我们可能会使用 `charAt` 或者直接通过 `toCharArray` 转成字符数组。这里我们仍然采用了这种思路，但是我们提供了工具类来对这种方式进行封装，以便提供更多的功能。

#### CharReader

这个由我们封装的工具类底层使用的就是 `char[]` ，当我们的 `JSON` 字符串传递进来的时候，就直接转化成 `char[] `。除此之外，它还提供了 `peek ` `next` `hasMore` 等方法来帮助我们处理字符串，具体怎么帮忙的可看后续内容。

```java
public class CharReader {
    // 缓存
    private final char[] buffer;
    // 当前读取位置
    private int pos;
    // 数据大小
    private int size;
    ...
}
```

### Tokenizer

这个类用于将我们的 `JSON` 字符则正式转化为 `tokens` 序列。`tokens` 序列即为由一个个 `token` ，如：`Token{value='name', tokenType=STRING} `够成的序列集合。

转化时的核心逻辑：

```java
void tokenize() {
    ...
    do {
        token = getToken();
        tokens.add(token);
	} while (token != null && token.getTokenType() != TokenType.END_DOCUMENT);
}
```

```java
Token getToken() {
    do {
        if (!charReader.hasMore()) {
            return new Token(null, TokenType.END_DOCUMENT);
        }
        ch = charReader.next();
    } while (isWhiteSpace(ch));
    
}
```

