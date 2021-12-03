package cn.chh.destroy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.PriorityOrdered;

/**
 * @Author: chh
 * @Version: 1.0
 * @Date: 2021/10/16 10:36
 * @Since: 1.0
 * @Package: cn.chh.destroy
 */
public class TestBeanDefinitionRegistryPostProcessorPriorityOrdered implements BeanDefinitionRegistryPostProcessor,
		PriorityOrdered {
	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		AnnotatedBeanDefinition genericBeanDefinition = new AnnotatedGenericBeanDefinition(DestroyBean2.class);
		genericBeanDefinition.setDestroyMethodName("myCustomDes");
		registry.registerBeanDefinition("TestBeanDefinitionRegistryPostProcessor", genericBeanDefinition);
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}

	@Override
	public int getOrder() {
		return 0;
	}
}
