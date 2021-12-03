package cn.chh.factoryBeanTest;

import cn.chh.common.UserDao;
import cn.chh.common.UserDaoImpl;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 * @Author: chh
 * @Version: 1.0
 * @Date: 2021/9/1 14:30
 * @Since: 1.0
 * @Package: cn.chh.factoryBeanTest
 */
@Component
public class TestFactoryBean implements FactoryBean<UserDao> {
	@Override
	public UserDao getObject() throws Exception {
		return new UserDaoImpl();
	}

	@Override
	public Class<?> getObjectType() {
		return UserDao.class;
	}
}
