package cn.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @Author: chh
 * @Version: 1.0
 * @Date: 2021/10/16 11:52
 * @Since: 1.0
 * @Package: cn.chh.aspect
 */
@Aspect
@Component
public class MyLogAspects {

	/**
	 * 修饰符 返回类型（一定要有）包名.类名.方法名（一定要有）参数
	 */
	@Pointcut("execution(* cn.aspect.service.*.*(..))")
	public void pointCut() {

	}

	/**
	 * 在目标方法执行后
	 */
	@Before("pointCut()")
	public void logBefore() {
		System.out.println("logBefore");
	}
}
