package util;

import com.cross.plateform.common.rpc.tcp.netty4.util.NetworkKit;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by lejing on 15/10/29.
 */
public class LocalIpTest {
	@Test
	public void test1() throws UnknownHostException {
		System.out.println(InetAddress.getLocalHost().getHostAddress());
		System.out.println(NetworkKit.getLocalIp());
	}
}
