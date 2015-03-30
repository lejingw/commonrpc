/**
 * 
 */
package test.cross.plateform.rocketrpc.demo.service.impl;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import test.cross.plateform.rocketrpc.demo.service.IDemoService;

/**
 * @author liubing1
 *
 */
public class DeomoClientTest {
	
	public static void main(String[] args) {
		
		ClassPathXmlApplicationContext context =new ClassPathXmlApplicationContext("CommonRpcClient.xml");
		long time1=System.currentTimeMillis();
		
		IDemoService demoService=(IDemoService) context.getBean("demoServiceClient");
		for(int j=0;j<5;j++){
			for(int i=0;i<1000000;i++){
				demoService.sayDemo("okok");
				
			}
			long end1=System.currentTimeMillis();
			System.out.println("完成时间1:"+(end1-time1));
		}
		
	}
}
