package com.autonavi.audit.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * 
 * @author chunsheng.zhang
 *
 */
public class HttpClientUtil {

    public static final String UTF_8 = "UTF-8";

    public static final String GBK = "GBK";

    private static DefaultHttpClient httpclient;

    protected static final Logger logger = Logger.getLogger(HttpClientUtil.class);



    static {
        HttpParams params = new BasicHttpParams();
        params.setParameter("http.connection.timeout", 20000);
        params.setParameter("http.socket.timeout", 20000);
        params.setParameter("http.protocol.element-charset", UTF_8);
        params.setParameter("http.protocol.content-charset", UTF_8);


        final SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http",  80,PlainSocketFactory.getSocketFactory()));
        final ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(registry);
        httpclient = new DefaultHttpClient(manager, params);
    }



    public static String get(String uri, String responseEncode) throws IOException {
        HttpGet httpGet = new HttpGet(uri);
        HttpResponse httpResponse = httpclient.execute(httpGet);

        String charset = responseEncode == null ? EntityUtils.getContentCharSet(httpResponse.getEntity()) : responseEncode;
        String s = EntityUtils.toString(httpResponse.getEntity(), charset);
        //httpResponse.getEntity().consumeContent();
        return s;
    }



    public static String getRedirectUrl(String uri, String responseEncode) throws IOException {
        HttpGet httpGet = new HttpGet(uri);
        httpGet.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
        HttpResponse httpResponse = httpclient.execute(httpGet);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        //httpResponse.getEntity().consumeContent();
        if (statusCode == 301 || statusCode == 302) {
            return httpResponse.getFirstHeader("Location").getValue();
        } else {
            return null;
        }

    }




    public static String get(String url, Map<String, String> params, String encode, String responseEncode) throws IOException {
        return get(addParams(url, params, encode), responseEncode);
    }



    public static String post(String uri, Map<String, String> m, String encoding) throws IOException {
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(getParamList(m), encoding);
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(entity);
        HttpResponse httpResponse = httpclient.execute(httpPost);
        String charset = encoding == null ? EntityUtils.getContentCharSet(httpResponse.getEntity()) : encoding;
        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String s = EntityUtils.toString(httpResponse.getEntity(), charset);
            //httpResponse.getEntity().consumeContent();
            return s;
        } else {
            //httpResponse.getEntity().consumeContent();
            httpPost.abort();
            return null;
        }
    }

    public static void postAsyn(String uri, Map<String, String> m, String encoding) {
    	final String address = uri;
    	final Map<String,String> params = m;
    	final String coding = encoding;
    	logger.info("send content："+ m.toString());
    	new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("send now!");
					logger.info("back message："+post(address, params, coding));
				} catch (IOException e) {
					logger.info(e.getMessage());
				}
			}
		}).start();
    }

    public static List<NameValuePair> getParamList(Map<String, String> params) {
        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            qparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return qparams;
    }



    public static String addParams(String baseURI, Map<String, String> params, String encode) {
        if (baseURI == null) return null;
        String paramStr = URLEncodedUtils.format(getParamList(params), encode);
        char sp = (baseURI.indexOf('?') != -1) ? '&' : '?';
        return baseURI + sp + paramStr;
    }

    
    
    public static String getWithoutException(String uri, String responseEncode){
        HttpGet httpGet = new HttpGet(uri);
        HttpResponse httpResponse;
        String s = null;
		try {
			httpResponse = httpclient.execute(httpGet);
			String charset = responseEncode == null ? EntityUtils.getContentCharSet(httpResponse.getEntity()) : responseEncode;
			s = EntityUtils.toString(httpResponse.getEntity(), charset);
		} catch (ClientProtocolException e) {
			logger.info("ClientProtocolException");
			e.printStackTrace();
		} catch (IOException e) {
			logger.info("IOException");
			e.printStackTrace();
		}
		return s;
    }
    
    
    
	/**
	 * 执行一个HTTP GET请求，返回请求响应的HTML
	 * 
	 * @param url
	 *            请求的URL地址
	 * @param queryString
	 *            请求的查询参数,可以为null
	 * @param charset
	 *            字符集
	 * @param pretty
	 *            是否美化
	 * @return 返回请求响应的HTML
	 */
	public static String doGet(String url, String queryString, String charset,
			boolean pretty) {
		StringBuffer response = new StringBuffer();
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(url);
		try {
			if (queryString != null && !queryString.equals(""))
				// 对get请求参数做了http请求默认编码，好像没有任何问题，汉字编码后，就成为%式样的字符串
				method.setQueryString(URIUtil.encodeQuery(queryString));
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(method.getResponseBodyAsStream(),
								charset));
				String line;
				while ((line = reader.readLine()) != null) {
					if (pretty)
						response.append(line).append(
								System.getProperty("line.separator"));
					else
						response.append(line);
				}
				reader.close();
			}
		} catch (URIException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}
		return response.toString();
	}
}
