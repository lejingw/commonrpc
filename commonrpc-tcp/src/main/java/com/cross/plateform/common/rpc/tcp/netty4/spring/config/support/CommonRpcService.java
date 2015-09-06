package com.cross.plateform.common.rpc.tcp.netty4.spring.config.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import com.cross.plateform.common.rpc.core.util.StringUtils;
import com.cross.plateform.common.rpc.server.filter.RpcFilter;
import com.cross.plateform.common.rpc.tcp.netty4.server.CommonRpcTcpServer;

public class CommonRpcService implements ApplicationContextAware, ApplicationListener {
    private String interfacename;//接口名称 key
    private String ref;//服务类bean value
    private ApplicationContext applicationContext;
    private String filterRef;//拦截器类

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (StringUtils.isNullOrEmpty(filterRef) || !(applicationContext.getBean(filterRef) instanceof RpcFilter)) {//为空
            CommonRpcTcpServer.getInstance().registerProcessor(interfacename, applicationContext.getBean(ref), null);
        } else {
            CommonRpcTcpServer.getInstance().registerProcessor(interfacename, applicationContext.getBean(ref), (RpcFilter) applicationContext.getBean(filterRef));
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public String getInterfacename() {
        return interfacename;
    }

    public void setInterfacename(String interfacename) {
        this.interfacename = interfacename;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public String getFilterRef() {
        return filterRef;
    }

    public void setFilterRef(String filterRef) {
        this.filterRef = filterRef;
    }
}
