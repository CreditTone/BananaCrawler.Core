package banana.core.modle;

import java.io.IOException;
import java.util.Map;

public interface ContextModle extends Map<String, Object> {
	
	public Object parseObject(String line) throws IOException;
	
	public String parseString(String line) throws IOException;
	
	public void copyTo(Map<String,Object> dst);
	
	public boolean existPath(String path);
	
}
