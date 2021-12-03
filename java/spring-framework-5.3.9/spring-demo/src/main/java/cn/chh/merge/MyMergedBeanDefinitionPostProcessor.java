package cn.chh.merge;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: chh
 * @Version: 1.0
 * @Date: 2021/10/16 11:12
 * @Since: 1.0
 * @Package: cn.chh.merge
 */
public class MyMergedBeanDefinitionPostProcessor implements MergedBeanDefinitionPostProcessor {

	// bean名字对应的注解方法
	public Map<String, List<Method>> stringMethodMap;

	private Class<? extends Annotation> myAnnotationType;

	public MyMergedBeanDefinitionPostProcessor() {
		myAnnotationType = MyAnnotation.class;
		stringMethodMap = new HashMap<>();
	}

	@Override
	public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
		List<Method> list = new ArrayList<>();
		ReflectionUtils.doWithLocalMethods(beanType, method -> {
			if(this.myAnnotationType != null && method.isAnnotationPresent(this.myAnnotationType)) {
				list.add(method);
				stringMethodMap.put(beanName, list);
			}
		});
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if(stringMethodMap.get(beanName) != null) {
			for(Method method : stringMethodMap.get(beanName)) {
				MyAnnotation annotation = (MyAnnotation) method.getAnnotation(this.myAnnotationType);
				for(int i = 0; i < annotation.count(); i++) {
					try {
						method.invoke(bean, new Object[]{method.getName()});
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return bean;
	}
}
