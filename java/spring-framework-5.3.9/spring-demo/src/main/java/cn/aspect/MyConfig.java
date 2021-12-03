package cn.aspect;

import cn.aspect.service.IService;
import cn.aspect.service.MyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @Author: chh
 * @Version: 1.0
 * @Date: 2021/10/17 9:58
 * @Since: 1.0
 * @Package: cn.chh.aspect
 */
@Configuration
@EnableAspectJAutoProxy
public class MyConfig {

	@Bean
	public IService getService() {
		return new MyService();
	}

	@Bean
	public MyLogAspects getMyLogAspects() {
		return new MyLogAspects();
	}
}
