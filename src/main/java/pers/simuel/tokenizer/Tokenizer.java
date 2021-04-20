package pers.simuel.tokenizer;

import pers.simuel.enums.TokenType;
import pers.simuel.exceptions.JSONParseException;

import java.io.IOException;

/**
 * @Author simuel_tang
 * @Date 2021/3/31
 * @Time 13:48
 */
public class Tokenizer {
    
    private CharReader charReader;
    private TokenList tokens;

    /**
     * 根据传入的字符流进行解析
     *
     * @param charReader
     * @return
     */
    public TokenList tokenize(CharReader charReader) throws IOException {
        this.charReader = charReader;
        tokenize();
        return tokens;
    }

    /**
     * 进行了初始化操作和整体的解析过程
     */
    private void tokenize() throws IOException {
        tokens = new TokenList();
        Token token;
        do {
            token = getToken();
            tokens.add(token);
        } while (token != null && token.getTokenType() != TokenType.END_DOCUMENT);

        // TODO:
//        if (token == null) {
//            
//        }
    }

    /**
     * 具体的解析过程
     *
     * @return
     */
    private Token getToken() throws IOException {
        char ch;
        // 找到第一个非空格符进行解析
        do {
            if (!charReader.hasMore()) {
                // 当我们输入的字符串已经读取到了文件末尾，就返回该信息
                return new Token(TokenType.END_DOCUMENT, null);
            }
            ch = charReader.next();
        } while (isWhiteSpace(ch));
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
        throw new JSONParseException("Invalid character");
    }


    /**
     * 当读取到了负号或者某一个数字时会进入该方法，表示即将处理数字信息
     *
     * @return
     * @throws IOException
     */
    private Token readNumber() throws IOException {
        // 先判断是因为哪个条件而进入此方法的
        char ch = charReader.peek();
        StringBuilder builder = new StringBuilder();
        if (ch == '-') { // 因为负号进入了此方法
            builder.append('-');
            // 添加了负号后，获取下一个字符
            ch = charReader.next();
        }
        if (ch == '0') { // 如果这次字符为0，说明可能有小数要处理
            builder.append(ch);
            builder.append(readFracAndExp());
        } else if (Character.isDigit(ch)) { // 如果负号后面是数字，就正常处理
            ReadRemainNumber(builder);
        } else { // 负号后面出现了非法符号
            throw new JSONParseException("Invalid minus number");
        }
        return new Token(TokenType.NUMBER, builder.toString());
    }

    private void ReadRemainNumber(StringBuilder sb) throws IOException {
        // 先获取进入该方法前获取到的第一个数字
        char ch = charReader.peek();
        do {
            // 添加完这个数字后继续往后处理
            sb.append(ch);
            ch = charReader.next();
        } while (isDigit(ch));
        // 退出循环的愿意有两个：1.读取到的是小数点，它不是数字导致的退出 2.已经读取完毕
        if (ch != (char) -1) { // 因小数点而退出的话，我们要另外处理小数后面的数字
            charReader.back();
            sb.append(readFracAndExp());
        }
    }

    /**
     * 进入该方法的可能原因：
     * 1. 遇到了小数点
     * 2. 遇到了科学记数法
     * 3. 数字本身就是0
     *
     * @return
     */
    private String readFracAndExp() throws IOException {
        StringBuilder sb = new StringBuilder();
        char ch = charReader.next();
        if (ch == '.') { // 处理小数
            sb.append(ch);
            sb.append(readFrac());
        } else if (isExp(ch)) { // 处理科学记数法
            sb.append(ch);
            sb.append(readExp());
        } else {
            // 回退一步，防止对其他数据的读取造成影响
            charReader.back();
        }
        return sb.toString();
    }
    
    /**
     * 进入这个方法时，e/E 已经被添加了，所以直接处理数字即可
     *
     * @return
     */
    private String readExp() throws IOException {
        StringBuilder sb = new StringBuilder();
        char ch = charReader.next();
        if (ch == '+' || ch == '-') {
            sb.append(ch);
            ch = charReader.next();
            if (isDigit(ch)) {
                do {
                    sb.append(ch);
                    ch = charReader.next();
                } while (isDigit(ch));
                if (ch != (char) -1) { // 说明可能是因为读取到了其他数据的开始部分
                    charReader.back();
                }
            } else {
                throw new JSONParseException("Invalid exp");
            }
        } else {
            throw new JSONParseException("Invalid exp");
        }
        return sb.toString();
    }

    /**
     * 确点了要读取的是小数后，我们就进入这个方法
     *
     * @return
     * @throws IOException
     */
    private String readFrac() throws IOException {
        // 获取小数点后的第一个字符
        char ch = charReader.next();
        if (!isDigit(ch)) {
            // 如果小数点后的第一个字符不是数字，直接抛出异常
            throw new JSONParseException("Invalid fraction!");
        }
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(ch);
            ch = charReader.next();
        } while (isDigit(ch));
        // 退出循环有以下可能：1.已经读取到了小数的末尾 2.遇到了科学记数法e 3.读取到了下一个数据
        if (isExp(ch)) {
            sb.append(ch);
            sb.append(readExp());
        } else {
            if (ch != (char) -1) {
                // 回退一步，防止对下个数据的读取造成影响
                charReader.back();
            }
        }
        return sb.toString();
    }

    /**
     * 当我们读取了第一个 " 后，进入该方法，处理字符串的剩余字符
     *
     * @return
     */
    private Token readString() throws IOException {
        StringBuilder builder = new StringBuilder();
        while (true) {
            char ch = charReader.next();
            if (ch == '\\') { // 如果读取到待转义的字符
                //获取这个可能的转义字符
                ch =  charReader.next();
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

    private Token readBoolean() throws IOException {
        // 先使用 peek() 而不是像 readString() 中使用 next() 的原因是：
        // 当我们读取到 t 或者 f 的时候才会进入这个方法，所以，我们要先判断是哪个布尔值
        // 之后，通过连续的 next() 判断是否是我们要的布尔值
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

    private Token readNull() throws IOException {
        if (!(charReader.next() == 'u' && charReader.next() == 'l' && charReader.next() == 'l')) {
            throw new JSONParseException("Invalid json string");
        }
        return new Token(TokenType.NULL, "null");
    }

    private boolean isExp(char ch) {
        return ch == 'E' || ch == 'e';
    }

    private boolean isHex(char ch) {
        return ((ch >= '0' && ch <= '9') || ('a' <= ch && ch <= 'f')
                || ('A' <= ch && ch <= 'F'));
    }

    private boolean isDigit(char ch) {
        return ch <= '9' && ch >= '0';
    }

    /**
     * 判断是否符合转义符
     *
     * @return
     * @throws IOException
     */
    private boolean isEscape(char ch) throws IOException {
        return (ch == '"' || ch == '\\' || ch == 'u' || ch == 'r'
                || ch == 'n' || ch == 'b' || ch == 't' || ch == 'f');
    }

    /**
     * 判断是否为空格符
     *
     * @param ch
     * @return
     */
    private boolean isWhiteSpace(char ch) {
        return (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r');
    }

}
