package banana.core;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.TagType;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.TypeSafeTemplate;

public final class EscapeTemplate implements Template {
	
	private final Template template;
	
	public EscapeTemplate(Template template) {
		this.template = template;
	}

	@Override
	public void apply(Object context, Writer writer) throws IOException {
		template.apply(context, writer);
	}

	@Override
	public String apply(Object context) throws IOException {
		if (!template.text().contains("{{")){
			return template.text();
		}
		return StringEscapeUtils.unescapeHtml(template.apply(context));
	}

	@Override
	public void apply(Context context, Writer writer) throws IOException {
		template.apply(context, writer);
	}

	@Override
	public String apply(Context context) throws IOException {
		if (!template.text().contains("{{")){
			return template.text();
		}
		return StringEscapeUtils.unescapeHtml(template.apply(context));
	}

	@Override
	public String text() {
		return template.text();
	}

	@Override
	public String toJavaScript() {
		return template.toJavaScript();
	}

	@Override
	public <T, S extends TypeSafeTemplate<T>> S as(Class<S> type) {
		return template.as(type);
	}

	@Override
	public <T> TypeSafeTemplate<T> as() {
		return template.as();
	}

	@Override
	public List<String> collect(TagType... tagType) {
		return template.collect(tagType);
	}

	@Override
	public List<String> collectReferenceParameters() {
		return template.collectReferenceParameters();
	}

	@Override
	public String filename() {
		return template.filename();
	}

	@Override
	public int[] position() {
		return template.position();
	}

}
