package banana.core.extractor2.string;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringExtractor {
	
	public static List<Object> doStringExtractor(String array_define,String val) {
		Pattern pattern = Pattern.compile(array_define);
		Matcher matcher = pattern.matcher(val);
		List<Object> result = new ArrayList<Object>();
		while(matcher.find()) {
			if (matcher.groupCount() > 0) {
				result.add(matcher.group(1));
			}else {
				result.add(matcher.group());
			}
		}
		return result;
	}
	
	public static String doStringOneExtractor(String parseConfig,String val) {
		Pattern pattern = Pattern.compile(parseConfig);
		Matcher matcher = pattern.matcher(val);
		if(matcher.find()) {
			if (matcher.groupCount() > 0) {
				return matcher.group(1);
			}else {
				return matcher.group();
			}
		}
		return null;
	}
	
}
