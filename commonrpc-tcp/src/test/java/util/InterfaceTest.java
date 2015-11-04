package util;

import org.junit.Test;
import test.cross.plateform.rocketrpc.demo.service.impl.DemoServiceImpl;

/**
 * Created by lejing on 15/10/30.
 */
public class InterfaceTest {
	@Test
	public void test(){
		Class cls1 = DemoServiceImpl.class;
		System.out.println(cls1.isInterface());
		Class[] interfaces = cls1.getInterfaces();
		for(Class i : interfaces){
			System.out.println(i + "---" + i.getName());
		}
	}
}
