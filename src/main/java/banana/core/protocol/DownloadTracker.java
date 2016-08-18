package banana.core.protocol;

import banana.core.exception.DownloadException;
import banana.core.request.PageRequest;
import banana.core.response.Page;

public interface DownloadTracker {
	
	Page sendRequest(PageRequest request);
	
	void stop() throws DownloadException;
}
