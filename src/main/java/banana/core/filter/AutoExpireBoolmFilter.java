package banana.core.filter;

public class AutoExpireBoolmFilter implements Filter{
	
	private long expireTime;  

	@Override
	public void add(String value) {
	}

	@Override
	public boolean contains(String value) {
		return false;
	}
	
}
