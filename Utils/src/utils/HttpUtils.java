package utils;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class HttpUtils {

	public static void test_1() throws Exception {
		
		HttpGet httpPost = new HttpGet("http://10.21.157.228:18080/PerfAutoTest");
		httpPost.addHeader("Connection", "keep-alive");
		// httpPost.addHeader("Connection", "close");
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		CloseableHttpResponse response = null;
		for (int i = 0; i < 100; i++) {
			response = httpClient.execute(httpPost);
			String result = EntityUtils.toString(response.getEntity(), "utf-8");
			System.out.println(result);
		}
	}
}
