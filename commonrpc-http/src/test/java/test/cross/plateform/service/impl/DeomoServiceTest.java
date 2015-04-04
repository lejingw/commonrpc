/**
 * 
 */
package test.cross.plateform.service.impl;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author liubing1
 *
 */
public class DeomoServiceTest {
	
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("CommonRpcHttpServer.xml");
		int a = 0 % 30 == 0 ? 0 / 30 : 0 / 30 +1;
		System.out.println(a);
	}
}
