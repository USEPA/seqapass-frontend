package gov.epa.seqapass.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import gov.epa.seqapass.common.LevelOneRequestable;
import gov.epa.seqapass.controller.SettingsController;
import gov.epa.seqapass.listener.SeqAPassServletContextListener;
import gov.epa.seqapass.model.User;

@ManagedBean
@SessionScoped
public class SettingsView {

	private String email;
//	private String firstName;
//	private String lastName;
//	private String password;
	private boolean adminRights;
	private boolean itasserRights;

//	private String oldPass;
//	private String newPass1;
//	private String newPass2;

	private String dialogMsg;

	private String deleteText;
	private List<String> deleteAccList;

	public void addUserButton() {
		FacesContext context = FacesContext.getCurrentInstance();
		boolean validated = true;
		// Validation
//		Pattern namePattern = Pattern.compile("[^a-zA-Z -]");
		Pattern emailPattern = Pattern
				.compile("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
//		Matcher firstNameMatcher = namePattern.matcher(firstName);
//		Matcher lastNameMatcher = namePattern.matcher(lastName);
		Matcher emailMatcher = emailPattern.matcher(email);
//		if (firstName.length() > 50 || lastName.length() > 50 || email.length() > 50 || password.length() > 50) {
		if (email.length() > 50) {
			validated = false;
			context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Email field must be less than 50 characters."));
		}
//		if (firstNameMatcher.find() || lastNameMatcher.find()) {
//			validated = false;
//			context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
//					"Names can only contain letters, dashes, and spaces"));
//		}
		if (!emailMatcher.matches()) {
			validated = false;
			context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Email address is not valid"));
		}

		if (!validated) {
			return;
		}
		SettingsController.addUser(email, adminRights, itasserRights);
		clearAddUser();
	}

//	public void changePassword() {
//		FacesContext context = FacesContext.getCurrentInstance();
//		if (newPass1.length() > 50) {
//			context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Passwords must be less than 50 characters."));
//			return;
//		}
//
//		SettingsController.changePassword(oldPass, newPass1, newPass2);
////		clearChangePass();
//	}

	public void clearAddUser() {
		setEmail("");
//		setFirstName("");
//		setLastName("");
//		setPassword("");
		setAdminRights(false);
	}

//	public void clearChangePass() {
//		setOldPass("");
//		setNewPass1("");
//		setNewPass2("");
//	}

	public void deleteAccButton() {
		FacesContext context = FacesContext.getCurrentInstance();
		deleteAccList = new ArrayList<String>();
		// String entries[] = inputText.split("\\s|\\n");

		Pattern accessionListPattern = Pattern.compile("[^a-zA-Z0-9:=_,.-]");

		if (deleteText.trim().length() == 0) {
			context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Must enter NCBI accession"));
			return;
		}

		String entries[] = deleteText.split("\\s+");
		for (String entry : entries) {
			Matcher m = accessionListPattern.matcher(entry);
			if (m.find()) {
				context.addMessage("growl",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Only alphanumeric and : = _ , . - allowed"));
				return;
			}
			deleteAccList.add(entry);
		}

		// Call getLevelOneJobCount service
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();

		User theUser = (User) session.getAttribute("UserInfo");

		RestTemplate restTemplate = new RestTemplate();
		String url = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/analysis/levelOneJobsCount/";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.set("UserID", Integer.toString(theUser.getUserid()));
		headers.setContentType(MediaType.APPLICATION_JSON);

		LevelOneRequestable levelOneRequest = new LevelOneRequestable(deleteAccList, theUser.getUserid());
		HttpEntity<?> requestEntity = new HttpEntity<Object>(levelOneRequest, headers);

		ResponseEntity<List<String>> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
				new ParameterizedTypeReference<List<String>>() {
				});
		List<String> result = new ArrayList<String>();
		result = response.getBody();

		// Format result string for displaying in message box
		String formattedResult = "";

		List<Integer> removeList = new ArrayList<Integer>();
		for (int i=0; i<result.size(); i++){
			String str = result.get(i);
			formattedResult += str + "\n";
			if (str.contains("not found") || str.contains("no results found") || str.contains("incorrect format")){
				removeList.add(i);
			}
		}
		
		// Remove accessions with errors, not found, etc
		if (removeList.size() > 0){
			Collections.sort(removeList);
			Collections.reverse(removeList);
			for (Integer index : removeList){
				deleteAccList.remove((int)index);
			}
		}
		
		setDialogMsg(formattedResult);
		System.out.println("Dialog button open");
		PrimeFaces.current().executeScript("PF('confirmDialogVar').show();");
		System.out.println("Done with dialog button");
		// clearAccButton();

	}

	public void deleteRuns() {
		System.out.println("Ready to delete with " + deleteText);
		for (String del : deleteAccList) {
			System.out.println("Entry: " + del);
		}

		FacesContext context = FacesContext.getCurrentInstance();

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();

		User theUser = (User) session.getAttribute("UserInfo");

		RestTemplate restTemplate = new RestTemplate();
		String url = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/analysis/levelOneJobsRemove/";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.set("UserID", Integer.toString(theUser.getUserid()));
		headers.setContentType(MediaType.APPLICATION_JSON);
		System.out.println("headers are set ... ");

		LevelOneRequestable levelOneRequest = new LevelOneRequestable(deleteAccList, theUser.getUserid());
		HttpEntity<?> requestEntity = new HttpEntity<Object>(levelOneRequest, headers);
		System.out.println("getting response ... ");

		ResponseEntity<List<String>> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
				new ParameterizedTypeReference<List<String>>() {
				});
		List<String> result = new ArrayList<String>();
		result = response.getBody();
		System.out.println("Got response ... ");

		clearAccButton();
		for (String str : result) {
			if (str.toLowerCase().contains("not found") || str.toLowerCase().contains("no results found")) {
				context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", str));
			} else {
				context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Outcome", str));
			}
		}
	}

