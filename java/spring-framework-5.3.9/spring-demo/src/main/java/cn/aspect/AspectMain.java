package cn.aspect;

import cn.aspect.service.IService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Author: chh
 * @Version: 1.0
 * @Date: 2021/10/17 21:41
 * @Since: 1.0
 * @Package: cn.chh.aspect
 */
public class AspectMain {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		applicationContext.register(MyConfig.class);
		applicationContext.refresh();
		IService bean = applicationContext.getBean(IService.class);
		bean.show();
	}
}
