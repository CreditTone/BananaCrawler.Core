package banana.core.response;

import java.io.InputStream;

public class StreamResponse extends BasicResponse {
	
	protected InputStream in;

	public InputStream getIn() {
		return in;
	}

	public void setIn(InputStream in) {
		this.in = in;
	}
	
}
