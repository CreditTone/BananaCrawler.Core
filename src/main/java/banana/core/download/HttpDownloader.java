package banana.core.download;

import java.io.Closeable;

import banana.core.download.impl.HttpsProxy;
import banana.core.request.BinaryRequest;
import banana.core.request.Cookies;
import banana.core.request.PageRequest;
import banana.core.response.Page;
import banana.core.response.StreamResponse;


public interface HttpDownloader extends Closeable{
	
    public Page download(PageRequest request);
    
    public StreamResponse downloadBinary(BinaryRequest request);
    
    public boolean supportJavaScript();
    
    public void open();

    public void setTimeout(int second);
    
    public void setProxy(HttpsProxy proxy);
    
    public void injectCookies(Cookies cookies);
    
    public Cookies getCookies();
}
