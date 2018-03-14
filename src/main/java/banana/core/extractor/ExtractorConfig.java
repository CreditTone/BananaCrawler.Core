package banana.core.extractor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ExtractorConfig implements Cloneable{
	
	private String input_type = "html";  //默认html，其他json、string
	
	private String output_type = "map"; //默认map，其他list
	
	private HashMap<String,Object> extractorConfig = new HashMap<String, Object>();
	
	public ExtractorConfig(Map<String,Object> config) {
		String input_type2 = (String) config.get("_input_type");
		if (input_type2 != null) {
			input_type = input_type2;
		}
		String output_type2 = (String) config.get("_output_type");
		if (output_type2 != null) {
			output_type = output_type2;
		}
		for (Entry<String, Object> entry : config.entrySet()) {
			if (entry.getKey().equals("_input_type") || entry.getKey().equals("_output_type")) {
				continue;
			}
			Object parser = entry.getValue();
			if (parser instanceof String) {
				extractorConfig.put(entry.getKey(), new ParserLine((String) parser));
			} else if (parser instanceof Map) {
				ExtractorConfig subEx = new ExtractorConfig((Map<String, Object>) parser);
				subEx.setInput_type(input_type);
				extractorConfig.put(entry.getKey(), subEx);
			}
		}
	}
	
	public String getInput_type() {
		return input_type;
	}

	public void setInput_type(String input_type) {
		this.input_type = input_type;
	}

	public String getOutput_type() {
		return output_type;
	}

	public void setOutput_type(String output_type) {
		this.output_type = output_type;
	}

	public HashMap<String, Object> getExtractorConfig() {
		return extractorConfig;
	}

	public void setExtractorConfig(HashMap<String, Object> extractorConfig) {
		this.extractorConfig = extractorConfig;
	}
	
}
