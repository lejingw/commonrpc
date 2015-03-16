/**
 * 
 */
package com.cross.plateform.common.rpc.tcp.netty4.spring.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import com.cross.plateform.common.rpc.tcp.netty4.spring.config.support.CommonRpcApplication;
/**
 * @author liubing1
 *
 */
public class CommonRpcApplicationParser implements BeanDefinitionParser {

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.xml.BeanDefinitionParser#parse(org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		// TODO Auto-generated method stub
		String id = element.getAttribute("id");
		String address=element.getAttribute("address");
		
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(CommonRpcApplication.class);
		beanDefinition.setLazyInit(false);
        beanDefinition.getPropertyValues().addPropertyValue("address", address);
        parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
		return beanDefinition;
	}

}
