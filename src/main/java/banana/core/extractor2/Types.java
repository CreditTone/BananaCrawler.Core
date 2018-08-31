package banana.core.extractor2;

import java.math.BigDecimal;

import org.jsoup.Jsoup;

public class Types {

	
	public static Object convertType(String ty,Object val) {
		switch (ty) {
		case "int":
			return toInt(val);
		case "float":
			return toFloat(val);
		case "boolean":
			return toBoolean(val);
		case "htmlText":
			return toHtmlText(val);
		}
		return null;
	}
	
	public static Object toInt(Object val) {
		String str = val.toString();
		if (str.contains("e") || str.contains("E")) {
			BigDecimal bd = new BigDecimal(str);
			if (bd.toPlainString().contains(".")) {
				return (int)Double.parseDouble(bd.toPlainString());
			}
			return Integer.parseInt(bd.toPlainString());
		}
		return Integer.parseInt(str);
	}
	
	public static Object toFloat(Object val) {
		String str = val.toString();
		if (str.contains("e") || str.contains("E")) {
			BigDecimal bd = new BigDecimal(str);
			return Double.parseDouble(bd.toPlainString());
		}
		return Double.parseDouble(str);
	}
	
	public static Object toBoolean(Object val) {
		String str = val.toString();
		return Boolean.parseBoolean(str);
	}
	
	public static Object toString(Object val) {
		return val.toString();
	}
	
	public static Object toHtmlText(Object val) {
		return Jsoup.parse(val.toString()).text().trim();
	}
	
}
