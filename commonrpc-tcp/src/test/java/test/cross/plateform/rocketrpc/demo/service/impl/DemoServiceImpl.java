/**
 * 
 */
package test.cross.plateform.rocketrpc.demo.service.impl;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import test.cross.plateform.rocketrpc.demo.service.IDemoService;

/**
 * @author liubing1
 *
 */
public class DemoServiceImpl implements IDemoService {
	
	private static final Log LOGGER = LogFactory
			.getLog(DemoServiceImpl.class);
	Random r = new Random();
	@Override
	public String sayDemo(String params)  {
		// TODO Auto-generated method stub
		//LOGGER.info("sayDemo params:"+params);
//		long begintime=System.currentTimeMillis();
//		int i1 = r.nextInt();
//		int i2 = Math.abs(i1 % 100);
//		try {
//			Thread.sleep(i2);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		long endtime=System.currentTimeMillis();
//		System.out.println("完成时间:"+(endtime-begintime));
		return "from server:"+params;
	}



	@Override
	public String getParam(String params) {
		// TODO Auto-generated method stub
		//LOGGER.info("getParam params:"+params);
		return "from server:"+params;
	}

}
