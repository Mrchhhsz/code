package cn.chh.destroy;

import org.springframework.beans.factory.DisposableBean;

/**
 * @Author: chh
 * @Version: 1.0
 * @Date: 2021/10/16 10:32
 * @Since: 1.0
 * @Package: cn.chh
 */
public class DestroyBean implements DisposableBean {
	@Override
	public void destroy() throws Exception {
		System.out.println("DestroyBean DisposableBean的销毁回调");
	}
}
