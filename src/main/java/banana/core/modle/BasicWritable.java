package banana.core.modle;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

import com.alibaba.fastjson.JSON;

public class BasicWritable implements Writable {
	
	public static enum BasicType {
		INT,DOUBLE,SHORT,FLOAT,STRING,BOOLEAN,BYTE,JSON
	}
	
	private BasicType basicType;
	
	private Object value;
	
	public BasicWritable(){}
	
	public BasicWritable(int value) {
		this.basicType = BasicType.INT;
		this.value = value;
	}
	
	public BasicWritable(double value) {
		this.basicType = BasicType.DOUBLE;
		this.value = value;
	}
	
	public BasicWritable(short value) {
		this.basicType = BasicType.SHORT;
		this.value = value;
	}
	
	public BasicWritable(byte value) {
		this.basicType = BasicType.BYTE;
		this.value = value;
	}
	
	public BasicWritable(String value) {
		this.basicType = BasicType.STRING;
		this.value = value;
	}
	
	public BasicWritable(boolean value) {
		this.basicType = BasicType.BOOLEAN;
		this.value = value;
	}
	
	public BasicWritable(Object value) throws Exception{
		if (value instanceof Integer){
			this.basicType = BasicType.INT;
		}else if (value instanceof Double){
			this.basicType = BasicType.DOUBLE;
		}else if(value instanceof Short){
			this.basicType = BasicType.SHORT;
		}else if(value instanceof Byte){
			this.basicType = BasicType.BYTE;
		}else if(value instanceof String){
			this.basicType = BasicType.STRING;
		}else if(value instanceof Boolean){
			this.basicType = BasicType.BOOLEAN;
		}else if(value instanceof Float){
			this.basicType = BasicType.FLOAT;
		}else if (value instanceof JSON){
			this.basicType = BasicType.JSON;
		}else{
			throw new Exception("not supported type" + value.getClass());
		}
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(basicType.name());
		switch (basicType) {
		case INT:
			out.writeInt((int) value);
			break;
		case BOOLEAN:
			out.writeBoolean((boolean) value);
			break;
		case BYTE:
			out.writeByte((byte) value);
			break;
		case FLOAT:
			out.writeFloat((float) value);
			break;
		case SHORT:
			out.writeShort((short) value);
			break;
		case STRING:
			out.writeUTF((String) value);
			break;
		case DOUBLE:
			out.writeDouble((double) value);
			break;
		case JSON:
			out.writeUTF(JSON.toJSONString(value));
			break;
		}
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		basicType = BasicType.valueOf(in.readUTF());
		switch (basicType) {
		case INT:
			value = in.readInt();
			break;
		case BOOLEAN:
			value = in.readBoolean();
			break;
		case BYTE:
			value = in.readByte();
			break;
		case FLOAT:
			value = in.readFloat();
			break;
		case SHORT:
			value = in.readShort();
			break;
		case STRING:
			value = in.readUTF();
			break;
		case DOUBLE:
			value = in.readDouble();
			break;
		case JSON:
			value = JSON.parse(in.readUTF());
			break;
		}
	}

}
