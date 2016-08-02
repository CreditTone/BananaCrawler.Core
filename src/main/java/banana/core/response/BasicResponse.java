package banana.core.response;

import java.util.Map;

import banana.core.request.BasicRequest;

public class BasicResponse {
	
	protected int statusCode;
	
	protected String contentType;
	
	protected Map<String,String> responseHeader;
	
	protected BasicRequest basicRequest;
	
	public int getStatus(){
		return statusCode;
	}
	
	public void setStatus(int status){
		statusCode = status;
	}

	public BasicRequest getRequest(){
		return basicRequest;
	}
	
	public void setRequest(BasicRequest request){
		basicRequest = request;
	}

	public Object getRequestAttribute(String attribute){
		return basicRequest.getAttribute(attribute);
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Map<String, String> getResponseHeader() {
		return responseHeader;
	}

	public void setResponseHeader(Map<String, String> responseHeader) {
		this.responseHeader = responseHeader;
	}
	
}
