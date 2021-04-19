package pers.simuel.parser;

import pers.simuel.entity.JSONArray;
import pers.simuel.entity.JSONObject;
import pers.simuel.enums.TokenType;
import pers.simuel.exceptions.JSONParseException;
import pers.simuel.tokenizer.Token;
import pers.simuel.tokenizer.TokenList;

/**
 * @Author simuel_tang
 * @Date 2021/4/3
 * @Time 9:26
 */
public class Parser {

    private static final int BEGIN_OBJECT_TOKEN = 1;
    private static final int END_OBJECT_TOKEN = 2;
    private static final int BEGIN_ARRAY_TOKEN = 4;
    private static final int END_ARRAY_TOKEN = 8;
    private static final int NULL_TOKEN = 16;
    private static final int NUMBER_TOKEN = 32;
    private static final int STRING_TOKEN = 64;
    private static final int BOOLEAN_TOKEN = 128;
    private static final int SEP_COLON_TOKEN = 256;
    private static final int SEP_COMMA_TOKEN = 512;

    private TokenList tokens;

    public Object parse(TokenList tokens) {
        this.tokens = tokens;
        return parse();
    }

    private Object parse() {
        Token token = tokens.next();
        if (token == null) {
            return new JSONObject();
        } else if (token.getTokenType() == TokenType.BEGIN_OBJECT) {
            return parseJsonObject();
        } else if (token.getTokenType() == TokenType.BEGIN_ARRAY) {
            return parseJsonArray();
        } else {
            throw new JSONParseException("Parse error, invalid Token, neither object nor array");
        }
    }

    private JSONArray parseJsonArray() {
        // 待返回的数组对象
        JSONArray jsonArray = new JSONArray();
        // 以[开头，可能存在的token
        int expectedToken = BEGIN_ARRAY_TOKEN | END_ARRAY_TOKEN | BEGIN_OBJECT_TOKEN | NULL_TOKEN
                | NUMBER_TOKEN | BOOLEAN_TOKEN | STRING_TOKEN;
        while (tokens.hasMore()) {
            Token token = tokens.next();
            TokenType tokenType = token.getTokenType();
            String tokenValue = token.getValue();
            switch (tokenType) {
                case BEGIN_OBJECT:
                    checkExpectedToken(expectedToken, tokenType);
                    jsonArray.add(parseJsonObject());
                    expectedToken = SEP_COMMA_TOKEN | END_ARRAY_TOKEN;
                    break;
                case BEGIN_ARRAY:
                    checkExpectedToken(expectedToken, tokenType);
                    jsonArray.add(parseJsonArray());
                    expectedToken = SEP_COMMA_TOKEN | END_ARRAY_TOKEN;
                    break;
                case END_ARRAY:
                case END_DOCUMENT:
                    checkExpectedToken(expectedToken, tokenType);
                    return jsonArray;
                case NULL:
                    checkExpectedToken(expectedToken, tokenType);
                    jsonArray.add(null);
                    expectedToken = SEP_COMMA_TOKEN | END_ARRAY_TOKEN;
                    break;
                case BOOLEAN:
                    checkExpectedToken(expectedToken, tokenType);
                    jsonArray.add(Boolean.valueOf(tokenValue));
                    expectedToken = SEP_COMMA_TOKEN | END_ARRAY_TOKEN;
                    break;
                case STRING:
                    checkExpectedToken(expectedToken, tokenType);
                    jsonArray.add(tokenValue);
                    expectedToken = SEP_COMMA_TOKEN | END_ARRAY_TOKEN;
                    break;
                case NUMBER:
                    checkExpectedToken(expectedToken, tokenType);
                    if (tokenValue.contains(".") || tokenValue.contains("e") || tokenValue.contains("E")) {
                        jsonArray.add(Double.valueOf(tokenValue));
                    } else {
                        long num = Long.parseLong(tokenValue);
                        if (num > Integer.MAX_VALUE || num < Integer.MIN_VALUE) {
                            jsonArray.add(num);
                        } else {
                            jsonArray.add((int) num);
                        }
                    }
                    expectedToken = SEP_COMMA_TOKEN | END_ARRAY_TOKEN;
                    break;
                case SEP_COMMA:
                    checkExpectedToken(expectedToken, tokenType);
                    expectedToken = BEGIN_ARRAY_TOKEN | BEGIN_OBJECT_TOKEN | NULL_TOKEN
                            | NUMBER_TOKEN | BOOLEAN_TOKEN | STRING_TOKEN;
                    break;
                default:
                    throw new JSONParseException("Unexpected Token.");
            }
        }
        throw new JSONParseException("Parse error, invalid Token.");
    }

