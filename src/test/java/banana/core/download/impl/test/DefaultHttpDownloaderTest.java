package banana.core.download.impl.test;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import banana.core.download.impl.DefaultHttpDownloader;
import banana.core.request.HttpRequest;
import banana.core.request.PageRequest;
import banana.core.request.RequestBuilder;
import banana.core.response.Page;

public class DefaultHttpDownloaderTest {
	
	private DefaultHttpDownloader defaultHttpDownloader = null;
	
	@Before
	public void init() throws IOException{
		defaultHttpDownloader = new DefaultHttpDownloader();
	}
	
	@Test
	public void test1() throws Exception{
		HttpRequest httpRequest = RequestBuilder.custom().setUrl("http://m.amap.com/service/poi/longlat.json?keywords=%E7%BE%8E%E9%A3%9F&latitude=36.697855&cluster_state=5&pagenum=10&client_network_class=4&uuid=1dccc8e4-d71c-4ba5-ba2e-b80ec29eea11&user_loc=118.484693%2C36.697855&notsort=true&classify_data=query_type%3Drqbxy%3Brange%3D2000%3Bis_uma%3Dtrue%3Bcur_adcode%3D110000%2Bfilter_keywords%3D%E7%BE%8E%E9%A3%9F%2Bsort_rule%3D0%3Breserved_keywords%3Dtrue&longitude=118.484693").build();
		Page page = defaultHttpDownloader.download((PageRequest) httpRequest);
		System.out.println(page.getContent());
	}
}
