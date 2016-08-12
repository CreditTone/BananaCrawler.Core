package banana.core;

import java.util.HashMap;
import java.util.Map;

import banana.core.processor.BinaryProcessor;
import banana.core.processor.PageProcessor;

public final class Static {
	
	public static final Map<String,Class<? extends PageProcessor>> pageProcessorIndex = new HashMap<String,Class<? extends PageProcessor>>();
	
	public static final Map<String,Class<? extends BinaryProcessor>> binaryProcessorIndex = new HashMap<String,Class<? extends BinaryProcessor>>();
	
}
