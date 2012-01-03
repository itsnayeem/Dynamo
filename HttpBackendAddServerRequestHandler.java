import java.io.IOException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

public class HttpBackendAddServerRequestHandler implements HttpRequestHandler {
	public void handle(final HttpRequest request, final HttpResponse response,
			final HttpContext context) throws HttpException, IOException {

		String method = request.getRequestLine().getMethod()
				.toUpperCase(Locale.ENGLISH);

		if (method.equals("GET")) {
			final String target = request.getRequestLine().getUri();
			Pattern p = Pattern.compile("/addserver\\?ip=(.*?)&port=(.*)$");
			Matcher m = p.matcher(target);
			if (m.find()) {
				String ip = m.group(1);
				int port = Integer.parseInt(m.group(2));

				SSM.getInstance().addServer(ip, port);
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
