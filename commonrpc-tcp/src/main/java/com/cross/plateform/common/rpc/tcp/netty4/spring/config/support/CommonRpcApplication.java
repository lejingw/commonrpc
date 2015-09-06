package com.cross.plateform.common.rpc.tcp.netty4.spring.config.support;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import com.cross.plateform.common.rpc.core.util.StringUtils;
import com.cross.plateform.common.rpc.service.client.service.CommonRpcClientService;
import com.cross.plateform.common.rpc.service.server.service.CommonRpcServerService;
import com.cross.plateform.common.rpc.tcp.netty4.client.factory.CommonRpcTcpClientFactory;

public class CommonRpcApplication implements InitializingBean, DisposableBean {
    private String address = null;
    private String clientid = null;
    private Integer flag;//server :1  client :2
    private int timeout;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isNullOrEmpty(address)) {
            throw new RuntimeException("address can not be null or empty");
        }
        if (StringUtils.isNullOrEmpty(flag)) {
            throw new RuntimeException("flag can not be null or empty");
        }
        if (flag != 1 && flag != 2) {
            throw new RuntimeException("flag only be 1 or 2");
        }

        if (flag == 1) {//服务端
            CommonRpcServerService.getInstance().connectZookeeper(address, timeout);
        } else if (flag == 2) {//客户端
            CommonRpcClientService.getInstance().connectZookeeper(address, timeout);
            CommonRpcTcpClientFactory.getInstance().startClientFactory(timeout);//客户端启动
        }
    }

    @Override
    public void destroy() throws Exception {
        if(flag == 2) {
            CommonRpcClientService.getInstance().close();
            CommonRpcTcpClientFactory.getInstance().stopClientFactory();
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
