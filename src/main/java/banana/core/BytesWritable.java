package banana.core;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public abstract class BytesWritable implements Writable {
	
	public byte[] toBytes(){
		ByteArrayOutputStream out = null;
		DataOutputStream dataOutput = null;
		try{
			out = new ByteArrayOutputStream();
			dataOutput = new DataOutputStream(out);
			write(dataOutput);
			dataOutput.flush();
			return out.toByteArray();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (dataOutput != null){
				try {
					dataOutput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public void load(byte[] data) {
		ByteArrayInputStream in = null;
		DataInputStream dataInput = null;
		try{
			in = new ByteArrayInputStream(data);
			dataInput = new DataInputStream(in);
			readFields(dataInput);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (dataInput != null){
				try {
					dataInput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (in != null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
