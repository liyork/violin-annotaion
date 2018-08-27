package com.wolf.base.apply;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * <p> Description: 测试使用spring的注解和aop方式缓存service层的数据
 * <p/>
 * Date: 2015/12/22
 * Time: 8:56
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
@ContextConfiguration(locations = "classpath:applicationContext-test.xml")
public class CacheTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private BusinessService businessService;

	@Test
	public void testGet(){
		String cityName1 = businessService.getCityName();
		String cityName2 = businessService.getCityName();
		String cityName3 = businessService.getCityName();
	}

	@Test
	public void testDelete(){
		businessService.deleteCityName();
	}

	@Test
	public void testGetMemberById(){
		businessService.getMemberById(1l,"x");
		businessService.getMemberById(1l,"x");
		businessService.getMemberById(2l,"y");
	}
}
