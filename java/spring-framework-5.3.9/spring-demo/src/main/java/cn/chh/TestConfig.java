package cn.chh;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.Property;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;

/**
 * @Author: chh
 * @Version: 1.0
 * @Date: 2021/8/24 17:20
 * @Since: 1.0
 * @Package: cn.chh
 */
@Configuration
public class TestConfig {

	@Value("${os.name}")
	private String osName;

	@Autowired
	private StandardEnvironment environment;

	@Bean
	public User user() {
		System.out.println(osName);
		User user = new User(environment);
		return user;
	}

	@Bean
	public TestUser testUser() {
		TestUser testUser = new TestUser();
		testUser.setUser(user());
		return testUser;
	}
}
