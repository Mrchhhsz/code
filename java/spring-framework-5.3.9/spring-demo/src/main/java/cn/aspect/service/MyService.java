package cn.aspect.service;

/**
 * @Author: chh
 * @Version: 1.0
 * @Date: 2021/10/17 10:08
 * @Since: 1.0
 * @Package: cn.chh.aspect
 */
public class MyService implements IService {
	@Override
	public void show() {
		System.out.println("MyService show");
	}
}
