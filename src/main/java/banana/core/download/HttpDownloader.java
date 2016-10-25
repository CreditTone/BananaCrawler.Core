package banana.core.download;

import java.io.Closeable;

import banana.core.request.BinaryRequest;
import banana.core.request.PageRequest;
import banana.core.response.Page;
import banana.core.response.StreamResponse;


public abstract class HttpDownloader implements Closeable{
	
    public abstract Page download(PageRequest request);
    
    public abstract StreamResponse downloadBinary(BinaryRequest request);
    
    public abstract boolean supportJavaScript();
    
    public abstract void open();

    public abstract void setTimeout(int second);
    
}
