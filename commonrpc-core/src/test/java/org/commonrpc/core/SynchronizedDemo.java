package org.commonrpc.core;

public class SynchronizedDemo {
	public static void main(String[] args) throws Exception {
		SynchronizedDemo synchronizedDemo = new SynchronizedDemo();
		Object s = "1";
		synchronizedDemo.waitTime(s, 5);
		synchronizedDemo.notify(s);
	}

	private void waitTime(Object obj, int timeout) {
		synchronized (obj) {
			try {
				obj.wait(timeout);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("线程" + Thread.currentThread().getName() + "获取到了锁");
		}
	}

	private void notify(Object obj) throws Exception {
		Thread.sleep(6000L);
		synchronized (obj) {
			obj.notify();
			System.out.println("线程" + Thread.currentThread().getName() + "调用了object.notify()");
		}
		System.out.println("线程" + Thread.currentThread().getName() + "释放了锁");
	}
}
