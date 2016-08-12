package banana.core.request;

import banana.core.processor.PageProcessor;

/**
 * 网页类型Request的表示。
 *
 */
public final class PageRequest extends HttpRequest{
	
	/**
	 * 网页编码
	 */
	public enum PageEncoding{
    	UTF8,
    	GBK,
    	GB2312,
  //  	AUTO;
    }

    private PageEncoding pageEncoding;
    
    
    private String processor ;
    
    
    protected PageRequest(){
    	type = type.PAGE_REQUEST;
    	method = Method.GET;
    }
    
	
	    
	public String getProcessor() {
		return processor;
	}

	public void setProcessor(String processorCls) {
		if(processorCls == null){
			throw new NullPointerException("PageReuqest的PageProcessor不能为Null");
		}else{
			this.processor = processorCls;
		}
	}
	

	public PageEncoding getPageEncoding() {
		return pageEncoding;
	}

	public void setPageEncoding(PageEncoding pageEncoding) {
		if(pageEncoding != null){
			this.pageEncoding = pageEncoding;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PageRequest other = (PageRequest) obj;
		if (method != other.method)
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PageRequest [url=" + url + ", method=" + method + ", pageEncoding=" + pageEncoding + ", requestParams="
				+ requestParams + ", headers=" + headers + ", processor=" + processor + ", attributes=" + attributes 
				+ ", type=" + type + ", getPriority()="
				+ getPriority() + "]";
	}



}
