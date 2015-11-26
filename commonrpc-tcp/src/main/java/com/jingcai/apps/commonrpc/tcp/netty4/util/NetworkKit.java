package com.jingcai.apps.commonrpc.tcp.netty4.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lejing on 15/10/29.
 */
public class NetworkKit {
	private static final Logger logger = LoggerFactory.getLogger(NetworkKit.class);
	public static String getLocalIp() {
		try {
			Pattern pattern = Pattern.compile("(192|172|10)\\.[0-9]+\\.[0-9]+\\.[0-9]+");
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

			while (interfaces.hasMoreElements()) {
				NetworkInterface ni = interfaces.nextElement();
				logger.debug("isUp={}, isLoopback={}, isVirtual={}, isPointToPoint={}", ni.isUp(), ni.isLoopback(), ni.isVirtual(), ni.isPointToPoint());
				if (!ni.isUp() || ni.isLoopback()) {
					continue;
				}
				Enumeration<InetAddress> en = ni.getInetAddresses();
				while (en.hasMoreElements()) {
					InetAddress addr = en.nextElement();
					//System.out.println("\t\t\t" + addr);
					String ip = addr.getHostAddress();
					logger.debug("interface={}, ip={}", ni.getDisplayName(), ip);
					Matcher matcher = pattern.matcher(ip);
					if (matcher.matches()) {
						return ip;
					}
				}
			}
		} catch (Exception e) {
		}
		throw new RuntimeException("无法获取本地Ip");
	}
}
