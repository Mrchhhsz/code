package cn.chh.factoryBeanTest;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @Author: chh
 * @Version: 1.0
 * @Date: 2021/9/1 14:32
 * @Since: 1.0
 * @Package: cn.chh.factoryBeanTest
 */
public class Main {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		applicationContext.register(TestFactoryBean.class);
		applicationContext.refresh();
		Object obj = applicationContext.getBean("testFactoryBean");
		System.out.println(obj);

		Object obj1 = applicationContext.getBean("&testFactoryBean");
		System.out.println(obj1);
	}
}
