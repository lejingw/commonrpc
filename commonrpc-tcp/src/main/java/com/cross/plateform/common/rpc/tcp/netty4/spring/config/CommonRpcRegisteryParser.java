/**
 * 
 */
package com.cross.plateform.common.rpc.tcp.netty4.spring.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.cross.plateform.common.rpc.tcp.netty4.spring.config.support.CommonRpcRegistery;
/**
 * @author liubing1
 *
 */
public class CommonRpcRegisteryParser implements BeanDefinitionParser {

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.xml.BeanDefinitionParser#parse(org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		// TODO Auto-generated method stub
		String id = element.getAttribute("id");
		String ip = element.getAttribute("ip");
		int port=Integer.parseInt(element.getAttribute("port"));
		int timeout=Integer.parseInt(element.getAttribute("timeout"));
		int procotolType=Integer.parseInt(element.getAttribute("procotolType"));
		int codecType=Integer.parseInt(element.getAttribute("codecType"));
		int threadCount=Integer.parseInt(element.getAttribute("threadCount"));
		String group=element.getAttribute("group");
		
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(CommonRpcRegistery.class);
		beanDefinition.getPropertyValues().addPropertyValue("ip", ip);
		beanDefinition.getPropertyValues().addPropertyValue("port", port);
		beanDefinition.getPropertyValues().addPropertyValue("timeout", timeout);
		beanDefinition.getPropertyValues().addPropertyValue("group", group);
		beanDefinition.getPropertyValues().addPropertyValue("procotolType", procotolType);
	    beanDefinition.getPropertyValues().addPropertyValue("codecType", codecType);
	    beanDefinition.getPropertyValues().addPropertyValue("threadCount", threadCount);
	    
        parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
        
		return beanDefinition;
	}

}
