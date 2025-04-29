package gov.epa.seqapass.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import gov.epa.seqapass.bean.UserLoginView;
import gov.epa.seqapass.common.ReportInfo;
import gov.epa.seqapass.listener.SeqAPassServletContextListener;

@ManagedBean
@ViewScoped
public class LoginController {

//	public String login() {
//		return "protected/dashboard/index.xhtml";
//	}
	
	public void testLogin(){
		System.out.println("testLogin");
		
		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		UserLoginView userLoginView = (UserLoginView) FacesContext.getCurrentInstance().getApplication().getELResolver()
				.getValue(elContext, null, "userLoginView");
		
		boolean success = userLoginView.login();
		
		HttpServletRequest origRequest = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		
//		System.out.println(origRequest.getContextPath());
//		System.out.println(origRequest.getPathInfo());
//		System.out.println(origRequest.getRequestURI());
//		
//		System.out.println(origRequest.getServletPath());
//		System.out.println(origRequest.getLocalAddr());
		System.out.println(origRequest.getServerName());
		System.out.println(origRequest.getServerPort());
		
		System.out.println(origRequest.isSecure());
		System.out.println(origRequest.getContextPath());
		
		String url;
		
		if (origRequest.isSecure()){
			url = "https://";
		} else {
			url = "http://";
		}
		
		url += origRequest.getServerName() + ":" + origRequest.getServerPort() + origRequest.getContextPath();
		
		
		if (success){
			url = "index.xhtml";
		} else {
//			url = "";
			url += "/loginError.xhtml";
		}
		
		
		System.out.println("url: " + url);
		
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(url);
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String processLogin(){
		System.out.println("processing login!!!");
		String outcome="index.xhtml";
//		UserLoginView userLoginView = (UserLoginView) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
//				.get("userLoginView");
//		boolean success = userLoginView.login();
//		if (success){
			outcome = "protected/dashboard/index.xhtml";
//			try {
////				FacesContext.getCurrentInstance().getExternalContext().redirect("https://www.google.com");
//				FacesContext.getCurrentInstance().getExternalContext().dispatch("protected/dashboard/index.xhtml");
//				return "protected/dashboard/index.xhtml";
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return "protected/dashboard/index.xhtml";
			return outcome;
//		}
//		return null;
//		try {
//			FacesContext.getCurrentInstance().getExternalContext().redirect("https://www.google.com");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//			return "";
	}

	public String logout() {
		// activeUsers.remove(getLoggedInUser());
		// FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

		UserLoginView userLoginView = (UserLoginView) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.get("userLoginView");
//		if ((userLoginView != null) && (userLoginView.isAuthenticated())) {
		if ((userLoginView != null)) {
			userLoginView.logout();
		}
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

		return "login";

	}

	public static List<ReportInfo> getUpdateInfo() {
		// HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/updateInfo";
		
		System.out.println("Trying: " + serviceUrl);

		HttpHeaders headers = new HttpHeaders();
		// headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<ReportInfo[]> response = null;
		try {
			response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity, ReportInfo[].class);
		} catch (HttpClientErrorException ex) {
			if (ex.getStatusCode().value() == 404){
				System.out.println("Backend is not available!!");
				return null;
			} else if (ex.getStatusCode().value() == 500){
				System.out.println("Database is not available!!");
				return new ArrayList<ReportInfo>();
			} else {
				throw ex;
			}
		} 

		List<ReportInfo> theList = Arrays.asList(response.getBody());
		return theList;
	}
	
}
