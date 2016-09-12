package banana.core.modle;

import java.io.IOException;
import java.util.Map;

public interface ContextModle extends Map<String, Object> {
	
	public String parse(String line) throws IOException;
	
}
