package banana.core.extractor2.html;

public class HtmlSelector {

	private String xpath;
	private String attr;
	
	
	public HtmlSelector(String v) {
		String[] tks = v.split(";");
		this.xpath = tks[0];
		if (tks.length > 1 && !tks[1].trim().isEmpty()) {
			this.attr = tks[1];
		}
	}
	
	public String getXpath() {
		return xpath;
	}


	public void setXpath(String xpath) {
		this.xpath = xpath;
	}


	public String getAttr() {
		return attr;
	}


	public void setAttr(String attr) {
		this.attr = attr;
	}
	
	
}
