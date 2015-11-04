package com.cross.plateform.common.rpc.tcp.netty4.spring.config;

import com.cross.plateform.common.rpc.tcp.netty4.spring.config.support.CommonRpcApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class CommonRpcApplicationParser implements BeanDefinitionParser {
	private static final Logger logger = LoggerFactory.getLogger(CommonRpcApplicationParser.class);

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String id = element.getAttribute("id");
		String address=element.getAttribute("address");
		String providerFlag=element.getAttribute("providerFlag");
		String timeout=element.getAttribute("timeout");
		
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(CommonRpcApplication.class);
		beanDefinition.setLazyInit(false);
        beanDefinition.getPropertyValues().addPropertyValue("address", address);
        beanDefinition.getPropertyValues().addPropertyValue("providerFlag", "true".equals(providerFlag));
        beanDefinition.getPropertyValues().addPropertyValue("timeout", timeout);

		if(!StringUtils.isEmpty(id)) {
			parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
		}else {
			id = parserContext.getReaderContext().registerWithGeneratedName(beanDefinition);
		}
		parserContext.registerComponent(new BeanComponentDefinition(beanDefinition, id));
		return null;
	}

}
