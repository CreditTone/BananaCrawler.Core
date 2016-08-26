package banana.core.request;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * AttributeRequest是BasicRequest操作是属性的实现。
 *
 */
public class AttributeRequest extends BasicRequest {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * request属性
     */
    protected Map<String,Object> attributes = new HashMap<String, Object>();

	@Override
	public BasicRequest addAttribute(String attribute, Object value) {
    	attributes.put(attribute, value);
    	return this;
	}

	@Override
	public Object getAttribute(String attribute) {
		Object value = attributes.get(attribute);
		if(value == null && parentRequest != null){
			value = parentRequest.getAttribute(attribute);
		}
    	return value;
	}
	
	public Map<String,Object> getAttributes(){
		return attributes;
	}

	@Override
	public Set<String> enumAttributeNames() {
		return attributes.keySet();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		super.write(out);
		String attributesJson = JSON.toJSONString(attributes);
		out.writeUTF(attributesJson);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		super.readFields(in);
		String attributesJson = in.readUTF();
		attributes = JSON.parseObject(attributesJson, Map.class);
	}
	
}
