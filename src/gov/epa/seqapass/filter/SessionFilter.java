package gov.epa.seqapass.filter;

import gov.epa.seqapass.listener.SeqAPassServletContextListener;

import java.io.IOException;
import java.util.ArrayList;
//import java.util.Enumeration;
import java.util.StringTokenizer;

//import javax.faces.bean.SessionScoped;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SessionFilter implements Filter {

	private ArrayList<String> urlList;

	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session = request.getSession(false);

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String frontHostName = SeqAPassServletContextListener.getFrontHost();

		ArrayList<String> excludedUrls = new ArrayList<String>();
		excludedUrls.add("/protected/dashboard/loginSplash.xhtml");

		String path = ((HttpServletRequest) req).getServletPath();
		if (!excludedUrls.contains(path)) {

			String loginUrl;

			if (frontHostName != null) {
				loginUrl = protocol + "://" + frontHostName + "/seqapass/";
			} else {
				loginUrl = protocol + "://" + hostName + ":" + port + "/seqapass/";
			}
			
			if (session != null) {
				if (session.getAttribute("UserInfo") == null) {
					response.sendRedirect(loginUrl);
				} else {
					chain.doFilter(req, res);
				}
			} else {
				HttpServletRequest httpReq = (HttpServletRequest) req;
				if (httpReq.getHeader("accept") != null && httpReq.getHeader("accept").contains("text/html")){
					response.setContentType("text/html"); 
					response.getWriter().write(createAjaxRedirectHtml(loginUrl));
				} else {
					response.setContentType("application/xml");
					response.getWriter().write(createAjaxRedirectXml(loginUrl));
				}
			}

//			if (session != null) {
//				if (session.getAttribute("loggedIn") == null) {
//					response.sendRedirect(loginUrl);
//				} else {
//					chain.doFilter(req, res);
//				}
//			} else {
//				response.setContentType("text/xml"); // needed to handle session
//														// timeout where
//														// expecting JSON
//														// response
//				response.getWriter().write(createAjaxRedirectXml(loginUrl));
//				// response.sendRedirect(loginUrl);
//			}
		} else {
			chain.doFilter(req, res);
		}

	}

	private String createAjaxRedirectXml(String redirectUrl) {
		return new StringBuilder().append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
				.append("<partial-response><redirect url=\"").append(redirectUrl)
				.append("\"></redirect></partial-response>").toString();
	}
	
	private String createAjaxRedirectHtml(String redirectUrl) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<head>");
		sb.append("<meta http-equiv=\"refresh\" content=\"0; url=" + redirectUrl + "\" />");
		sb.append("</head");
		sb.append("<body>");
		sb.append("<p><a href=\"" + redirectUrl + "\">Redirect</a></p>");
		sb.append("</body>");
		sb.append("</html>");
//		return new StringBuilder().append("<html version=\"1.0\" encoding=\"UTF-8\"?>")
//				.append("<partial-response><redirect url=\"").append(redirectUrl)
//				.append("\"></redirect></partial-response>").toString();
		return sb.toString();
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		String urls = config.getInitParameter("avoid-urls");
		StringTokenizer token = new StringTokenizer(urls, ",");

		urlList = new ArrayList<String>();

		while (token.hasMoreTokens()) {
			urlList.add(token.nextToken());

		}
	}

}
