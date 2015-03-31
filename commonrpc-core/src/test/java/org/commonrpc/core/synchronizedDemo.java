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
public class synchronizedDemo {
	
	protected static Map<Integer, CommonRpcResponse> responses = 
			new ConcurrentHashMap<Integer, CommonRpcResponse>();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}
	
	private void waitTime(Object obj,int timeout){
		 synchronized (obj) {
             try {
            	 obj.wait(timeout);
             } catch (InterruptedException e) {
             }
             System.out.println("线程"+Thread.currentThread().getName()+"获取到了锁");
         }
	}
	
	private void notify(Object obj){
		 synchronized (obj) {
			 obj.notify();
             System.out.println("线程"+Thread.currentThread().getName()+"调用了object.notify()");
         }
         System.out.println("线程"+Thread.currentThread().getName()+"释放了锁");
	}
}
