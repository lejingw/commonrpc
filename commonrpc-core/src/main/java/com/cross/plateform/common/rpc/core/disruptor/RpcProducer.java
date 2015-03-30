/**
 * 
 */
package com.cross.plateform.common.rpc.core.disruptor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.TimeoutBlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
/**
 * @author liubing
 *
 */
public class RpcProducer {
	
	private static int nThreads=Runtime.getRuntime().availableProcessors()*2;//根据cpu核数，决定个数
	
	private static Disruptor<RpcValueEvent> disruptor=null;
	
	private static RingBuffer<RpcValueEvent> ringBuffer=null;
	public RpcProducer() {

	}

	private static class SingletonHolder {
		static final RpcProducer instance = new RpcProducer();
	}

	public static RpcProducer getInstance() {
		return SingletonHolder.instance;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Disruptor<RpcValueEvent> getDisruptor(long timeout){
		if(disruptor==null){
			ThreadPoolExecutor threadPoolExecutor=new ThreadPoolExecutor(nThreads, nThreads,
	                0L, TimeUnit.MILLISECONDS,
	                new LinkedBlockingQueue<Runnable>(),
	                new ThreadPoolExecutor.DiscardPolicy());//超过最大线程数，直接丢弃，可以让启动起来
			
			TimeoutBlockingWaitStrategy waitStrategy = new TimeoutBlockingWaitStrategy(timeout, TimeUnit.MILLISECONDS);
			disruptor = new Disruptor(RpcValueEvent.EVENT_FACTORY, 
		                2,threadPoolExecutor,
		                ProducerType.SINGLE, // Single producer
		                waitStrategy
		                );
			
		}
		
		return disruptor;
	}
	
	
	private RingBuffer<RpcValueEvent> getRingBuffer(long timeout,EventHandler<RpcValueEvent> eventHandler){
		if(ringBuffer==null){
			disruptor =getDisruptor(timeout);
			disruptor.handleEventsWith(eventHandler);
			ringBuffer=disruptor.start();
		}
		return ringBuffer;
	}
	
	public void publish(long timeout,EventHandler<RpcValueEvent> eventHandler,Object obj){
		 RingBuffer<RpcValueEvent> ringBuffer=getRingBuffer(timeout, eventHandler);
		 
		 long index = ringBuffer.next();
		 ringBuffer.get(index).setValue(obj);
		 ringBuffer.publish(index);
	}
	
	public void close(){
		if(disruptor!=null){
			disruptor.shutdown();
		}
	}
}
