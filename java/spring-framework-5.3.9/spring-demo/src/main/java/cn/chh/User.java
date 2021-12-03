package cn.chh;

import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Service;

/**
 * @Author: chh
 * @Version: 1.0
 * @Date: 2021/8/24 16:50
 * @Since: 1.0
 * @Package: cn.chh
 */
public class User {

	public User(StandardEnvironment environment) {
		this.environment = environment;
		System.out.println("创建User实例");
	}

	private String username;

	private StandardEnvironment environment;

	public String getUsername(){
		return this.username;
	}

	public void setUsername(String username){
		this.username = username;
	}
}
