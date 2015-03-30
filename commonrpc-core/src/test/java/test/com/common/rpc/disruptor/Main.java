package test.com.common.rpc.disruptor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 
 * @author liubing
 *
 */
public class Main {
	
	
    @SuppressWarnings("unchecked")
	public static void main(String[] args) {
        //消费者处理事件的线程池
        Executor executor = Executors.newCachedThreadPool();
        //通过事件工厂,RingBuffer大小,消费者线程池创建Disruptor
       // Disruptor<ValueEvent> disruptor = new Disruptor<ValueEvent>(ValueEvent.EVENT_FACTORY, 1, executor);
        Disruptor<ValueEvent> disruptor = new Disruptor(ValueEvent.EVENT_FACTORY, 
                2,executor,
                ProducerType.SINGLE, // Single producer
                new BlockingWaitStrategy()
                );
        //设置事件对象的处理逻辑
        //disruptor.handleEventsWith((event, sequence, endOfBatch) -> System.out.println(event.getValue()));
        disruptor.handleEventsWith(new EventHandler<ValueEvent>() {

			@Override
			public void onEvent(ValueEvent arg0, long arg1, boolean arg2)
					throws Exception {
				// TODO Auto-generated method stub
				System.out.println(arg0.getValue()+"----"+arg1+"-----"+arg2);
				
			}
		});
        
        //启动Disruptor
        RingBuffer<ValueEvent> ringBuffer = disruptor.start();

        //生产者向RingBuffer中添加事件
        for (long i = 0; i < 10; i++) {
            //获取可添加事件的序列号
            long index = ringBuffer.next();
            //System.out.println("index:"+index);
            try {
                //向指定的序列号的事件容器覆盖值
                ringBuffer.get(index).setValue(i+"de");
                
            } finally {
                //发布事件
                ringBuffer.publish(index);
               
            }
        }
        System.out.println(ringBuffer.remainingCapacity());
    }
}
