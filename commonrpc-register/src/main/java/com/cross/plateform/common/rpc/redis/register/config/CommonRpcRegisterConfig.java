/**
 * 
 */
package com.cross.plateform.common.rpc.redis.register.config;

import redis.clients.jedis.JedisPoolConfig;

/**
 * @author liubing1
 *
 */
public class CommonRpcRegisterConfig {
	
	public static final String redispoolmaxActive ="redis.pool.maxActive";
	
    public static int commonRpcMaxActive=   Integer.parseInt(System.getProperty(redispoolmaxActive, "10"));
    
    public static final String redispoolmaxIdle ="redis.pool.maxIdle";
    
    public static int commonRpcMaxIdle=   Integer.parseInt(System.getProperty(redispoolmaxIdle, "10"));
    
    public static final String redispoolminIdle ="redis.pool.minIdle";
    
    public static int commonRpcMinIdle=   Integer.parseInt(System.getProperty(redispoolminIdle, "3"));

    public static final String redispoolMaxWait ="redis.pool.maxWait";
    
    public static int commonRpcMaxWait=   Integer.parseInt(System.getProperty(redispoolMaxWait, "200"));

    public static final String redispoolNumTestsPerEvictionRun="redis.pool.NumTestsPerEvictionRun";
    
    public static int commonRpcNumTestsPerEvictionRun=   Integer.parseInt(System.getProperty(redispoolNumTestsPerEvictionRun, "10"));
    
    public static final String redispoolMinEvictableIdleTimeMillis="redis.pool.MinEvictableIdleTimeMillis";
    
    public static int commonRpcMinEvictableIdleTimeMillis=   Integer.parseInt(System.getProperty(redispoolMinEvictableIdleTimeMillis, "200"));

    public static final String redispoolTimeBetweenEvictionRunsMillis="redis.pool.TimeBetweenEvictionRunsMillis";
    
    public static int commonRpcTimeBetweenEvictionRunsMillis=   Integer.parseInt(System.getProperty(redispoolTimeBetweenEvictionRunsMillis, "200"));
    
    public static final String redispoolTestOnBorrow="redis.pool.TestOnBorrow";
    
    public static boolean commonRpcTestOnBorrow=Boolean.parseBoolean(System.getProperty(redispoolTestOnBorrow, "false"));
    
    public static final String redispoolTestWhileIdle="redis.pool.TestWhileIdle";
    
    public static boolean commonRpcTestWhileIdle=Boolean.parseBoolean(System.getProperty(redispoolTestWhileIdle, "false"));
    
    
    public static JedisPoolConfig getJedisPoolConfig(){
    	
    	JedisPoolConfig config = new JedisPoolConfig();   
		config.setBlockWhenExhausted(false);
		config.setTestWhileIdle(CommonRpcRegisterConfig.commonRpcTestWhileIdle);
		config.setTestOnBorrow(CommonRpcRegisterConfig.commonRpcTestOnBorrow);
		config.setMaxIdle(CommonRpcRegisterConfig.commonRpcMaxIdle);
		config.setMinIdle(CommonRpcRegisterConfig.commonRpcMinIdle);
		config.setMaxTotal(CommonRpcRegisterConfig.commonRpcMaxIdle);
		config.setMaxWaitMillis(CommonRpcRegisterConfig.commonRpcMaxWait);
		config.setMinEvictableIdleTimeMillis(CommonRpcRegisterConfig.commonRpcMinEvictableIdleTimeMillis);
		config.setNumTestsPerEvictionRun(CommonRpcRegisterConfig.commonRpcNumTestsPerEvictionRun);
		config.setTimeBetweenEvictionRunsMillis(CommonRpcRegisterConfig.commonRpcTimeBetweenEvictionRunsMillis);	
		return config;
    }
}
