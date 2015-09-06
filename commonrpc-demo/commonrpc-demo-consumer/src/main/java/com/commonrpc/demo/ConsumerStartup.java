package com.commonrpc.demo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.commonrpc.demo.sdk.DemoService;

public class ConsumerStartup {

    public static void main(String[] args) throws IOException, InterruptedException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"spring-context.xml"});
        DemoService demoService = (DemoService) context.getBean("demoService");

        for (int i = 0; i < 100000000; i++) {
            System.out.println("------------------call------");
            try {
                String hello = demoService.sayHello("world");
                System.err.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + hello + "\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(1500);
        }

    }
}
