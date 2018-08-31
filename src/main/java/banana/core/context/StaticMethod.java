package banana.core.context;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public final class StaticMethod {
	
	private static Logger logger = Logger.getLogger(StaticMethod.class);
	
	public static void registerGt(Handlebars handlebars) {
		handlebars.registerHelper("gt", new Helper<Object>() {

			public Object apply(Object context, Options options) throws IOException {
				try {
					float p0 = Float.parseFloat(((Object) options.param(0)).toString());
					if (options.param(1) == null) {
						return false;
					}
					float p1 = Float.parseFloat(((Object) options.param(1)).toString());
					return p0 > p1;
				} catch (Exception e) {
					logger.warn(options.context.model() + e.getMessage() + ",好像有东西没解析到哦");
				}
				return false;
			}
		});
	}
	
	public static void registerLt(Handlebars handlebars) {
		handlebars.registerHelper("lt", new Helper<Object>() {

			public Object apply(Object context, Options options) throws IOException {
				try {
					float p0 = Float.parseFloat(((Object) options.param(0)).toString());
					if (options.param(1) == null) {
						return false;
					}
					float p1 = Float.parseFloat(((Object) options.param(1)).toString());
					return p0 < p1;
				} catch (Exception e) {
					logger.warn(options.context.model() + e.getMessage() + ",好像有东西没解析到哦");
				}
				return false;
			}
		});
	}
	
	public static void registerHasPrefix(Handlebars handlebars) {
		handlebars.registerHelper("hasPrefix", new Helper<Object>() {

			public Object apply(Object context, Options options) throws IOException {
				String a = options.param(0);
				String b = options.param(1);
				return a.startsWith(b);
			}
		});
	}
	
	public static void registerHasSuffix(Handlebars handlebars) {
		handlebars.registerHelper("hasSuffix", new Helper<Object>() {

			public Object apply(Object context, Options options) throws IOException {
				String a = options.param(0);
				String b = options.param(1);
				return a.endsWith(b);
			}
		});
	}
}
