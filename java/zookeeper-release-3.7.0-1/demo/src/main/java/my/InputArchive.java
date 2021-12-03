package my;

/**
 * @Description: TODO
 * @Author: chh
 * @Date: 2021/2/21 21:37
 * @Package: my
 */
public interface InputArchive {

    public byte readByte(String tag);

    boolean readBool(String tag);

    int readInt(String tag);

    long readLong(String tag);


}
