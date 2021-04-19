package pers.simuel.tokenizer;

import pers.simuel.enums.TokenType;

/**
 * @Author simuel_tang
 * @Date 2021/3/30
 * @Time 21:05
 */
public class Token {
    
    private final TokenType tokenType;
    private final String value;

    public Token(TokenType tokenType, String value) {
        this.tokenType = tokenType;
        this.value = value;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Token{" +
                "tokenType=" + tokenType +
                ", value='" + value + '\'' +
                '}';
    }
}
