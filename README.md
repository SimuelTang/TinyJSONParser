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
    char ch;
    do {
        if (!charReader.hasMore()) {
            return new Token(null, TokenType.END_DOCUMENT);
        }
        ch = charReader.next();
    } while (isWhiteSpace(ch));
    // some others
}
```

`some others`  处的代码可以很容易猜到，就是为了解析 `token` 而存在的。对于较为平常的几个 `token`， 比如：花括号，中括号，分隔符，我们可以直接将它们作为 `Token` 对象返回。而对于如字符串、布尔值、数字、空值，我们需要进行解析再作为 `Token` 返回。

```java
// some others处的代码
switch (ch) {
    case '{':
        return new Token(TokenType.BEGIN_OBJECT, String.valueOf(ch));
    case '}':
        return new Token(TokenType.END_OBJECT, String.valueOf(ch));
    case '[':
        return new Token(TokenType.BEGIN_ARRAY, String.valueOf(ch));
    case ']':
        return new Token(TokenType.END_ARRAY, String.valueOf(ch));
    case ',':
        return new Token(TokenType.SEP_COMMA, String.valueOf(ch));
    case ':':
        return new Token(TokenType.SEP_COLON, String.valueOf(ch));
    case 'n':
        return readNull();
    case 't':
    case 'f':
        return readBoolean();
    case '"':
        return readString();
    case '-':
        return readNumber(); // 读取到了负数
}
if (Character.isDigit(ch)) {
    return readNumber(); // 读取的是正数
}
```

#### readNull

当 `JSON` 为空时，我们会直接使用 `null`，所以，对 `null` 的解析比较简单，只要保证每个字符符合就行。这里要注意的是，我们只有读取到了第一个 `n` 才会进入这个 `readNull` 方法，所以解析时保证 `ull` 三部分符合就行。

```java
private Token readNull() throws IOException {
    if (!(charReader.next() == 'u' && charReader.next() == 'l' && charReader.next() == 'l')) {
        throw new JSONParseException("Invalid json string");
    }
    return new Token(TokenType.NULL, "null");
}
```

#### readBoolean

进入该方法时，我们已经读取了 `t` 或者 `f` 这两个字符，所以我们需要先判断读取的是哪个字符，再用上面同样的方法对后面的字符进行解析。

```java
private Token readBoolean() throws IOException {
    if (charReader.peek() == 't') {
        if (!(charReader.next() == 'r' && charReader.next() == 'u' && charReader.next() == 'e')) {
            throw new JSONParseException("Invalid json string");
        }
        return new Token(TokenType.BOOLEAN, "true");
    } else {
        if (!(charReader.next() == 'a' && charReader.next() == 'l'
              && charReader.next() == 's' && charReader.next() == 'e')) {
            throw new JSONParseException("Invalid json string");
        }
        return new Token(TokenType.BOOLEAN, "false");
    }
}
```

**小结**

可以看出，解析这两类字符时，我们用到了 `charReader` 的 `peek` 和 `next` 方法，它方便了我们对字符的提取。这就是之前封装了一个工具类 `CharReader` 的原因。

#### readString

对于字符串类型的 `token` ，解析起来比上面两种稍微复杂一些。这里大概列出解析时的逻辑：

读取到了第一个 `"` ，进入该方法

1. 读取到的为 `"` ，直接返回，此时 `Token` 的 `value` 为 `""`。
2. 读取到的为 `\r` 或者 `\n` ，则抛出异常，说明格式不合法。
3. 读取到的为 `\` ，说明后面应该出现转义字符。
   * 如果不是转义字符，则直接抛出异常。
   * 如果符合转义字符，则读取该字符。
     * 判断该字符是否为 `u` ，如果是，则进行`UTF-8`编码字符解析。
4. 读取到的为普通字符，直接读取即可。

```java
private Token readString() throws IOException {
    StringBuilder builder = new StringBuilder();
    while (true) {
        char ch = charReader.next();
        if (ch == '\\') { // 如果读取到待转义的字符
            ch =  charReader.next(); // 获取这个可能符合规则的转义字符
            if (!isEscape(ch)) {
                throw new JSONParseException("Invalid escape character");
            }
            builder.append('\\');
            builder.append(ch);
            if (ch == 'u') { // 表示使用的是 UTF-8 转义
                for (int i = 0; i < 4; i++) {
                    ch = charReader.next();
                    if (!isHex(ch)) {
                        throw new JSONParseException("Invalid hex character");
                    }
                    builder.append(ch);
                }
            }
        } else if (ch == '\"') { // 读取到了下一个 " 表示是字符串的末尾
            return new Token(TokenType.STRING, builder.toString());
        } else if (ch == '\r' || ch == '\n') { // 只存在一个 " 后进行了回车换行，属于不规范的格式
            throw new JSONParseException("Invalid character");
        } else { // 普通字符，直接添加即可
            builder.append(ch);
        }
    }
}
```

#### readNumber

依照 `JSON` 规范，合格的数字格式是以 `-` 开头或者直接以数字开头。所以进入此方法时，也只有这两种可能。