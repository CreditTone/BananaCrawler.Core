package banana.core.extractor2;

public class SelectItem {

	private String inputType;

	private String selectBody;

	public SelectItem(String selectitemStr) {
		int subStart = 0;
		if (selectitemStr.startsWith("string(")) {
			inputType = "string";
			subStart = 7;
		} else if (selectitemStr.startsWith("json(")) {
			inputType = "json";
			subStart = 5;
		} else if (selectitemStr.startsWith("html(")) {
			inputType = "html";
			subStart = 5;
		}
		selectBody = selectitemStr.substring(subStart, selectitemStr.length() - 1);
	}

	public String getInputType() {
		return inputType;
	}

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

	public String getSelectBody() {
		return selectBody;
	}

	public void setSelectBody(String selectBody) {
		this.selectBody = selectBody;
	}
	
}
