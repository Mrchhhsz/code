package cn.chh;

import cn.chh.common.UserDao;
import cn.chh.common.UserDaoImpl2;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * @Author: chh
 * @Version: 1.0
 * @Date: 2021/10/13 16:43
 * @Since: 1.0
 * @Package: cn.chh
 */
public class InstantiationSupplierBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		registry.registerBeanDefinition("userDao", new RootBeanDefinition(UserDao.class, () -> {
			System.out.println("UserDao自定义提供器");
			return new UserDaoImpl2();
		}));
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}
}
