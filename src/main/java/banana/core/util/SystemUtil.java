package banana.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
	
	public static byte[] inputStreamToBytes(InputStream in){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = null;
		try{
			int len = 0;
			buf = new byte[1024];
			while((len = in.read(buf)) != -1){
				out.write(buf, 0, len);
			}
			buf = out.toByteArray();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return buf;
	}
	
	public static final byte[] intToBytes(int x){
		byte[] dataLength = new byte[4];
		dataLength[0] = (byte)((x >> 24) & 0xFF);
		dataLength[1] = (byte)((x >> 16) & 0xFF);
		dataLength[2] = (byte)((x >> 8) & 0xFF); 
		dataLength[3] = (byte)(x & 0xFF);
		return dataLength;
	}
}
