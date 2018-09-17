package banana.core.extractor2.html;

public class Util {
	
	//兼容jquery语法
	public static String jqueryCompatibility(String jqueryCssSelector) {
		System.out.println("jqueryCompatibility input:" +jqueryCssSelector);
		char a = '_';
		char b = '_';
		char[] jqueryCssSelectorChars = jqueryCssSelector.toCharArray();
		for (int i = 0; i < jqueryCssSelectorChars.length; i++) {
			if (jqueryCssSelectorChars[i] == '[') {
				a = '[';
				continue;
			}
			if (jqueryCssSelectorChars[i] == ']') {
				a = '_';
				b = '_';
				continue;
			}
			if (jqueryCssSelectorChars[i] == '=') {
				b = '=';
				continue;
			}
			if ((jqueryCssSelectorChars[i] == '\'' || jqueryCssSelectorChars[i] == '"') && (a == '[' && b == '=')) {
				jqueryCssSelectorChars[i] = '%';
				continue;
			}
		}
		String jsoupCssSelector = new String(jqueryCssSelectorChars).replaceAll("%", "");
		jsoupCssSelector = jsoupCssSelector.replaceAll("\\(\\s*'", "(").replaceAll("'\\s*\\)", ")");
		System.out.println("jqueryCompatibility output:" +jsoupCssSelector);
		return jsoupCssSelector;
	}
	
	public static void main(String[] args) {
		System.out.println(jqueryCompatibility("a[class=right-arrow iconfont icon-btn_right]"));
	}
	
}
