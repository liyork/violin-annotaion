package com.wolf.base.apply;

import com.wolf.annotation.CacheResult;
import com.wolf.annotation.WipeCache;
import org.springframework.stereotype.Service;

/**
 * <p> Description: 使用spring的注解和aop方式缓存service层的数据
 * <p/>
 * Date: 2015/12/22
 * Time: 8:56
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
@Service(value="businessService")
public class BusinessService {

	//基本操作
	@CacheResult(domain ="city")
	public String getCityName() {
		System.out.println("get data from db");
		return "天津";
	}

	//基本操作
	@WipeCache(domain ="city")
	public void deleteCityName() {
		System.out.println("delete data from db");
	}

	//使用id作为缓存key
	@CacheResult(domain ="city",key="#id")
	public Object getMemberById(Long id, String name){
		return new Object();
	}

}
