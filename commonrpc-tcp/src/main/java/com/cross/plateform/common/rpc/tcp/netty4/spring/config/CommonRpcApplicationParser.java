package com.cross.plateform.common.rpc.tcp.netty4.spring.config;

import com.cross.plateform.common.rpc.tcp.netty4.spring.config.support.CommonRpcApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class CommonRpcApplicationParser implements BeanDefinitionParser {
	private static final Logger logger = LoggerFactory.getLogger(CommonRpcApplicationParser.class);

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
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
