import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.log4j.Logger;

public class HttpFrontendSearchHandler implements HttpRequestHandler {
	private static Logger log = Logger
			.getLogger(HttpFrontendSearchHandler.class);

	public void handle(final HttpRequest request, final HttpResponse response,
			final HttpContext context) throws HttpException, IOException {

		String method = request.getRequestLine().getMethod()
				.toUpperCase(Locale.ENGLISH);

		log.info("Frontend: Handling Search; Line = "
				+ request.getRequestLine());
		if (method.equals("GET")) {
			final String target = request.getRequestLine().getUri();

			Pattern p = Pattern.compile("/search\\?q=(.*)$");
			Matcher m = p.matcher(target);
			if (m.find()) {
				String hash = m.group(1);

				String responseText = Util
						.doGet(SSM.getInstance().getServer(hash), "/gethash?h="
								+ hash);

				log.info("Backend returns: " + responseText);
				ResultSet result = Util.g.fromJson(responseText,
						ResultSet.class);

				final String Content = result.getLatest();

				EntityTemplate body = new EntityTemplate(new ContentProducer() {
					public void writeTo(final OutputStream outstream)
							throws IOException {
						OutputStreamWriter writer = new OutputStreamWriter(
								outstream, "UTF-8");
						writer.write(Content);
						writer.write("\n");
						writer.flush();
					}
				});
				body.setContentType("application/json; charset=UTF-8");

				response.setStatusCode(HttpStatus.SC_OK);
				response.setEntity(body);
			} else {
				response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
			}
		} else {
			throw new MethodNotSupportedException(method
					+ " method not supported\n");
		}

	}
}