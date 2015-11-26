package com.jingcai.apps.commonrpc.tcp.spring.config;

import com.jingcai.apps.commonrpc.tcp.spring.config.support.CommonRpcReference;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class CommonRpcReferenceParser implements BeanDefinitionParser {

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String id = element.getAttribute("id");
		String intfType = element.getAttribute("interface");
		String group = element.getAttribute("group");
		String procotolType = element.getAttribute("procotolType");
		String codecType = element.getAttribute("codecType");
		String timeout = element.getAttribute("timeout");

		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(CommonRpcReference.class);
		beanDefinition.setLazyInit(false);

		beanDefinition.getPropertyValues().addPropertyValue("intfType", intfType);
		beanDefinition.getPropertyValues().addPropertyValue("group", group);
		beanDefinition.getPropertyValues().addPropertyValue("protocolType", procotolType);
		beanDefinition.getPropertyValues().addPropertyValue("codecType", codecType);
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
