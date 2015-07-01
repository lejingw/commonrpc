/**
 * 
 */
package com.cross.plateform.common.rpc.tcp.netty4.spring.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.cross.plateform.common.rpc.core.all.message.CommonRpcRequest;
import com.cross.plateform.common.rpc.core.protocol.impl.DefualtRpcProtocolImpl;
import com.cross.plateform.common.rpc.tcp.netty4.spring.config.support.CommonRpcApplication;
/**
 * @author liubing1
 *
 */
public class CommonRpcApplicationParser implements BeanDefinitionParser {

	private static final Log LOGGER = LogFactory
			.getLog(CommonRpcApplicationParser.class);
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.xml.BeanDefinitionParser#parse(org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		// TODO Auto-generated method stub
		String id = element.getAttribute("id");
		String address=element.getAttribute("address");
		String clientid=element.getAttribute("clientid");  //用于标识不同客户端,也可不配
		String flag=element.getAttribute("flag");
		String timeout=element.getAttribute("timeout");
		
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(CommonRpcApplication.class);
		beanDefinition.setLazyInit(false);
        beanDefinition.getPropertyValues().addPropertyValue("address", address);
        beanDefinition.getPropertyValues().addPropertyValue("clientid", clientid);
        beanDefinition.getPropertyValues().addPropertyValue("flag", flag);
        beanDefinition.getPropertyValues().addPropertyValue("timeout", timeout);
        
        
        parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
		return beanDefinition;
	}

}
