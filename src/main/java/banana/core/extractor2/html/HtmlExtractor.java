package banana.core.extractor2.html;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlExtractor {
	
	private static Logger logger = Logger.getLogger(HtmlExtractor.class);
	
	//解析多个结果
	public static List<Object> doHtmlExtractor(String array_define,String val) throws Exception {
		Document doc = Jsoup.parse(val);
		Elements elements = doXpath(array_define , new Elements(doc)) ;
		if (elements == null || elements.isEmpty()) {
			return null;
		}
		List<Object> result = new ArrayList<Object>();
		for (Element element : elements) {
			result.add(element.html());
		}
		return result;
	}
	
	//解析单个结果
	public static String doHtmlOneExtractor(String parseConfig,String val) throws Exception {
		Document doc = Jsoup.parse(val);
		try {
			return queryValue(parseConfig, doc.children());
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String queryValue(String query, Elements element) throws Exception {
		HtmlSelector selector = new HtmlSelector(query);
		Elements b = element;
		String text = null;
		if (selector.getXpath().length() > 0) {
			b = doXpath(selector.getXpath(), b);
			text = b.html();
		}
		if (selector.getAttr() != null) {
			text = b.attr(selector.getAttr());
			if (text != null && text.trim().isEmpty()) {
				text = null;
			}
		}
		return text;
	}
	
	public static Elements doXpath(String xpath, Elements element) throws Exception {
		xpath = Util.jqueryCompatibility(xpath);
		if (xpath.contains(" [") && xpath.contains("]")) {
			List<String> xpathArr = new ArrayList<>();
			while(true) {
				int start = xpath.indexOf(" [");
				int end = xpath.indexOf("]", start) +1;
				if (start != -1) {
					if (start > 0) {
						xpathArr.add(xpath.substring(0, start));
					}
					xpathArr.add(xpath.substring(start, end).trim());
					xpath = xpath.substring(end);
				}else {
					if (!xpath.trim().isEmpty()) {
						xpathArr.add(xpath);
					}
					break;
				}
			}
			Elements b = element;
			for (String xpathItem: xpathArr) {
				if (xpathItem.startsWith("[") && xpathItem.endsWith("]")) {
					int index = Integer.parseInt(xpathItem.substring(1, xpathItem.length()-1));
					if (index == -1) {
						b = new Elements(b.last());
					}else {
						b = new Elements(b.get(index));
					}
				}else {
					b = b.select(xpathItem);
				}
			}
		}else {
			return element.select(xpath);
		}
		return null;
	}
}
