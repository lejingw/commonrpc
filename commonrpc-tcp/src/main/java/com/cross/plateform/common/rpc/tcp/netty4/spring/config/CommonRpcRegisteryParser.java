/**
 *
 */
package com.cross.plateform.common.rpc.tcp.netty4.spring.config;

import com.cross.plateform.common.rpc.tcp.netty4.spring.config.support.CommonRpcRegistery;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class CommonRpcRegisteryParser implements BeanDefinitionParser {

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String id = element.getAttribute("id");
		String ip = element.getAttribute("ip");
		String port = element.getAttribute("port");
		String timeout = element.getAttribute("timeout");
		String procotolType = element.getAttribute("procotolType");
		String codecType = element.getAttribute("codecType");
		String threadCount = element.getAttribute("threadCount");
		String group = element.getAttribute("group");

		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(CommonRpcRegistery.class);
		beanDefinition.getPropertyValues().addPropertyValue("ip", ip);
		beanDefinition.getPropertyValues().addPropertyValue("port", port);
		beanDefinition.getPropertyValues().addPropertyValue("timeout", timeout);
		beanDefinition.getPropertyValues().addPropertyValue("group", group);
		beanDefinition.getPropertyValues().addPropertyValue("procotolType", procotolType);
		beanDefinition.getPropertyValues().addPropertyValue("codecType", codecType);
		beanDefinition.getPropertyValues().addPropertyValue("threadCount", threadCount);

		if(!StringUtils.isEmpty(id)) {
			parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
		}else {
			id = parserContext.getReaderContext().registerWithGeneratedName(beanDefinition);
		}
		parserContext.registerComponent(new BeanComponentDefinition(beanDefinition, id));
		return null;
	}

}
