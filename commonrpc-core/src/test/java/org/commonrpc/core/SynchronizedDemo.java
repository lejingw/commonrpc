/**
 * 
 */
package org.commonrpc.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cross.plateform.common.rpc.core.all.message.CommonRpcResponse;

/**
 * @author liubing
 *
 */
public class SynchronizedDemo {
	
	protected static Map<Integer, CommonRpcResponse> responses = 
			new ConcurrentHashMap<Integer, CommonRpcResponse>();
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		SynchronizedDemo synchronizedDemo=new SynchronizedDemo();
		Object s="1";
		synchronizedDemo.waitTime(s,5);
		synchronizedDemo.notify(s);
	}
	
	private void waitTime(Object obj,int timeout){
		 synchronized (obj) {
             try {
            	 obj.wait(timeout);
             } catch (Exception e) {
            	 e.printStackTrace();
             }
             System.out.println("线程"+Thread.currentThread().getName()+"获取到了锁");
         }
	}
	
	private void notify(Object obj) throws Exception{
		 Thread.sleep(6000L);
		 synchronized (obj) {
			
			 obj.notify();
             System.out.println("线程"+Thread.currentThread().getName()+"调用了object.notify()");
         }
         System.out.println("线程"+Thread.currentThread().getName()+"释放了锁");
	}
}
