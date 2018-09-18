package banana.core.extractor2.json.test;

import org.junit.Test;

import banana.core.extractor2.json.Condition;

public class ConditionTest {

	
	@Test
	public void test() throws Exception{
		System.out.println(new Condition("(id='1001')"));
		System.out.println(new Condition("(id > 1001)"));
		System.out.println(new Condition("(id='1001' && num < 3)"));
		System.out.println(new Condition("(age<10 || (name='enny' && gender='f'))"));
		System.out.println(new Condition("(name='enny' && (gender='f' || id < 10))"));
	}
	
	@Test
	public void testFilter() throws Exception{
		System.out.println(new Condition("(id='1001')").getFilter());
		System.out.println(new Condition("(id > 1001)").getFilter());
		System.out.println(new Condition("(id='1001' && num < 3)").getFilter());
		System.out.println(new Condition("(age<10 || (name='enny' && gender='f'))").getFilter());
		System.out.println(new Condition("(name='enny' && (gender='f' || id < 10))").getFilter());
	}
}
