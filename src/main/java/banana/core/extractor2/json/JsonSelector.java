package banana.core.extractor2.json;

import com.jayway.jsonpath.Filter;

public class JsonSelector {
	
	private String[] steps;
	
	public JsonSelector(String v) {
		steps = v.split("\\.");
	}

	public String[] getSteps() {
		return steps;
	}
	
	public static Filter getWhereCondition(String conditionStr) {
		Condition condition = new Condition(conditionStr);
		return condition.getFilter();
	}

}
