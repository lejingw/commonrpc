/**
 * 
 */
package com.cross.plateform.common.rpc.core.client.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.cross.plateform.common.rpc.core.client.RpcClient;
import com.cross.plateform.common.rpc.core.client.factory.RpcClientFactory;
import com.cross.plateform.common.rpc.core.route.CommonRpcRoute;
import com.cross.plateform.common.rpc.core.route.bean.RpcRouteServer;
import com.cross.plateform.common.rpc.core.util.SocketAddressUtil;
import com.cross.plateform.common.rpc.service.client.service.CommonRpcClientService;
/**
 * @author liubing1
 *
 */
public abstract class AbstractRpcClientInvocationHandler implements
		InvocationHandler {
	
	private String group;
	
	private int timeout;
	
	private String targetInstanceName;
	
	private int codecType;
	
	private int protocolType;
	

	
	public AbstractRpcClientInvocationHandler(
			String group, int timeout,
			String targetInstanceName, int codecType, int protocolType) {
		super();
		this.group = group;
		this.timeout = timeout;
		this.targetInstanceName = targetInstanceName;
		this.codecType = codecType;
		this.protocolType = protocolType;

	}

	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		// TODO Auto-generated method stub
		RpcClient client = null;
		
		String [] groups=group.split(",");
		
		Random r=new Random(); 
		
		int i=r.nextInt(groups.length);
		
		Set<InetSocketAddress>addresses =CommonRpcClientService.getInstance().getServersByGroup(groups[i]);
		
		List<RpcRouteServer> servers=SocketAddressUtil.getInetSocketAddress(addresses);
				
		InetSocketAddress server =CommonRpcRoute.GetBestServer(servers).getServer();
		
		client = getClientFactory().getClient(server.getAddress().getHostAddress(), server.getPort());
		String methodName = method.getName();
		String[] argTypes = createParamSignature(method.getParameterTypes());
		Object result= client.invokeImpl(targetInstanceName, methodName, argTypes, args, timeout, codecType, protocolType);
		return result;
	}
	
	private String[] createParamSignature(Class<?>[] argTypes) {
        if (argTypes == null || argTypes.length == 0) {
            return new String[] {};
        }
        String[] paramSig = new String[argTypes.length];
        for (int x = 0; x < argTypes.length; x++) {
            paramSig[x] = argTypes[x].getName();
        }
        return paramSig;
    }
	
	public abstract RpcClientFactory getClientFactory();
	
	
}
