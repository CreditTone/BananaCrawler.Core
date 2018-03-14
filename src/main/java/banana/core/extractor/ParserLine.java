package banana.core.extractor;

public class ParserLine {
	
	private String parserLine;
	
	private String expectType;
	
	private String jsoup_cssSelector;
	
	private String jsoup_attr;
	
	private String jsoup_regular;
	
	public ParserLine(String parseStr) {
		super();
		if (parseStr.contains("->")) {
			String[] pp = parseStr.split("->");
			this.parserLine = pp[0];
			this.expectType = pp[1];
		}else {
			this.parserLine = parseStr;
		}
		String[] jsoup_expr = parserLine.split(";");
		jsoup_cssSelector = jsoup_expr[0].trim();
		if (jsoup_expr.length > 1) {
			jsoup_attr = jsoup_expr[1].trim();
		}
		if (jsoup_expr.length > 2) {
			jsoup_regular = jsoup_expr[2].trim();
		}
	}
	
	public String getJsoup_cssSelector() {
		return jsoup_cssSelector;
	}

	public void setJsoup_cssSelector(String jsoup_cssSelector) {
		this.jsoup_cssSelector = jsoup_cssSelector;
	}

	public String getJsoup_attr() {
		return jsoup_attr;
	}

	public void setJsoup_attr(String jsoup_attr) {
		this.jsoup_attr = jsoup_attr;
	}

	public String getJsoup_regular() {
		return jsoup_regular;
	}

	public void setJsoup_regular(String jsoup_regular) {
		this.jsoup_regular = jsoup_regular;
	}

	public String getParserLine() {
		return parserLine;
	}

	public void setParserLine(String parserLine) {
		this.parserLine = parserLine;
	}

	public String getExpectType() {
		return expectType;
	}

	public void setExpectType(String expectType) {
		this.expectType = expectType;
	}
	
	public boolean isContextParse() {
		return parserLine.contains("{{") && parserLine.contains("}}");
	}
	
	public boolean isContextParseObject() {
		return parserLine.startsWith("{{") && parserLine.endsWith("}}");
	}
	
	public boolean isExpectTypeString() {
		if (expectType == null || expectType.equals("string")) {
			return true;
		}
		return false;
	}
	
	
	public boolean isExpectTypeInt() {
		if (expectType != null && expectType.equals("int")) {
			return true;
		}
		return false;
	}
	
	public boolean isExpectTypeFloat() {
		if (expectType != null && expectType.equals("float")) {
			return true;
		}
		return false;
	}
	
	public boolean isExpectTypeDouble() {
		if (expectType != null && expectType.equals("double")) {
			return true;
		}
		return false;
	}
	
	public boolean isExpectTypeBoolean() {
		if (expectType != null && expectType.equals("bool")) {
			return true;
		}
		return false;
	}
}
