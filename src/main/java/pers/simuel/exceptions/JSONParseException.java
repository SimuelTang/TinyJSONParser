package pers.simuel.exceptions;

/**
 * @Author simuel_tang
 * @Date 2021/3/31
 * @Time 14:03
 */
public class JSONParseException extends RuntimeException {
    public JSONParseException(String message) {
        super(message);
    }
}
