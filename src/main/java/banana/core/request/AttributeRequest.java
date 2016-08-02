package banana.core.request;

import java.util.HashMap;
import java.util.Set;

/**
 * AttributeRequest是BasicRequest操作是属性的实现。
 * @author 郭钟 
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
    protected HashMap<String,Object> attributes = new HashMap<String, Object>();

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
	
	public HashMap<String,Object> getAttributes(){
		return attributes;
	}

	@Override
	public Set<String> enumAttributeNames() {
		return attributes.keySet();
	}

}
