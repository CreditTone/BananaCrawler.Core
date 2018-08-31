package banana.core.actions;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import banana.core.util.HttpUtils;

public class OCR {
	
	

	/**
	 * 重庆尖叫网络科技有限公司
	 * @param body
	 * @param type 图形验证码类型（n4：4位纯数字，n5：5位纯数字，n6:6位纯数字，e4：4位纯英文，e5：5位纯英文，e6：6位纯英文，ne4：4位英文数字，ne5：5位英文数字，ne6：6位英文数字），请准确填写，以免影响识别准确性。（其他类型，请搜索：图形验证码识别-复杂图形）
	 * @return
	 */
	public static String ocrDecode(byte[] imageBody,String type) {
		String base64 = java.util.Base64.getEncoder().encodeToString(imageBody); 
		String host = "http://txyzmsb.market.alicloudapi.com";
		String path = "/yzm";
		String method = "POST";
		String appcode = "10d26218b7124b28b068f70bae071348";
		Map<String, String> headers = new HashMap<String, String>();
	    //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
	    headers.put("Authorization", "APPCODE " + appcode);
	    //根据API的要求，定义相对应的Content-Type
	    headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	    Map<String, String> querys = new HashMap<String, String>();
	    Map<String, String> bodys = new HashMap<String, String>();
	    bodys.put("v_pic", base64);
	    bodys.put("v_type", type);
	    try {
	    		HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
	    		String content = EntityUtils.toString(response.getEntity(),"UTF-8".toString());
	    		JSONObject obj = JSON.parseObject(content);
	    		return obj.getString("v_code");
	    }catch(Exception e) {
	    		e.printStackTrace();
	    }
		return null;
	}
	
	
}
