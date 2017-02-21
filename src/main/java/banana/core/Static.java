package banana.core;

import java.util.HashMap;
import java.util.Map;

import banana.core.processor.DownloadProcessor;

public final class Static {
	
	public static final Map<String,Class<? extends DownloadProcessor>> pageProcessorIndex = new HashMap<String,Class<? extends DownloadProcessor>>();
	
}
