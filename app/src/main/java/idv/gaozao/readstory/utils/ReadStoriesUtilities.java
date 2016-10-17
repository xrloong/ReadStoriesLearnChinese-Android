package idv.gaozao.readstory.utils;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


public class ReadStoriesUtilities {
	private static final String HOST = "read-stories.appspot.com";
	private static final String URL_FORMAT_ARTICLE_LIST = "https://%s.%s/article/";
	private static final String URL_FORMAT_ARTICLE = "https://%s.%s/article/%s";
	private static final int FORMAT_VERSION = 1;

	public static String getUrlArticleList(String languageCode) {
		return String.format(URL_FORMAT_ARTICLE_LIST, FORMAT_VERSION, HOST) + "?" + String.format("lang=%s", languageCode);
	}

	public static String getUrlArticle(String key) {
		return String.format(URL_FORMAT_ARTICLE, FORMAT_VERSION, HOST, key);
	}

	public static String getOnlinePage(String url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		String retSrc = "";
		try {
			HttpResponse response = client.execute(request);
			retSrc = EntityUtils.toString(response.getEntity());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return retSrc;
	}
}
