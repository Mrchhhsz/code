package cn.chh;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Author: chh
 * @Version: 1.0
 * @Date: 2021/8/31 17:39
 * @Since: 1.0
 * @Package: cn.chh
 */
public class TestPostProcessor {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		applicationContext.register(TestConfig.class);
		applicationContext.addBeanFactoryPostProcessor(new TestBeanFactoryPostProcessor());
		applicationContext.refresh();
	}
}
