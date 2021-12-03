package cn.chh.merge;

import org.springframework.stereotype.Component;

/**
 * @Author: chh
 * @Version: 1.0
 * @Date: 2021/10/16 11:08
 * @Since: 1.0
 * @Package: cn.chh.merge
 */
@Component
public class MyMergedBean {

	@MyAnnotation(count = 1)
	public void m1(String msg) {
		print(msg);
	}

	@MyAnnotation(count = 2)
	public void m2(String msg) {
		print(msg);
	}

	@MyAnnotation(count = 3)
	public void m3(String msg) {
		print(msg);
	}

	private void print(String msg) {
		System.out.println(msg);
	}
}
