package cn.chh;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;

/**
 * 该类继承{@link InstantiationAwareBeanPostProcessor}接口，
 * beanFactory.addBeanPostProcessor(该对象实例)
 * 可以在bean实例化之前对bean进行修改
 * 实际中使用这个扩展点对bean进行动态代理
 * @Author: chh
 * @Version: 1.0
 * @Date: 2021/10/13 15:54
 * @Since: 1.0
 * @Package: cn.chh
 */
public class TestInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

	@Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		return null;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return null;
	}
}
