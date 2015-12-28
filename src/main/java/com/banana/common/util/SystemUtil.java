package com.banana.common.util;

import com.banana.common.NodeStatus;

public final class SystemUtil {

	public static final NodeStatus getLocalNodeStatus(String host){
		NodeStatus nodeStat = new NodeStatus();
		nodeStat.setHost(host);
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
}
