package cn.chh;

import org.springframework.asm.Type;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cglib.core.ClassGenerator;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.DefaultGeneratorStrategy;
import org.springframework.cglib.transform.ClassEmitterTransformer;
import org.springframework.cglib.transform.TransformingClassGenerator;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @Author: chh
 * @Version: 1.0
 * @Date: 2021/8/31 18:21
 * @Since: 1.0
 * @Package: cn.chh
 */
public class MyBeanFactoryAwareGeneratorStrategy extends DefaultGeneratorStrategy {

	private final ClassLoader classLoader;

	public MyBeanFactoryAwareGeneratorStrategy(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	protected ClassGenerator transform(ClassGenerator cg) throws Exception {
		ClassEmitterTransformer transformer = new ClassEmitterTransformer() {
			@Override
			public void end_class() {
				declare_field(Constants.ACC_PUBLIC, "自定义的Bean工厂", Type.getType(BeanFactory.class),null);
				super.end_class();
			}
		};
		return new TransformingClassGenerator(cg, transformer);
	}

	@Override
	public byte[] generate(ClassGenerator cg) throws Exception {
		if(this.classLoader == null) {
			return super.generate(cg);
		}

		Thread thread = Thread.currentThread();
		ClassLoader threadClassLoader;
		try {
			threadClassLoader = thread.getContextClassLoader();
		} catch (Throwable e) {
			return super.generate(cg);
		}

		boolean overrideClassLoader = !this.classLoader.equals(threadClassLoader);
		if(overrideClassLoader) {
			thread.setContextClassLoader(this.classLoader);
		}

		try {
			byte[] generate = super.generate(cg);
			FileOutputStream fileOutputStream = null;
			fileOutputStream = new FileOutputStream(new File("D:/TestConfigProxy.class"));
			fileOutputStream.write(generate);
			fileOutputStream.flush();
			fileOutputStream.close();
			return generate;
		} finally {
			thread.setContextClassLoader(threadClassLoader);
		}
	}
}
