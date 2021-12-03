package cn.chh;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Author: chh
 * @Version: 1.0
 * @Date: 2021/8/24 16:46
 * @Since: 1.0
 * @Package: cn.chh
 */
public class App {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(TestConfig.class);
		//ctx.getBeanFactory().addBeanPostProcessor(new TestJDKDynamicInstantiationAwareBeanPostProcessor());
		ctx.addBeanFactoryPostProcessor(new InstantiationSupplierBeanDefinitionRegistryPostProcessor());
		User user = (User)ctx.getBean("user");

		System.out.println(user.getUsername());

		TestUser testUser = (TestUser) ctx.getBean("testUser");
		System.out.println(testUser.getUser());

		String str = "";
		// str.getBytes("UTF-8");

		// Role role = (Role)ctx.getBean("role");
		// System.out.println(role);
	}


}
