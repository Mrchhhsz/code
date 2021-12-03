package cn.chh.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: chh
 * @Version: 1.0
 * @Date: 2021/9/1 15:24
 * @Since: 1.0
 * @Package: cn.chh.configTest
 */
@Configuration
public class MyConfig {

	@Bean
	public UserDao userDao() {
		return new UserDaoImpl();
	}
}
