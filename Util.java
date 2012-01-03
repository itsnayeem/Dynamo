import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class Util {
	private static Logger log = Logger.getLogger(WebServer.class);

	public static Gson g = new Gson();

	public static String doGet(Server s, String path) {
		String url = "http://" + s.getIP() + ":" + s.getPort() + path;
		log.info("Doing get on url: " + url);
		String entityContent = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse r = null;
		try {
			r = httpclient.execute(httpget);
		} catch (IOException e) {
		}
		HttpEntity entity = r.getEntity();
		try {
			entityContent = EntityUtils.toString(entity);
		} catch (ParseException e) {
		} catch (IOException e) {
		}
		return entityContent;
	}
	
	public static void doPost(Server s, String path, String key, String content) {
		String url = "http://" + s.getIP() + ":" + s.getPort() + path;
		log.info("Doing post to url: " + url + "; " + key + ": " + content);
		HttpClient httpclient = new DefaultHttpClient();

		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair(key, content));
		UrlEncodedFormEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(formparams, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		HttpPost httppost = null;
		httppost = new HttpPost(url);

		httppost.setEntity(entity);
		try {
			httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}
	}
}