	public void clearAccButton() {
		deleteText = "";
		deleteAccList.clear();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

//	public String getFirstName() {
//		return firstName;
//	}
//
//	public void setFirstName(String firstName) {
//		this.firstName = firstName;
//	}
//
//	public String getLastName() {
//		return lastName;
//	}
//
//	public void setLastName(String lastName) {
//		this.lastName = lastName;
//	}
//
//	public String getPassword() {
//		return password;
//	}
//
//	public void setPassword(String password) {
//		this.password = password;
//	}

	public boolean isAdminRights() {
		return adminRights;
	}

	public void setAdminRights(boolean adminRights) {
		this.adminRights = adminRights;
	}

//	public String getOldPass() {
//		return oldPass;
//	}
//
//	public void setOldPass(String oldPass) {
//		this.oldPass = oldPass;
//	}
//
//	public String getNewPass1() {
//		return newPass1;
//	}
//
//	public void setNewPass1(String newPass1) {
//		this.newPass1 = newPass1;
//	}
//
//	public String getNewPass2() {
//		return newPass2;
//	}
//
//	public void setNewPass2(String newPass2) {
//		this.newPass2 = newPass2;
//	}

	public String getDeleteText() {
		return deleteText;
	}

	public void setDeleteText(String deleteText) {
		this.deleteText = deleteText;
	}

	public List<String> getDeleteAccList() {
		return deleteAccList;
	}

	public void setDeleteAccList(List<String> deleteAccList) {
		this.deleteAccList = deleteAccList;
	}

	public String getDialogMsg() {
		return dialogMsg;
	}

	public void setDialogMsg(String dialogMsg) {
		this.dialogMsg = dialogMsg;
	}

	public boolean isItasserRights() {
		return itasserRights;
	}

	public void setItasserRights(boolean itasserRights) {
		this.itasserRights = itasserRights;
	}

//	public class LevelOneRequestable {
//
//		public LevelOneRequestable(List<String> accessions, int userID) {
//			this.accessionList = accessions;
//			this.userID = userID;
//		}
//
//		private List<String> accessionList;
//		private int userID;
//
//		public List<String> getAccessionList() {
//			return accessionList;
//		}
//
//		public void setAccessionList(List<String> accessionList) {
//			this.accessionList = accessionList;
//		}
//
//		public int getUserID() {
//			return userID;
//		}
//
//		public void setUserID(int userID) {
//			this.userID = userID;
//		}
//
//	}

}