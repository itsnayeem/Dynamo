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

public class HttpBackendAddTweetHandler implements HttpRequestHandler {
	private static Logger log = Logger
			.getLogger(HttpBackendAddTweetHandler.class);

	public void handle(final HttpRequest request, final HttpResponse response,
			final HttpContext context) throws HttpException, IOException {

		log.info("request: " + request.getRequestLine());

		String method = request.getRequestLine().getMethod()
				.toUpperCase(Locale.ENGLISH);

		if (method.equals("POST")) {
			String tweet = null;
			HttpEntity entity = ((HttpEntityEnclosingRequest) request)
					.getEntity();
			String entityContent = EntityUtils.toString(entity);
			Pattern p = Pattern.compile("t=(.*)$");
			Matcher m = p.matcher(entityContent);
			if (m.find()) {
				tweet = m.group(1);
			}

			if (tweet != null) {
				tweet = URLDecoder.decode(tweet, "UTF-8");
				JSONHashTweet t = Util.g.fromJson(tweet, JSONHashTweet.class);

				BackendDB.getInstance().addTweet(SSM.getVirtualNode(t.hash),
						t.hash, t.tweet);

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