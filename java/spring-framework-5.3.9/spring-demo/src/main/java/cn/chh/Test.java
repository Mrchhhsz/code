package cn.chh;

import cn.chh.common.UserDaoImpl2;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Author: chh
 * @Version: 1.0
 * @Date: 2021/10/25 16:03
 * @Since: 1.0
 * @Package: cn.chh
 */
public class Test {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext("cn.chh");
		ctx.registerBean("test", UserDaoImpl2.class, () -> {
			return new UserDaoImpl2();
		});
		UserDaoImpl2 user = (UserDaoImpl2)ctx.getBean("test");

		System.out.println(user);

		TestUser testUser = (TestUser) ctx.getBean("testUser");
		System.out.println(testUser.getUser());
	}
}
