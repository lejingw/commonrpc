import com.cross.plateform.common.rpc.tcp.netty4.util.NetworkKit;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lejing on 15/10/29.
 */
public class LocalIpTest {
	public static void main(String[] args) throws UnknownHostException {
		System.out.println(InetAddress.getLocalHost().getHostAddress());
		System.out.println(getLocalIp());
		System.out.println(NetworkKit.getLocalIp());
	}

	public static String getLocalIp() {
		try {
			Pattern pattern = Pattern.compile("(192|172|10)\\.[0-9]+\\.[0-9]+\\.[0-9]+");
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

			while (interfaces.hasMoreElements()) {
				NetworkInterface ni = interfaces.nextElement();
				System.out.println("isUp=" + ni.isUp() + "--isLoopback=" + ni.isLoopback() + "--isVirtual=" + ni.isVirtual() + "--isPointToPoint=" + ni.isPointToPoint());
				if(!ni.isUp()){
					continue;
				}
				Enumeration<InetAddress> en = ni.getInetAddresses();
				while (en.hasMoreElements()) {
					InetAddress addr = en.nextElement();
					System.out.println("\t\t\t" + addr);
					String ip = addr.getHostAddress();
					Matcher matcher = pattern.matcher(ip);
//					if (matcher.matches()) {
//						return ip;
//					}
				}
			}
			return "0.0.0.0";
		} catch (Throwable e) {
			e.printStackTrace();
			return "0.0.0.0";
		}
	}
}
