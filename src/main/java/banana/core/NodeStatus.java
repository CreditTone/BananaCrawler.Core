package banana.core;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class NodeStatus implements Writable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeLong(totalMemory);
		out.writeLong(freeMemory);
		out.writeLong(useMemory);
		out.writeInt(activeThread);
		out.writeInt(cpuNum);
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		totalMemory = in.readLong();
		freeMemory = in.readLong();
		useMemory = in.readLong();
		activeThread = in.readInt();
		cpuNum = in.readInt();
	}

	@Override
	public String toString() {
		return "NodeStatus [totalMemory=" + totalMemory + ", freeMemory=" + freeMemory + ", useMemory=" + useMemory
				+ ", activeThread=" + activeThread + ", cpuNum=" + cpuNum + "]";
	}

}
