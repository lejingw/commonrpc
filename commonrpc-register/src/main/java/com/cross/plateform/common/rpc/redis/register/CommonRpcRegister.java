/**
 * 
 */
package com.cross.plateform.common.rpc.redis.register;

import java.util.LinkedList;
import java.util.List;
import com.cross.plateform.common.rpc.redis.register.config.CommonRpcRegisterConfig;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

/**
 * @author liubing1
 *
 */
public class CommonRpcRegister {
	
	private static ShardedJedisPool shardPool =null; 
	
	public CommonRpcRegister() {

	}

	private static class SingletonHolder {
		static final CommonRpcRegister instance = new CommonRpcRegister();
	}

	public static CommonRpcRegister getInstance() {
		return SingletonHolder.instance;
	}
	/**
	 * 初始化服务器
	 * @param server
	 */
	public void initServer(String server){
		JedisPoolConfig config=CommonRpcRegisterConfig.getJedisPoolConfig();
		List<JedisShardInfo> list = new LinkedList<JedisShardInfo>(); 
		String[]serverIps=getServers(server);
		for(String serverIp:serverIps){
			String []host=serverIp.split(":");
			JedisShardInfo jedisShardInfo = new JedisShardInfo(host[0], host[1]);
			list.add(jedisShardInfo);
		}
		shardPool = new ShardedJedisPool(config, list); 
	}

	/**
	 * @return the shardPool
	 */
	public  ShardedJedisPool getShardPool() {
		return shardPool;
	}
	
	private String[] getServers(String serverIp){
		String[]serverIps=serverIp.split(";");
		return serverIps;
	}
}
