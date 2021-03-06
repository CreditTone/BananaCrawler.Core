package banana.core.request;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

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

    private PageEncoding pageEncoding = PageEncoding.UTF8;;
    
    
    
    public PageRequest(){
    	method = Method.GET;
    }
    
	public PageRequest(String url, String processor) {
		this.url = url;
		this.processor = processor;
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
	public void write(DataOutput out) throws IOException {
		super.write(out);
		out.writeUTF(pageEncoding.name());
	}


	@Override
	public void readFields(DataInput in) throws IOException {
		super.readFields(in);
		String pageEncodingName = in.readUTF();
		if (pageEncodingName.equals(PageEncoding.UTF8.name())){
			pageEncoding = PageEncoding.UTF8;
		}else if(pageEncodingName.equals(PageEncoding.GBK.name())){
			pageEncoding = PageEncoding.GBK;
		}else if(pageEncodingName.equals(PageEncoding.GB2312.name())){
			pageEncoding = PageEncoding.GB2312;
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
	
}
