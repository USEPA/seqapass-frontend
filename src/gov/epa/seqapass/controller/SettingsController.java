package gov.epa.seqapass.controller;

import gov.epa.seqapass.listener.SeqAPassServletContextListener;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ManagedBean
@ViewScoped
public class SettingsController {

	static public final int minPassLength = 8;

	static public String addUser(String email, boolean adminRights, boolean itasserRights) {
		String isAdmin = "N";
		String isItasser = "N";
		if (adminRights) {
			isAdmin = "Y";
		}
		if (itasserRights) {
			isItasser = "Y";
		}

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		int updatedRows = 0;
		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		// String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/user/addUser";
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/admin/addUser";
		String outcome = "protected/dashboard/admin.xhtml";
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		FacesContext context = FacesContext.getCurrentInstance();

		if (email != null && !email.isEmpty()) {

			serviceUrl += "?email=" + email + "&isAdmin=" + isAdmin + "&isItasser=" + isItasser;
			ResponseEntity<Integer> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity, Integer.class);
			updatedRows = response.getBody();
		} else {
			context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Email is a required field"));
		}

		if (updatedRows == 0) {
			context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "User info was NOT updated!"));
		} else {
			context.addMessage("growl", new FacesMessage("Success", "User info was updated"));
		}

		return outcome;
	}

	static public String changePassword(String oldPass, String newPass1, String newPass2) {

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		if (!(Boolean) session.getAttribute("loggedIn")) {
			return "index-failed.xhtml";
		}

		int updatedRows = 0;
		String email = session.getAttribute("username").toString();
		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/user/changePass";
		String outcome = "protected/dashboard/notImplemented.xhtml";

		if (newPass1.equals(newPass2) && newPass1.length() > minPassLength - 1) {

			serviceUrl += "?email=" + email + "&origPass=" + oldPass + "&newPass=" + newPass1;

			HttpHeaders headers = new HttpHeaders();
			headers.set("Cookie", session.getAttribute("COOKIE").toString());
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
			ResponseEntity<Integer> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity, Integer.class);

			updatedRows = response.getBody();

		} else {
			if (!newPass1.equals(newPass2)) {
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "New passwords do not match",
						"Please re-enter new passwords");
				FacesContext.getCurrentInstance().addMessage("growl", message);
			} else if (newPass1.length() < 8) {
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "New password must be at least 8 characters",
						"Please re-enter new password");
				FacesContext.getCurrentInstance().addMessage("growl", message);
			}
		}


		if (updatedRows == 0) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Password was not changed", "Please try again");
			FacesContext.getCurrentInstance().addMessage("growl", message);
		} else {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Password updated successfully", null);
			FacesContext.getCurrentInstance().addMessage("growl", message);
		}

		return outcome;
	}

	static public boolean userExists(String email) {

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		// String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/user/addUser";
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/user/userExists";
		serviceUrl += "?email=" + email;
		String outcome = "protected/dashboard/admin.xhtml";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		FacesContext context = FacesContext.getCurrentInstance();
		ResponseEntity<Boolean> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity, boolean.class);

		return response.getBody();
	}

}
