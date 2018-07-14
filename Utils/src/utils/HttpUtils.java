package utils;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class HttpUtils {

	public static void test_1() throws Exception {

		HttpGet httpPost = new HttpGet("http://10.21.157.228:18080/PerfAutoTest");
		httpPost.addHeader("Connection", "keep-alive");
		httpPost.addHeader("Connection", "close");
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		CloseableHttpResponse response = null;
		for (int i = 0; i < 100; i++) {
			response = httpClient.execute(httpPost);
			String result = EntityUtils.toString(response.getEntity(), "utf-8");
			System.out.println(result);
		}
	}

	public static HttpClient httpClient = HttpClientBuilder.create().build();

	public static String doGet(String url, Map<String, String> headers) {

		String result = "";

		try {
			HttpGet httpGet = new HttpGet(url);

			for (Entry<String, String> entry : headers.entrySet()) {
				httpGet.addHeader(entry.getKey(), entry.getValue());
			}

			HttpResponse closeableHttpResponse = httpClient.execute(httpGet);
			result = EntityUtils.toString(closeableHttpResponse.getEntity(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String doPost(String url, Map<String, String> headers, String body) {

		String result = "";

		try {
			HttpPost httpPost = new HttpPost(url);

			for (Entry<String, String> entry : headers.entrySet()) {
				httpPost.addHeader(entry.getKey(), entry.getValue());
			}

			StringEntity entity = new StringEntity(body, "UTF-8");

			httpPost.setEntity(entity);
			HttpResponse closeableHttpResponse = httpClient.execute(httpPost);
			result = EntityUtils.toString(closeableHttpResponse.getEntity(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static String doPut(String url, Map<String, String> headers, String body) {

		String result = "";

		try {
			HttpPut httpPut = new HttpPut(url);

			for (Entry<String, String> entry : headers.entrySet()) {
				httpPut.addHeader(entry.getKey(), entry.getValue());
			}

			StringEntity entity = new StringEntity(body, "UTF-8");

			httpPut.setEntity(entity);
			HttpResponse closeableHttpResponse = httpClient.execute(httpPut);
			result = EntityUtils.toString(closeableHttpResponse.getEntity(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}
