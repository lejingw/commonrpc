package com.jingcai.apps.commonrpc.tcp.netty4.spring.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.jingcai.apps.commonrpc.tcp.netty4.spring.config.support.CommonRpcService;

public class CommonRpcServiceParser implements BeanDefinitionParser {
	private static final Logger logger = LoggerFactory.getLogger(CommonRpcServiceParser.class);

	public CommonRpcServiceParser() {
	}

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String ref = element.getAttribute("ref");
		String clsname = element.getAttribute("class");
		String filterRef = element.getAttribute("filterRef");

		if (StringUtils.isEmpty(ref) && StringUtils.isEmpty(clsname)) {
			logger.error("ref or class must be setting when define service !!!");
			return null;
		}
		if (StringUtils.isEmpty(ref)) {
			Class cls = null;
			try {
				cls = Class.forName(clsname);
			} catch (ClassNotFoundException e) {
				logger.error("could not found class:{}", clsname);
				return null;
			}

			RootBeanDefinition serviceDefinition = new RootBeanDefinition();
			serviceDefinition.setBeanClass(cls);
			ref = parserContext.getReaderContext().registerWithGeneratedName(serviceDefinition);
			parserContext.registerComponent(new BeanComponentDefinition(serviceDefinition, ref));
		}

		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(CommonRpcService.class);
		beanDefinition.setLazyInit(false);
		beanDefinition.getPropertyValues().addPropertyValue("ref", new RuntimeBeanReference(ref));

		if(!StringUtils.isEmpty(filterRef)) {
			beanDefinition.getPropertyValues().addPropertyValue("filterRef", new RuntimeBeanReference(filterRef));
		}
//		parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
		String id = parserContext.getReaderContext().registerWithGeneratedName(beanDefinition);
		parserContext.registerComponent(new BeanComponentDefinition(beanDefinition, id));
		return beanDefinition;
	}

}
