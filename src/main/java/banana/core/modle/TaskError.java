package banana.core.modle;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.Writable;

import com.alibaba.fastjson.JSON;

public class TaskError implements Writable{
	
	public static final String PROCESSOR_ERROR_TYPE = "processor_error_type";
	
	public static final String FORWORD_ERROR_TYPE = "forword_error_type";
	
	public String taskname;
	
	public String taskid;
	
	public Date time = new Date();

	public String errorType;
	
	public String exceptionClass;
	
	public String exceptionMessage;
	
	public int firstNativeLineNumber;
	
	public String method = "";
	
	public Map<String,Object> runtimeContext = new HashMap<String,Object>(){

		@Override
		public void putAll(Map<? extends String, ? extends Object> m) {
			m.remove("_content");
			super.putAll(m);
		}
		
	};
	
	public TaskError(){}

	public TaskError(String taskname,String taskid,String errorType, Exception exception) {
		this.taskname = taskname;
		this.taskid = taskid;
		this.errorType = errorType;
		this.exceptionClass = exception.getClass().getName();
		this.exceptionMessage = exception.getMessage();
		StackTraceElement[] stackTraceElements = exception.getStackTrace();
		if (stackTraceElements == null){
			return;
		}
		for (int i = 0; i < stackTraceElements.length; i++) {
			if(stackTraceElements[i].isNativeMethod()){
				firstNativeLineNumber = stackTraceElements[i].getLineNumber();
				method = stackTraceElements[i].getClassName() + "."+ stackTraceElements[i].getMethodName();
				break;
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((errorType == null) ? 0 : errorType.hashCode());
		result = prime * result + firstNativeLineNumber;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TaskError other = (TaskError) obj;
		if (errorType == null) {
			if (other.errorType != null)
				return false;
		} else if (!errorType.equals(other.errorType))
			return false;
		if (firstNativeLineNumber != other.firstNativeLineNumber)
			return false;
		return true;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(taskname);
		out.writeUTF(taskid);
		out.writeLong(time.getTime());
		out.writeUTF(errorType);
		out.writeUTF(exceptionClass);
		out.writeUTF(exceptionMessage);
		out.writeInt(firstNativeLineNumber);
		out.writeUTF(JSON.toJSONString(runtimeContext));
		out.writeUTF(method);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		taskname = in.readUTF();
		taskid = in.readUTF();
		time.setTime(in.readLong());
		errorType = in.readUTF();
		exceptionClass = in.readUTF();
		exceptionMessage = in.readUTF();
		firstNativeLineNumber = in.readInt();
		runtimeContext = JSON.parseObject(in.readUTF(), Map.class);
		method = in.readUTF();
	}
	
}
