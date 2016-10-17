package idv.gaozao.readstory.utils;

import java.io.IOException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
		OkHttpClient client = getUnsafeOkHttpClient();

		if(client == null) {
			client = new OkHttpClient();
		}

		Request request = new Request.Builder()
				.url(url)
				.get()
				.build();
		Call call = client.newCall(request);
		try {
			Response response = call.execute();
			String s = response.body().string();
			return s;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	private static OkHttpClient getUnsafeOkHttpClient() {
		try {
			// Create a trust manager that does not validate certificate chains
			final TrustManager[] trustAllCerts = new TrustManager[] {
					new X509TrustManager() {
						@Override
						public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
						}

						@Override
						public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
						}

						@Override
						public java.security.cert.X509Certificate[] getAcceptedIssuers() {
							return new java.security.cert.X509Certificate[]{};
						}
					}
			};

			// Install the all-trusting trust manager
			final SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
			// Create an ssl socket factory with our all-trusting manager
			final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

			OkHttpClient.Builder builder = new OkHttpClient.Builder();
			builder.sslSocketFactory(sslSocketFactory);
			builder.hostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});

			OkHttpClient okHttpClient = builder.build();
			return okHttpClient;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
