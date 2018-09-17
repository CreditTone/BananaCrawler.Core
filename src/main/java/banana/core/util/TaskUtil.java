package banana.core.util;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSON;

import banana.core.modle.Task;

public class TaskUtil {
	
	private static class InternelTask {
		public List<LinkedHashMap> processors;
	}

	public static HashMap<String,String[]> getProcessorExorders(String configBody){
		HashMap<String,String[]> exorders = new HashMap<>();
		InternelTask m = JSON.parseObject(configBody, InternelTask.class);
		for (LinkedHashMap processorObj : m.processors) {
			Set<String> keySet = processorObj.keySet();
			String index = (String) processorObj.get("index");
			keySet.remove("index");
			String[] exorder = new String[keySet.size()];
			keySet.toArray(exorder);
			exorders.put(index, exorder);
		}
		return exorders;
	}
	
	public static Task getTaskFromJson(String configBody) {
		HashMap<String,String[]> exorders = getProcessorExorders(configBody);
		Task task = JSON.parseObject(configBody, Task.class);
		for (Task.PageProcessorConfig pageProcessorConfig : task.processors) {
			pageProcessorConfig.exorders = exorders.get(pageProcessorConfig.index);
		}
		return task;
	}
	
}
