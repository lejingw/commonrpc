/**
 * 
 */
package com.cross.plateform.common.rpc.redis.register.server;


/**
 * @author liubing1
 *
 */
public interface ICommonRpcRegisterServer{
	
	public void deleteRpcServiceServer(String group,String serverIp);
	
	/**
	 * 注册服务 
	 * @param group 组名
	 * @param server 机器
	 * @param status 状态
	 */
	public boolean registerService(String group ,String server);
	
	public void close();
}
