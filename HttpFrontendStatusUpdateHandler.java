import java.io.IOException;
import java.net.URLDecoder;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class HttpFrontendStatusUpdateHandler implements HttpRequestHandler {
	private static Logger log = Logger.getLogger(HttpFrontendStatusUpdateHandler.class);

	public void handle(final HttpRequest request, final HttpResponse response,
			final HttpContext context) throws HttpException, IOException {
		
		log.info("request: " + request.getRequestLine());

		String method = request.getRequestLine().getMethod()
				.toUpperCase(Locale.ENGLISH);

		if (method.equals("POST")) {
			final String target = request.getRequestLine().getUri();
			Pattern p_uri = Pattern.compile("/status/update\\?status=(.*)$");
			Pattern p_body = Pattern.compile("/status/update");
			Matcher m_uri = p_uri.matcher(target);
			Matcher m_body = p_body.matcher(target);
			String tweet = null;
			if (m_uri.find()) {
				tweet = m_uri.group(1);
			} else if (m_body.find()) {
				HttpEntity entity = ((HttpEntityEnclosingRequest) request)
						.getEntity();
				String entityContent = EntityUtils.toString(entity);
				Pattern p = Pattern.compile("status=(.*)$");
				Matcher m = p.matcher(entityContent);
				if (m.find()) {
					tweet = m.group(1);
				}
			} else {
				response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
				return;
			}

			if (tweet != null) {
				tweet = URLDecoder.decode(tweet, "UTF-8");
				Pattern p = Pattern.compile("#(\\S*)");
				Matcher m = p.matcher(tweet);

				while (m.find()) {
					String hash = m.group(1);
					JSONHashTweet content = new JSONHashTweet(hash, tweet);
					Util.doPost(SSM.getInstance().getServer(hash), "/addtweet",
							"t", Util.g.toJson(content, JSONHashTweet.class));
				}
				response.setStatusCode(HttpStatus.SC_OK);
			} else {
				response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
			}
		} else {
			throw new MethodNotSupportedException(method
					+ " method not supported\n");
		}

	}

}