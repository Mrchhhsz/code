package cn.chh;

import cn.chh.common.UserDao;
import cn.chh.common.UserDaoImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Author: chh
 * @Version: 1.0
 * @Date: 2021/10/13 15:59
 * @Since: 1.0
 * @Package: cn.chh
 */
public class TestJDKDynamicInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

	@Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		if("userDao".equals(beanName)) {
			UserDao userDaoImpl = new UserDaoImpl();
			UserDao userDao = (UserDao)Proxy.newProxyInstance(this.getClass().getClassLoader(),
					new Class<?>[]{UserDao.class},
					new InvocationHandler() {
						@Override
						public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
							System.out.println("动态代理");
							method.invoke(userDaoImpl);
							return null;
						}
					});
			return userDao;
		}
		return null;
	}
}
