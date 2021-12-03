package cn.test;

import org.springframework.stereotype.Service;

/**
 * @Author: chh
 * @Version: 1.0
 * @Date: 2021/8/31 15:53
 * @Since: 1.0
 * @Package: cn.test
 */
@Service
public class Role {

	private String id;

	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
