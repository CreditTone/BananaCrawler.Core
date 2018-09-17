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
	
	public static void registerSum(Handlebars handlebars) {
		handlebars.registerHelper("sum", new Helper<Object>() {

			public Object apply(Object context, Options options) throws IOException {
				int sum = 0;
				int p0 = 0;
				for (int i = 0; i < options.params.length; i++) {
					p0 = Integer.parseInt(options.param(i).toString());
					sum += p0;
				}
				return sum;
			}
		});
	}
	
	public static void registerSubtract(Handlebars handlebars) {
		handlebars.registerHelper("subtract", new Helper<Object>() {

			public Object apply(Object context, Options options) throws IOException {
				int p0 = options.param(0);
				for (int i = 1; i < options.params.length; i++) {
					int p = Integer.parseInt(options.param(i).toString());
					p0 -= p;
				}
				return p0;
			}
		});
	}
	
	public static void registerMultiply(Handlebars handlebars) {
		handlebars.registerHelper("multiply", new Helper<Object>() {

			public Object apply(Object context, Options options) throws IOException {
				int product = Integer.parseInt(options.param(0).toString());
				int p0;
				for (int i = 1; i < options.params.length; i++) {
					p0 = Integer.parseInt(options.param(i).toString());
					product *= p0;
				}
				return product;
			}
		});
	}
	
	public static void registerDivide(Handlebars handlebars) {
		handlebars.registerHelper("divide", new Helper<Object>() {

			public Object apply(Object context, Options options) throws IOException {
				int product = Integer.parseInt(options.param(0).toString());
				int p0;
				for (int i = 1; i < options.params.length; i++) {
					p0 = Integer.parseInt(options.param(i).toString());
					product /= p0;
				}
				return product;
			}
		});
	}
	
	public static void registerRandomChar(Handlebars handlebars) {
		handlebars.registerHelper("randomChar", new Helper<Object>() {

			public Object apply(Object context, Options options) throws IOException {
				int number = Integer.parseInt(options.param(0).toString());
				StringBuilder sb = new StringBuilder();
				String chars = "abcdefghijklmnopqrstuvwxyz";
				int r = (int) (Math.random() * 100 % 2);
				for(int i = 0; i < number ; i++) {
					char c = 'A';
					if (r == 0) {
						c = (char) (0x4e00 + (int) (Math.random() * (0x9fa5 - 0x4e00 + 1)));
					}else {
						c = chars.charAt((int)(Math.random() * 26));
					}
					sb.append(c);
				}
				return sb.toString();
			}
		});
	}
}
