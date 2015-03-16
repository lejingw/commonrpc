/**
 * 
 */
package com.cross.plateform.common.rpc.redis.register.client.impl;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.ShardedJedis;
import com.cross.plateform.common.rpc.redis.register.CommonRpcRegister;
import com.cross.plateform.common.rpc.redis.register.client.ICommonRpcRegisterClient;
/**
 * @author liubing1
 *
 */
public class CommonRpcRegisterClientImpl implements ICommonRpcRegisterClient {
	
	public static final int TYPE = 0;
	
	private static final Log LOGGER = LogFactory.getLog(CommonRpcRegisterClientImpl.class);

	/* (non-Javadoc)
	 * @see com.cross.plateform.common.rpc.redis.register.client.ICommonRpcRegisterClient#registerClient(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean registerClient(final String group, String server) {
		// TODO Auto-generated method stub
		
		ShardedJedis shardJedis = CommonRpcRegister.getInstance().getShardPool().getResource();
		try{
			if(shardJedis.sismember(group+"_client", server)){
				shardJedis.srem(group+"_client", server);
			}
			shardJedis.sadd(group+"_client", server);
			return true;
		 }catch(Exception e){
			LOGGER.error("registerClient fail  group:"+group+"_client"+"，server："+server, e);
			shardJedis.close();
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.cross.plateform.common.rpc.redis.register.client.ICommonRpcRegisterClient#deleteRpcClient(java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteRpcClient(String group, String clientIp) {
		// TODO Auto-generated method stub
		ShardedJedis shardJedis = CommonRpcRegister.getInstance().getShardPool().getResource();
		try{
			 shardJedis.srem(group+"_client", clientIp);
		 }catch(Exception e){
			 LOGGER.error("deleteRpcClient fail  ,group:"+group+"_client，clientIp："+clientIp, e);
			shardJedis.close();
		}
	}

	/* (non-Javadoc)
	 * @see com.cross.plateform.common.rpc.redis.register.client.ICommonRpcRegisterClient#getServersByGroup(java.lang.String)
	 */
	@Override
	public Set<String> getServersByGroup(String group) {
		// TODO Auto-generated method stub
		ShardedJedis shardJedis = CommonRpcRegister.getInstance().getShardPool().getResource();
		
		return shardJedis.smembers(group);
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		ShardedJedis shardJedis = CommonRpcRegister.getInstance().getShardPool().getResource();
		shardJedis.close();
	}

}
