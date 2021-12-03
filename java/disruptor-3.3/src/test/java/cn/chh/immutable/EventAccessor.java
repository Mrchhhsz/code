package cn.chh.immutable;

/**
 * @Description: TODO
 * @Author: chh
 * @Date: 2021/1/28 8:46
 * @Package: cn.chh.immutable
 */
public interface EventAccessor<T> {

    T take(long sequence);
}
