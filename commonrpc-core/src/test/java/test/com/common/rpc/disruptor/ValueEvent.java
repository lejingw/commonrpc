package test.com.common.rpc.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * 
 * @author liubing
 *
 */
public final class ValueEvent {
    /** 事件所附属的值 */
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /** 事件工厂;创建事件填充RingBuffer */
    public static final EventFactory<ValueEvent> EVENT_FACTORY = new EventFactory<ValueEvent>() {

		@Override
		public ValueEvent newInstance() {
			// TODO Auto-generated method stub
			return new ValueEvent();
		}
	};
}
