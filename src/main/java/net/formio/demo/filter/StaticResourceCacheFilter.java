package net.formio.demo.filter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StaticResourceCacheFilter implements Filter {

	private static final TimeZone GMT = TimeZone.getTimeZone("GMT");
	private static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
	public static final String[] CACHEABLE_CONTENT_TYPES = new String[] {
		"text/css", "text/javascript", "application/javascript", "application/x-javascript",
		"image/png", "image/jpeg", "image/gif", "image/jpg",
		"application/octet-stream" };

	static {
		Arrays.sort(CACHEABLE_CONTENT_TYPES);
	}

	@Override
	public void init(FilterConfig cfg) throws ServletException {
		// nothing
	}

	@Override
	public void destroy() {
		// nothing
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		chain.doFilter(httpRequest, httpResponse);

		String contentType = httpResponse.getContentType();
		if (contentType != null && Arrays.binarySearch(CACHEABLE_CONTENT_TYPES, contentType) > -1) {
			Calendar inTwoMonths = Calendar.getInstance();
			inTwoMonths.add(Calendar.MONTH, 2);
			String dateStr = formatDate(inTwoMonths.getTime());
			httpResponse.setHeader("Last-Modified", dateStr);
			httpResponse.setHeader("Expires", dateStr);
		} else {
			httpResponse.setHeader("Expires", "-1");
		}
	}

	private static String formatDate(Date date) {
		if (date == null)
			throw new IllegalArgumentException("date is null");
		SimpleDateFormat formatter = new SimpleDateFormat(PATTERN_RFC1123, Locale.US);
		formatter.setTimeZone(GMT);
		return formatter.format(date);
	}
}