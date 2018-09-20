package banana.core.extractor2.json.test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import banana.core.extractor2.json.JsonExtractor;

public class JSONParseTest {
	
	private String json = null;
	
	@Before
	public void init() throws IOException{
		String filepath = this.getClass().getClassLoader().getResource("").getPath() + "banana/core/extractor2/json/test/";
		json = FileUtils.readFileToString(new File(filepath + "testdata.json"));
	}
	
	@Test
	public void testDoJsonExtractor() throws Exception{
		List<Object> results = JsonExtractor.doJsonExtractor("poi_list", json);
		System.out.println(results);
	}
	
	
	@Test
	public void testIndex() throws Exception{
		Object result = JsonExtractor.doJsonOneExtractor("[1]", "['1','2','3']");
		System.out.println(result);
	}
	
	@Test
	public void testWhere() throws Exception{
		Object result = JsonExtractor.doJsonOneExtractor("poi_list.[2].domain_list.(id='1001').[0].value", json);
		System.out.println(result);
	}

}
