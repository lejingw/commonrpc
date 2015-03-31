/**
 * 
 */
package com.cross.plateform.common.rpc.http.netty4.server.thread;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONObject;
import com.cross.plateform.common.rpc.core.disruptor.RpcValueEvent;
import com.cross.plateform.common.rpc.core.server.handler.factory.CommonRpcServerHandlerFactory;
import com.cross.plateform.common.rpc.core.server.service.bean.RpcRouteInfo;
import com.cross.plateform.common.rpc.core.util.StringUtils;
import com.cross.plateform.common.rpc.http.netty4.server.bean.ServerBean;
import com.lmax.disruptor.EventHandler;

/**
 * @author liubing
 *
 */
public class CommonRpcHttpServerEventHandler implements EventHandler<RpcValueEvent>{
	

	private boolean keepAlive=true;
	
	public CommonRpcHttpServerEventHandler() {
		super();
	}

	@Override
	public void onEvent(RpcValueEvent event, long sequence, boolean endOfBatch)
			throws Exception {
		// TODO Auto-generated method stub
		ChannelHandlerContext ctx=(ChannelHandlerContext) event.getCtx();
		try{
			
			ServerBean serverBean=null;
			Map<String, Object> map=new HashMap<String, Object>();
			String httpType=null;
			String url=null;
			if (event.getValue() instanceof HttpRequest) {
				HttpRequest request = (HttpRequest)event.getValue();
				if (HttpHeaders.is100ContinueExpected(request)){
					DefaultHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.CONTINUE);
					serverBean=new ServerBean(response, null,keepAlive);
					writeResponse(ctx, serverBean);
					return;
				}
				httpType=request.getMethod().name();
				url=request.getUri();
				url=url.substring(1, url.length());
				if(url.contains("?")){
					url=url.substring(0, url.indexOf("?"));
				}
				keepAlive=HttpHeaders.isKeepAlive(request);
				QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
				map.putAll(getParametersByUrl(queryStringDecoder));
			}
			if (event.getValue() instanceof HttpContent) {
				HttpContent httpContent = (HttpContent) event.getValue();
				ByteBuf content = httpContent.content();
				if (content.isReadable()) {
					String contentMsg=content.toString(CharsetUtil.UTF_8);
					if(!StringUtils.isNullOrEmpty(contentMsg)){
						Map<String, Object> params=JSONObject.parseObject(contentMsg, Map.class);
						map.putAll(params);
					}
					
				}
				if (event.getValue() instanceof LastHttpContent){
					RpcRouteInfo rpcRouteInfo=CommonRpcServerHandlerFactory.getHttpServerHandler().isRouteInfos(url, httpType, map);
					if(rpcRouteInfo==null||rpcRouteInfo.getRoute()==null){
						DefaultHttpResponse response=getDefaultHttpResponse(404, null);
						serverBean=new ServerBean(response, null,keepAlive);
						writeResponse(ctx, serverBean);
						return;
					}else{
						DefaultHttpResponse response=getDefaultHttpResponse(200, rpcRouteInfo.getReturnType());
						Object result=CommonRpcServerHandlerFactory.getHttpServerHandler().methodInvoke(rpcRouteInfo);
						if(result==null){
							serverBean=new ServerBean(response, null,keepAlive);
							writeResponse(ctx, serverBean);
							return;
						}else{
							String resultMsg=JSONObject.toJSONString(result);
							DefaultHttpContent defaultHttpContent = new DefaultHttpContent(Unpooled.copiedBuffer(resultMsg, CharsetUtil.UTF_8));
							serverBean=new ServerBean(response, defaultHttpContent,keepAlive);
							writeResponse(ctx, serverBean);
							return;
						}
						
					}
				}
			}
		}catch(Exception e){
			DefaultHttpResponse response=getDefaultHttpResponse(500, null);
			DefaultHttpContent defaultHttpContent = new DefaultHttpContent(Unpooled.copiedBuffer(e.getMessage(), CharsetUtil.UTF_8));
			ServerBean serverBean=new ServerBean(response, defaultHttpContent,keepAlive);
			writeResponse(ctx, serverBean);
			return;
		}finally{
			ReferenceCountUtil.release(event.getValue());
		}
	}
	
	private Map<String, Object> getParametersByUrl(QueryStringDecoder queryStringDecoder){
		Map<String, Object> map=new HashMap<String, Object>();
		Map<String, List<String>> params = queryStringDecoder.parameters();
		if (!params.isEmpty()) {
			for(Entry<String, List<String>> p: params.entrySet()) {
				String key = p.getKey();
				List<String> vals = p.getValue();
				for (String val : vals) {
					map.put(key, val);
				}
			}
			
		}
		return map;
	}
	
	private DefaultHttpResponse getDefaultHttpResponse(int type,String returnType){
		DefaultHttpResponse httpResponse=null;
		if(type==404){
			httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
		}else if(type==500){
			httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
		}else{
			httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		}
		httpResponse.headers().add(HttpHeaders.Names.TRANSFER_ENCODING,
				HttpHeaders.Values.CHUNKED);
		if(type!=404&&type!=500){
			if(returnType.equals("json")){
				httpResponse.headers().set(HttpHeaders.Names.CONTENT_TYPE,"application/json; charset=UTF-8");
			}else if(returnType.equals("html")){
				httpResponse.headers().set(HttpHeaders.Names.CONTENT_TYPE,"text/plain; charset=UTF-8");
			}else if(returnType.equals("xml")){
				httpResponse.headers().set(HttpHeaders.Names.CONTENT_TYPE,"text/xml; charset=UTF-8");
			}
		}else{
			httpResponse.headers().set(HttpHeaders.Names.CONTENT_TYPE,
					"text/plain; charset=UTF-8");
		}
		
		httpResponse.headers().set(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
		httpResponse.headers().set(HttpHeaders.Names.CACHE_CONTROL,"no-cache");
		httpResponse.headers().set(HttpHeaders.Names.PRAGMA,"no-cache");
		httpResponse.headers().set(HttpHeaders.Names.EXPIRES,"-1");
		return httpResponse;
	}
	
	private void writeResponse(ChannelHandlerContext ctx,ServerBean serverBean){
		
		if(ctx.channel().isOpen()&&serverBean!=null){
			if(!serverBean.isKeepAlive()){
				 ctx.write(serverBean.getResponse()).addListener(ChannelFutureListener.CLOSE);
			}else{
				serverBean.getResponse().headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
	             ctx.write(serverBean.getResponse());
			}
			if(serverBean.getDefaultHttpContent()!=null){
				ctx.writeAndFlush(serverBean.getDefaultHttpContent());
			}
		}
		
		DefaultLastHttpContent lastHttpContent = new DefaultLastHttpContent();
		ctx.writeAndFlush(lastHttpContent);
	}
}
