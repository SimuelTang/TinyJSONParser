package pers.simuel.tokenizer;

import java.io.*;

/**
 * @Author simuel_tang
 * @Date 2021/3/30
 * @Time 22:37
 */
public class CharReader {

    // 缓存
    private final char[] buffer;
    // 输入流
    private final Reader reader;
    // 当前读取位置
    private int pos;
    // 数据大小
    private int size;

    public CharReader(Reader reader) {
        // 单次最大读取数据大小
        int BUFFER_SIZE = 1024;
        this.buffer = new char[BUFFER_SIZE];
        this.reader = reader;
        try {   
            resize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取最后一个字符
     *
     * @return pos位置的前一个字符
     */
    public char peek() {
        if (pos <= size) {
            return buffer[Math.max(0, pos - 1)];
        }
        return (char) (-1);
    }

    /**
     * 判断是否有数据可以读取，第一次调用时进行初始化操作
     *
     * @return 是否有剩余的数据
     */
    public boolean hasMore() {
        return pos < size;
    }

    private void resize() throws IOException {
        int n = reader.read(buffer);
        pos = 0;
        size = n;
    }

    /**
     * 返回读取到的字符
     *
     * @return 当前字符
     */
    public char next() {
        if (hasMore()) {
            return buffer[pos++];
        }
        return (char) -1;
    }

    /**
     * 退格，回到上一个读取的位置处
     */
    public void back() {
        pos = Math.max(0, --pos);
    }
}
