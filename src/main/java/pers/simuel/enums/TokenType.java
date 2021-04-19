package pers.simuel.enums;

/**
 * @Author simuel_tang
 * @Date 2021/3/30
 * @Time 21:02
 */
public enum TokenType {
    // JSON 对应的数据类型
    BEGIN_OBJECT(1),
    END_OBJECT(2),
    BEGIN_ARRAY(4),
    END_ARRAY(8),
    NULL(16),
    NUMBER(32),
    STRING(64),
    BOOLEAN(128),
    SEP_COLON(256),
    SEP_COMMA(512),
    END_DOCUMENT(1024);

    private final int code;

    TokenType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
