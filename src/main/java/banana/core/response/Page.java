package banana.core.response;

import java.io.IOException;

import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;

import banana.core.request.PageRequest;

/**
 * 所有页面的抽象表示
 */
public class Page extends HttpResponse{
	
	public Page(){}
	
	public Page(PageRequest basicRequest, org.apache.http.HttpResponse response) throws ParseException, IOException {
		super(basicRequest, response);
		String content = EntityUtils.toString(response.getEntity(),basicRequest.getPageEncoding().toString());
		setContent(content);
	}

	protected String content;
	
	protected String title;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		if (content!=null && content.contains("<title")) {
			title = Jsoup.parse(content).title();
		}
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return content;
	}
	
}
