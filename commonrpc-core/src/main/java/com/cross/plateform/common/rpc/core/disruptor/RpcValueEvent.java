/**
 * 
 */
package com.cross.plateform.common.rpc.core.disruptor;

import com.lmax.disruptor.EventFactory;
/**
 * @author liubing
 *
 */
public final class RpcValueEvent {
	
	private Object value;
	
	private Object ctx;
	
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	 /**
	 * @return the ctx
	 */
	public Object getCtx() {
		return ctx;
	}

	/**
	 * @param ctx the ctx to set
	 */
	public void setCtx(Object ctx) {
		this.ctx = ctx;
	}

	/** 事件工厂;创建事件填充RingBuffer */
    public static final EventFactory<RpcValueEvent> EVENT_FACTORY = new EventFactory<RpcValueEvent>() {

		@Override
		public RpcValueEvent newInstance() {
			// TODO Auto-generated method stub
			return new RpcValueEvent();
		}
	};
}
