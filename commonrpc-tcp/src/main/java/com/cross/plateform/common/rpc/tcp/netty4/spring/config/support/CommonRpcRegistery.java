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
        // TODO Auto-generated method stub
        CommonRpcServerService.getInstance().close();
        CommonRpcTcpServer.getInstance().stop();//停止
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // TODO Auto-generated method stub
        if (port == 0) {
            throw new Exception("parameter port can not be null");
        }
        CommonRpcServerService.getInstance().registerService(group, getLocalhost());
        CommonRpcTcpServer.getInstance().setCodecType(codecType);
        CommonRpcTcpServer.getInstance().setProcotolType(procotolType);
        CommonRpcTcpServer.getInstance().setThreadCount(threadCount);

        CommonRpcTcpServer.getInstance().start(port, timeout);

    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * @return the procotolType
     */
    public int getProcotolType() {
        return procotolType;
    }

    /**
     * @param procotolType the procotolType to set
     */
    public void setProcotolType(int procotolType) {
        this.procotolType = procotolType;
    }

    /**
     * @return the codecType
     */
    public int getCodecType() {
        return codecType;
    }

    /**
     * @param codecType the codecType to set
     */
    public void setCodecType(int codecType) {
        this.codecType = codecType;
    }

    private String getLocalhost() {
        try {
            String ip = StringUtils.isNullOrEmpty(this.getIp()) ? InetAddress.getLocalHost().getHostAddress() : this.getIp();
            return ip + ":" + port;
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException("无法获取本地Ip", e);
        }

    }

    /**
     * @return the group
     */
    public String getGroup() {
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * @return the threadCount
     */
    public int getThreadCount() {
        return threadCount;
    }

    /**
     * @param threadCount the threadCount to set
     */
    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

}
