package banana.core.extractor2;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import banana.core.extractor2.html.HtmlExtractor;
import banana.core.extractor2.json.JsonExtractor;
import banana.core.extractor2.string.StringExtractor;

public class ComplexSelectLine {

	private List<SelectItem> selectItems;

	private List<String> resultType;

	public ComplexSelectLine(String complexParseLine) {
		// TODO Auto-generated constructor stub
		selectItems = new ArrayList<>();
		resultType = new ArrayList<>();
		String[] complexParseLineAndTypes = complexParseLine.split(">");
		complexParseLine = complexParseLineAndTypes[0].trim();
		for (int i = 1; i < complexParseLineAndTypes.length; i++) {
			resultType.add(complexParseLineAndTypes[i].trim());
		}
		Pattern pattern = Pattern.compile("\\s*(string|json|html)\\(");
		Matcher matcher = pattern.matcher(complexParseLine);
		List<String> regResult = new ArrayList<>();
		while(matcher.find()) {
			regResult.add(matcher.group());
		}
		int endWith = 0;
		for (int i = 0; i < regResult.size(); i++) {
			complexParseLine = complexParseLine.substring(endWith, complexParseLine.length()).trim();
			if (i == regResult.size() -1) {
				endWith = complexParseLine.length();
			}else {
				endWith = complexParseLine.indexOf(regResult.get(i+1));
			}
			String selectitemStr = complexParseLine.substring(0 , endWith).trim();
			if (!selectitemStr.endsWith(")")) {
				selectitemStr += ")";
			}
			selectItems.add(new SelectItem(selectitemStr));
		}
	}
	
	public static void main(String[] args) {
		new ComplexSelectLine("string(,([^,]+)) json(center)");
	}

	public List<SelectItem> getSelectItems() {
		return selectItems;
	}

	public void setSelectItems(List<SelectItem> selectItems) {
		this.selectItems = selectItems;
	}

	public List<String> getResultType() {
		return resultType;
	}

	public void setResultType(List<String> resultType) {
		this.resultType = resultType;
	}
	
	public String doComplexSelectLineFirst(String body) throws Exception{
		String cuurentRet = body;
		for (SelectItem selectItem : this.selectItems) {
			if (selectItem.getInputType().equals("html")) {
				cuurentRet = HtmlExtractor.doHtmlOneExtractor(selectItem.getSelectBody(), cuurentRet);
			}else if (selectItem.getInputType().equals("json")) {
				Object jsonRet = JsonExtractor.doJsonOneExtractor(selectItem.getSelectBody(), cuurentRet);
				cuurentRet = jsonRet.toString();
			}else if (selectItem.getInputType().equals("string")) {
				cuurentRet = StringExtractor.doStringOneExtractor(selectItem.getSelectBody(), cuurentRet);
			}
		}
		return cuurentRet;
	}
	
	public List<Object> doComplexSelectLineArray(String body) throws Exception {
		SelectItem lastSelectItem = selectItems.remove(selectItems.size()-1);
		String currentRet = this.doComplexSelectLineFirst(body);
		if (lastSelectItem.getInputType().equals("html")) {
			return HtmlExtractor.doHtmlExtractor(lastSelectItem.getSelectBody(), currentRet);
		}else if (lastSelectItem.getInputType().equals("json")) {
			return JsonExtractor.doJsonExtractor(lastSelectItem.getSelectBody(), currentRet);
		}else if (lastSelectItem.getInputType().equals("string")) {
			return StringExtractor.doStringExtractor(lastSelectItem.getSelectBody(), currentRet);
		}
		return null;
	}
	
	public Object doComplexSelectLine(String body) throws Exception {
		String currentRet = body;
		for (int i = 0; i < this.selectItems.size(); i++) {
			SelectItem selectItem = selectItems.get(i);
			if (selectItem.getInputType().equals("html")) {
				currentRet = HtmlExtractor.doHtmlOneExtractor(selectItem.getSelectBody(), currentRet);
			}else if (selectItem.getInputType().equals("json")) {
				Object jsonRet = JsonExtractor.doJsonOneExtractor(selectItem.getSelectBody(), currentRet);
				if (jsonRet != null && (i != selectItems.size() -1 || resultType.size() > 0)) {
					currentRet = jsonRet.toString();
				}else {
					return jsonRet;
				}
			}else if (selectItem.getInputType().equals("string")) {
				currentRet = StringExtractor.doStringOneExtractor(selectItem.getSelectBody(), currentRet);
			}
		}
		
		Object finalResult = currentRet;
		for (String type : resultType) {
			if (finalResult == null) {
				break;
			}
			finalResult = Types.convertType(type, finalResult);
		}
		return finalResult;
	}
}
