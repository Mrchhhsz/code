package cn.chh;

import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.proxy.CallbackFilter;
import org.springframework.cglib.proxy.Enhancer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author: chh
 * @Version: 1.0
 * @Date: 2021/8/31 17:59
 * @Since: 1.0
 * @Package: cn.chh
 */
public class TestCglib {

	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {

		Class<?> configurationClassEnhancerClass = Class.forName("org.springframework.context.annotation.ConfigurationClassEnhancer");
		Constructor<?> constructor = configurationClassEnhancerClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		Object configurationClassEnhancer = constructor.newInstance();

		Field callbackFilterField = configurationClassEnhancerClass.getDeclaredField("CALLBACK_FILTER");
		callbackFilterField.setAccessible(true);
		CallbackFilter callbackFilter = (CallbackFilter) callbackFilterField.get(configurationClassEnhancer);

		Class<?> conditionalCallbackFilter =
				Class.forName("org.springframework.context.annotation.ConfigurationClassEnhancer$ConditionalCallback");
		Method getCallbackTypesMethod = conditionalCallbackFilter.getDeclaredMethod("getCallbackTypes");
		getCallbackTypesMethod.setAccessible(true);

		Class<?> enhancerConfiguration =
				Class.forName("org.springframework.context.annotation" +
						".ConfigurationClassEnhancer$EnhancedConfiguration");

		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(TestConfig.class);
		enhancer.setInterfaces(new Class<?>[]{enhancerConfiguration});
		enhancer.setUseFactory(false);
		enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);

		enhancer.setCallbackFilter(callbackFilter);
		enhancer.setCallbackTypes((Class<?>[]) getCallbackTypesMethod.invoke(callbackFilter));

		Class<?> proxy = enhancer.createClass();
		System.out.println(proxy);
	}
}
