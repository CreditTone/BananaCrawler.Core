package banana.core.modle;

import java.io.IOException;
import java.util.Map;

import com.github.jknack.handlebars.Helper;

public interface ContextModle extends Map<String, Object> {
	
	public Object parseObject(String line) throws IOException;
	
	public String parseString(String line) throws IOException;
	
	public void copyTo(Map<String,Object> dst);
	
	void registerHelper(final String name, final Helper<Object> helper);
	
	public Object put(String key, Object value);
	
	public Object get(Object key);
	
}