    /**
     * 进入该方法时，我们已经读取到了{，所以可以直接设置expectedToken，一定为字符串或者 }
     *
     * @return
     */
    private Object parseJsonObject() {
        // 待返回的类型
        JSONObject jsonObject = new JSONObject();
        // 设置期待的token type
        int expectedToken = STRING_TOKEN | END_OBJECT_TOKEN;
        // 设置保存时的键值对
        String key = null;
        Object value;
        // 循环查找
        while (tokens.hasMore()) {
            // 获取实际的token
            Token token = tokens.next();
            // 获取对应的token type，以便找到对应的解析方式
            TokenType tokenType = token.getTokenType();
            String tokenValue = token.getValue();
            switch (tokenType) {
                case BEGIN_OBJECT:
                    // 如果是对象类型，则递归进行解析，同时，可以确定的是：
                    // 1.一定已经有了一个key，否则已经抛出异常了
                    // 2.递归解析完成后，期待的token为逗号或者右花括号}
                    checkExpectedToken(expectedToken, tokenType);
                    jsonObject.put(key, parseJsonObject());
                    expectedToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN;
                    break;
                case END_OBJECT:
                case END_DOCUMENT:
                    checkExpectedToken(expectedToken, tokenType);
                    return jsonObject;
                case BEGIN_ARRAY:
                    checkExpectedToken(expectedToken, tokenType);
                    jsonObject.put(key, parseJsonArray());
                    expectedToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN;
                    break;
                case STRING:
                    checkExpectedToken(expectedToken, tokenType);
                    // 因为读取到的String可能是key或者value，所以要提取它的前一个字符进行特判
                    Token preToken = tokens.peekPrevious();
                    if (preToken.getTokenType() == TokenType.SEP_COLON) {
                        // 如果前一个token是冒号，说明这个是作为value存在的，此时，键值对已经全部读取完成
                        // 直接放入jsonObject即可
                        value = token.getValue();
                        jsonObject.put(key, value);
                        expectedToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN;
                    } else {
                        // 读取到的字符串是作为key而存在的，所以我们要继续解析，同时可以断定，
                        // 下一个字符应该是冒号
                        key = token.getValue();
                        expectedToken = SEP_COLON_TOKEN;
                    }
                    break;
                case NUMBER:
                    checkExpectedToken(expectedToken, tokenType);
                    if (tokenValue.contains(".") || tokenValue.contains("e") || tokenValue.contains("E")) {
                        jsonObject.put(key, Double.valueOf(tokenValue));
                    } else {
                        long num = Long.parseLong(tokenValue);
                        if (num > Integer.MAX_VALUE || num < Integer.MIN_VALUE) {
                            jsonObject.put(key, num);
                        } else {
                            jsonObject.put(key, (int) num);
                        }
                    }
                    expectedToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN;
                    break;
                case BOOLEAN:
                    checkExpectedToken(expectedToken, tokenType);
                    jsonObject.put(key, Boolean.valueOf(tokenValue));
                    expectedToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN;
                    break;
                case SEP_COLON:
                    checkExpectedToken(expectedToken, tokenType);
                    expectedToken = NULL_TOKEN | NUMBER_TOKEN | BOOLEAN_TOKEN | STRING_TOKEN
                            | BEGIN_OBJECT_TOKEN | BEGIN_ARRAY_TOKEN;
                    break;
                case SEP_COMMA:
                    checkExpectedToken(expectedToken, tokenType);
                    // 读取到逗号后，后续的第一个token一定得是作为key而存在的，所以充值 expectedToken 为字符即可
                    expectedToken = STRING_TOKEN;
                    break;
                case NULL:
                    checkExpectedToken(expectedToken, tokenType);
                    jsonObject.put(key, null);
                    expectedToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN;
                    break;
                default:
                    throw new JSONParseException("unexpected token");
            }
        }
        throw new JSONParseException("Parse error, invalid Token.");
    }

    private void checkExpectedToken(int expectedToken, TokenType tokenType) {
        if ((expectedToken & tokenType.getCode()) == 0) {
            throw new JSONParseException("Parse error, invalid Token.");
        }
    }
}
