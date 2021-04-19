package pers.simuel.tokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author simuel_tang
 * @Date 2021/3/31
 * @Time 13:38
 */
public class TokenList {
    private final List<Token> tokens;

    {
        tokens = new ArrayList<>();
    }

    private int pos = 0;

    public void add(Token token) {
        tokens.add(token);
    }

    public Token peek() {
        return pos < tokens.size() ? tokens.get(pos) : null;
    }

    /**
     * 该方法仅会在一处被调用：当我们解析对象时，如果读取到了"，则有两种可能，
     * 该字符串是作为key或者作为value存在。如果peekPrevious发现是冒号，那么
     * 这个字符串就是作为value而存在。
     * 所以代码中，我们不担心pos为1的原因是：例如{"name":"saber"}，当我们读取了第一个"后才会有可能调用这个方法
     * 此时，pos的位置已经是2
     * @return
     */
    public Token peekPrevious() {
        return pos - 1 < 0 ? null : tokens.get(pos - 2);
    }

    public Token next() {
        return tokens.get(pos++);
    }

    public boolean hasMore() {
        return pos < tokens.size();
    }

    @Override
    public String toString() {
        return "TokenList{" +
                "tokens=" + tokens +
                "}";
    }
}
