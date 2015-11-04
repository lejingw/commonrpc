/**
 * 
 */
package com.cross.plateform.common.rpc.zk.manager.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.zookeeper.server.quorum.FastLeaderElection;

import com.alibaba.fastjson.JSONObject;
import com.cross.plateform.common.rpc.zk.manager.client.CommonRpcManagerClient;
import com.cross.plateform.common.rpc.zk.manager.client.util.SpringContextUtil;

/**
 * @author liubing
 *
 */
public class ZkServlet extends HttpServlet {
	
	private CommonRpcManagerClient commonRpcManagerClient;
	/**
	 * 
	 */
	private static final long serialVersionUID = -1168652368070878064L;
	
	public ZkServlet(){
		
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");  
        response.setContentType("text/html;charset=utf-8");
        commonRpcManagerClient=(CommonRpcManagerClient) SpringContextUtil.getBean("commonRpcManagerClient");
        String type = request.getParameter("type");  //group,server
        String param = request.getParameter("param");//参数
        List<String> result=new ArrayList<String>();
        PrintWriter out=response.getWriter();
        if(type!=null&&param!=null){
        	if(type.equals("group")){
        		try {
					result=commonRpcManagerClient.getServersByGroup(param);
				} catch (Exception e) {
				}
        	}else{
        		try {
					result=commonRpcManagerClient.getClientsByServer(param);
				} catch (Exception e) {
				}
        	}
        	String json=JSONObject.toJSONString(result);
        	out.println("<html>");//输出的内容要放在body中
            out.println("<body>");
            if(type.equals("group")){
            	out.println("组:"+param+"---------服务:");
            }else{
            	out.println("服务端:"+param+"------客户端:");
            }
            
            out.println(json);
            out.println("</body>");
            out.println("</html>");
        }else{
        	 
            out.println("<html>");//输出的内容要放在body中
            out.println("<body>");
            out.println("param and type can not be null........");
            out.println("</body>");
            out.println("</html>");
        }
       
        
	}

}
