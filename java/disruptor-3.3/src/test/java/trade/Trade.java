package trade;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description: TODO
 * @Author: chh
 * @Date: 2021/1/29 8:40
 * @Package: trade
 */
public class Trade {

    private long id;
    private String name;
    private double price;
    private AtomicInteger count = new AtomicInteger(0);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public AtomicInteger getCount() {
        return count;
    }

    public void setCount(AtomicInteger count) {
        this.count = count;
    }
}
