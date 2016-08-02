package banana.core.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import banana.core.NodeStatus;

public final class SystemUtil {

	public static final NodeStatus getLocalNodeStatus(){
		NodeStatus nodeStat = new NodeStatus();
		Runtime runtime = Runtime.getRuntime();
		nodeStat.setCpuNum(runtime.availableProcessors());
		nodeStat.setTotalMemory(runtime.totalMemory());
		nodeStat.setFreeMemory(runtime.freeMemory());
		nodeStat.setUseMemory(nodeStat.getTotalMemory() - nodeStat.getFreeMemory());
		ThreadGroup parentThread = Thread.currentThread().getThreadGroup();
		for(;parentThread.getParent()!=null;parentThread = parentThread.getParent());
		nodeStat.setActiveThread(parentThread.activeCount());
		return nodeStat;
	}
	
	public static String getLocalIP() {
        List<String> ipList = new ArrayList<String>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface;
            Enumeration<InetAddress> inetAddresses;
            InetAddress inetAddress;
            String ip;
            while (networkInterfaces.hasMoreElements()) {
                networkInterface = networkInterfaces.nextElement();
                inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    inetAddress = inetAddresses.nextElement();
                    if (inetAddress != null && inetAddress instanceof Inet4Address) { // IPV4
                        ip = inetAddress.getHostAddress();
                        ipList.add(ip);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        for (String host : ipList) {
			if (host.startsWith("192") || host.startsWith("172") || host.startsWith("10")){
				return host;
			}
		}
        return ipList.isEmpty()?null:ipList.get(0);
    }
	
}
