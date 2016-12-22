package banana.core.response;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;

import banana.core.request.HttpRequest;

public class HttpResponse {
	
	protected int statusCode;
	
	protected String contentType;
	
	protected Map<String,String> responseHeader;
	
	protected HttpRequest basicRequest;
	
	protected String driverId;
	
	public HttpResponse(){}
	
	public HttpResponse(HttpRequest basicRequest, org.apache.http.HttpResponse response) {
		setStatus(response.getStatusLine().getStatusCode());
		setRequest(basicRequest);
		setContentType(response.getEntity().getContentType().getValue());
		Header []headers = response.getAllHeaders();
		Map<String,String> headerCopy = new HashMap<String,String>();
		for (int i = 0; i < headers.length; i++) {
			headerCopy.put(headers[i].getName(), headers[i].getValue());
		}
		setResponseHeader(headerCopy);
	}
	
	public int getStatus(){
		return statusCode;
	}
	
	public void setStatus(int status){
		statusCode = status;
	}

	public HttpRequest getRequest(){
		return basicRequest;
	}
	
	public void setRequest(HttpRequest request){
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

	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}
	
}
