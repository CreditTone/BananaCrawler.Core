package banana.core.download;

import java.io.Closeable;

import banana.core.request.PageRequest;
import banana.core.response.Page;





public abstract class PageDownloader implements Closeable{
	
    /**
     * Downloads web pages and store in Page object.
     *
     * @param request
     * @param task
     * @return page
     */
    public abstract Page download(PageRequest request);
    
    public abstract boolean supportJavaScript();
    
    public abstract void open();

    public abstract void setTimeout(int second);
    
}
