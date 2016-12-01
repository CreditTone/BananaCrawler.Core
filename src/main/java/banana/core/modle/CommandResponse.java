package banana.core.modle;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public final class CommandResponse implements Writable{
	
	public boolean success;
	
	public String error;
	
	public CommandResponse(){}
	
	public CommandResponse(boolean success){
		this(success, "");
	}
	
	public CommandResponse(boolean success,String error){
		this.success = success;
		this.error = error;
	}
	
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeBoolean(success);
		out.writeUTF(error == null?"":error);
	}
	@Override
	public void readFields(DataInput in) throws IOException {
		success = in.readBoolean();
		error = in.readUTF();
	}
}
