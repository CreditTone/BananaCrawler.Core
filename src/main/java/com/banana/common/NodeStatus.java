package com.banana.common;

import java.io.Serializable;

public class NodeStatus implements Serializable{

	private String host;
	
	private long totalMemory;
	
	private long freeMemory;
	
	private long useMemory;
	
	private int activeThread;
	
	private int cpuNum;
	
	public long getTotalMemory() {
		return totalMemory;
	}

	public void setTotalMemory(long totalMemory) {
		this.totalMemory = totalMemory;
	}

	public long getFreeMemory() {
		return freeMemory;
	}

	public void setFreeMemory(long freeMemory) {
		this.freeMemory = freeMemory;
	}

	public int getCpuNum() {
		return cpuNum;
	}

	public void setCpuNum(int cpuNum) {
		this.cpuNum = cpuNum;
	}

	public long getUseMemory() {
		return useMemory;
	}

	public void setUseMemory(long useMemory) {
		this.useMemory = useMemory;
	}

	public int getActiveThread() {
		return activeThread;
	}

	public void setActiveThread(int activeThread) {
		this.activeThread = activeThread;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

}
