package banana.core.extractor2.json;

public class JsonSelector {
	
	private String jsonPath;
	
	public JsonSelector(String v) {
		String[] tks = v.split(";");
		jsonPath = tks[0];
	}

	public String getJsonPath() {
		return jsonPath;
	}

	public void setJsonPath(String jsonPath) {
		this.jsonPath = jsonPath;
	}
	
	
}
