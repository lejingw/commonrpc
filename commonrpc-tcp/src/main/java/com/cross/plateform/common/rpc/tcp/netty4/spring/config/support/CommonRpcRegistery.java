package com.cross.plateform.common.rpc.tcp.netty4.spring.config.support;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.cross.plateform.common.rpc.core.util.StringUtils;
import com.cross.plateform.common.rpc.service.server.service.CommonRpcServerService;
import com.cross.plateform.common.rpc.tcp.netty4.server.CommonRpcTcpServer;

public class CommonRpcRegistery implements InitializingBean, DisposableBean {
    private String ip;//暴露的ip
    private int port;//端口号
    private int timeout;
    private int procotolType;//协议名称
    private int codecType;//编码类型
    private String group;//组
    private int threadCount;//线程数

    @Override
    public void destroy() throws Exception {
        CommonRpcServerService.getInstance().close();
        CommonRpcTcpServer.getInstance().stop();//停止
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (port == 0) {
            throw new Exception("parameter port can not be null");
        }
        CommonRpcServerService.getInstance().registerService(group, getLocalhost());
        CommonRpcTcpServer.getInstance().setCodecType(codecType);
        CommonRpcTcpServer.getInstance().setProcotolType(procotolType);
        CommonRpcTcpServer.getInstance().setThreadCount(threadCount);

        CommonRpcTcpServer.getInstance().start(port, timeout);

    }

    private String getLocalhost() {
        try {
            String ip = StringUtils.isNullOrEmpty(this.getIp()) ? InetAddress.getLocalHost().getHostAddress() : this.getIp();
            return ip + ":" + port;
        } catch (UnknownHostException e) {
            throw new RuntimeException("无法获取本地Ip", e);
        }

    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getProcotolType() {
        return procotolType;
    }

    public void setProcotolType(int procotolType) {
        this.procotolType = procotolType;
    }

    public int getCodecType() {
        return codecType;
    }

    public void setCodecType(int codecType) {
        this.codecType = codecType;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }
}
