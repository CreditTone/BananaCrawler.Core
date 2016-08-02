package banana.core.protocol;

import banana.core.request.PageRequest;
import banana.core.response.Page;

public interface DownloadTracker {
	
	Page sendRequest(PageRequest request);
	
	void setFetchSize(int fetchsize);
	
	void stop();
}
