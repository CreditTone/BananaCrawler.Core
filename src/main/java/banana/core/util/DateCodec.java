package banana.core.util;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.alibaba.fastjson.parser.DefaultJSONParser;

public class DateCodec extends com.alibaba.fastjson.serializer.DateCodec {

	@Override
	protected <T> T cast(DefaultJSONParser parser, Type clazz, Object fieldName, Object val) {
		if (val instanceof String && val.toString().contains("T") && val.toString().endsWith("Z")){
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				return (T) dateFormat.parse(val.toString().replace("T", " ").replace("Z", ""));
			} catch (ParseException e) {
			}
		}
		return super.cast(parser, clazz, fieldName, val);
	}

}
