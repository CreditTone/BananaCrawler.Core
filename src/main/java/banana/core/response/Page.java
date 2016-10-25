package banana.core.response;

import java.io.IOException;

import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import banana.core.request.PageRequest;

/**
 * 所有页面的抽象表示
 */
public class Page extends HttpResponse{
	
	public Page(PageRequest basicRequest, org.apache.http.HttpResponse response) throws ParseException, IOException {
		super(basicRequest, response);
		String content = EntityUtils.toString(response.getEntity(),basicRequest.getPageEncoding().toString());
		setContent(content);
	}

	protected String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
