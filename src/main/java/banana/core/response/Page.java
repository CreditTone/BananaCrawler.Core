package banana.core.response;


/**
 * 所有页面的抽象表示
 */
public class Page extends BasicResponse{
	
	protected String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
