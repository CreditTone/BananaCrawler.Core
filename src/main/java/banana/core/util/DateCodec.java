package banana.core.util;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.alibaba.fastjson.parser.DefaultJSONParser;

public class DateCodec extends com.alibaba.fastjson.serializer.DateCodec {

	@Override
	protected <T> T cast(DefaultJSONParser parser, Type clazz, Object fieldName, Object val) {
		if (val instanceof String){
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (val.toString().contains("T") && val.toString().endsWith("Z")){
				try {
					return (T) dateFormat.parse(val.toString().replace("T", " ").replace("Z", ""));
				} catch (ParseException e) {
				}
			}else if (val.toString().contains("T") && val.toString().contains("+")){
				//2018-01-11T17:27:50.51210951+08:00
				String dateStr = val.toString().substring(0, 19).replace("T", " ");
				try {
					return (T) dateFormat.parse(dateStr);
				} catch (ParseException e) {
				}
			}
		}
		return super.cast(parser, clazz, fieldName, val);
	}
	
}
