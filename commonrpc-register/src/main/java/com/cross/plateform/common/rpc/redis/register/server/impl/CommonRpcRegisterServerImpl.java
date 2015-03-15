/**
 * 
 */
package com.cross.plateform.common.rpc.redis.register.server.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.ShardedJedis;
import com.cross.plateform.common.rpc.redis.register.CommonRpcRegister;
import com.cross.plateform.common.rpc.redis.register.server.ICommonRpcRegisterServer;
/**
 * @author liubing1
 *
 */
public class CommonRpcRegisterServerImpl implements ICommonRpcRegisterServer {
	
	public static final int TYPE = 0;
	
	private static final Log LOGGER = LogFactory.getLog(CommonRpcRegisterServerImpl.class);
	
	
	/* (non-Javadoc)
	 * @see com.cross.plateform.common.rpc.redis.register.ICommonRpcRegister#registerService(java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public boolean registerService(String group, String server) {
		ShardedJedis shardJedis = CommonRpcRegister.getInstance().getShardPool().getResource();
		try{
			if(shardJedis.sismember(group, server)){
				shardJedis.srem(group, server);
			}
			shardJedis.sadd(group, server);
			return true;
		 }catch(Exception e){
			LOGGER.error("registerService fail  group:"+group+"，server："+server, e);
			shardJedis.close();
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see com.cross.plateform.common.rpc.redis.register.server.ICommonRpcRegisterServer#deleteRpcServiceServer(java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteRpcServiceServer(String group, String serverIp) {
		// TODO Auto-generated method stub
		ShardedJedis shardJedis = CommonRpcRegister.getInstance().getShardPool().getResource();
		try{
			 shardJedis.srem(group, serverIp);
		 }catch(Exception e){
			 LOGGER.error("deleteRpcClient fail  ,group:"+group+"，clientIp："+serverIp, e);
			shardJedis.close();
		}
	}
	


	@Override
	public void close() {
		// TODO Auto-generated method stub
		ShardedJedis shardJedis = CommonRpcRegister.getInstance().getShardPool().getResource();
		shardJedis.close();
	}

	
}
