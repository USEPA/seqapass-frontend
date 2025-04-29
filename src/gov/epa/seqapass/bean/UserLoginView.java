package gov.epa.seqapass.bean;

//import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.opensaml.saml2.core.Attribute;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import gov.epa.seqapass.common.ReportInfo;
import gov.epa.seqapass.common.UserMessage;
import gov.epa.seqapass.controller.LoginController;
import gov.epa.seqapass.listener.SeqAPassServletContextListener;
import gov.epa.seqapass.model.SingletonUser;
import gov.epa.seqapass.model.User;

@ManagedBean
@SessionScoped
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserLoginView {
	
//	private boolean authenticated;

	//TODO: change this to have a user object
	private String username;
	private String password;
	private String email;
	private String firstName;
	private String lastName;
	private String affiliation;
	private boolean isAdmin;
	private boolean isItasser;

	private boolean disableLogin;

	private String txtMsg;

	private List<ReportInfo> updateInfo;
	private String notes;

	private String dbName;
	private String domain;
	private Boolean showBackendInfo;
	
	private String infoText = "Default";
	private String infoHeaderText = "Header";

	@PostConstruct
	public void init() {
		// this.updateInfo = LoginController.getUpdateInfo();
		displayPreLoginMsg();
	}

	private static String defaultMessage = "SeqAPASS is not available at this time. Please check back for updates.";

	/**
	 * Checks database for login message and displays message if not null/empty
	 * 
	 * @return boolean specifying whether logins are allowed
	 */
	public void displayPreLoginMsg() {
		List<ReportInfo> tempInfo = null;
		try {
			tempInfo = LoginController.getUpdateInfo();
		} catch (Exception e) {
			System.out.println("Could not retrieve updateInfo!");
		}
		// List<ReportInfo> tempInfo = LoginController.getUpdateInfo();
		// get Update Info.
		// Using temp value so that we set updateInfo to new value only if not
		// null or empty
		// This allows any previously received updateInfo to be seen if backend
		// or database is currently down
		if (tempInfo == null || tempInfo.isEmpty()) {
			// If null, this means backend is not available (getUpdateInfo
			// returns null on 404 error)
			// If empty, this means database is not available (getUpdateInfo
			// returns emptylist on 500 error)
			this.txtMsg = retrieveTxtMsg();
			Severity sev = FacesMessage.SEVERITY_INFO;
			String theTag = null;
			String theMsg = null;
			if (txtMsg.trim().isEmpty()) {
				theMsg = defaultMessage;
			} else {
				// At this point, guaranteed that message is not empty
				// split tag(for icon) and message from
				try {
					theTag = txtMsg.split(":")[0].trim();
					theMsg = txtMsg.split(":", 2)[1];
				} catch (Exception e) {
					// if there are no : in message then use default message
					theMsg = defaultMessage;
				}

				if (theMsg == null || theMsg.replaceAll("\\r?\\n", "").isEmpty()) {
					// Message is null or empty(including whitespace or carriage
					// return)
					theMsg = defaultMessage;
					sev = FacesMessage.SEVERITY_INFO;
				} else {
					if (theTag != null && !theTag.isEmpty()) {
						if (theTag.toLowerCase().equals("info")) {
							sev = FacesMessage.SEVERITY_INFO;
						} else if (theTag.toLowerCase().equals("warn")) {
							sev = FacesMessage.SEVERITY_WARN;
						} else if (theTag.toLowerCase().equals("error")) {
							sev = FacesMessage.SEVERITY_ERROR;
						} else if (theTag.toLowerCase().equals("fatal")) {
							sev = FacesMessage.SEVERITY_FATAL;
						} else {
							// use original message because tag is missing
							theMsg = txtMsg;
						}
					} else {
						if (txtMsg.trim().isEmpty()) {
							// use default message because retrieved message is
							// empty
							theMsg = defaultMessage;
						}
					}
				}

			}

			FacesMessage msg = new FacesMessage(sev, theMsg, "");
			FacesContext.getCurrentInstance().addMessage("preLoginKey", msg);
			this.disableLogin = true;
		} else {
			setUpdateInfo(tempInfo);
			UserMessage userMsg = getUserMessage();
			Long loginBlockTime = userMsg.getLoginBlockTime();
			String preBlockLoginMsg = userMsg.getLoginPreBlockMsg();
			String postBlockLoginMsg = userMsg.getLoginPostBlockMsg();

			// if current time is before login block time or login block time is
			// null(always show preBlockLoginMsg message)
			if (loginBlockTime == null || loginBlockTime > System.currentTimeMillis()) {
				if (preBlockLoginMsg != null && !preBlockLoginMsg.isEmpty()) {
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, preBlockLoginMsg, "");
					FacesContext.getCurrentInstance().addMessage("preLoginKey", msg);
				}
				this.disableLogin = false;
			} else {
				// after loginBlockTime
				if (postBlockLoginMsg != null && !postBlockLoginMsg.isEmpty()) {
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, postBlockLoginMsg, "");
					FacesContext.getCurrentInstance().addMessage("preLoginKey", msg);
				}
				this.disableLogin = true;
			}

		}
	}

	// handles checking database for connection issues (database down)
	public List<ReportInfo> checkDatabase() throws ConnectException {
		return LoginController.getUpdateInfo();
	}

	public void logout() {
		username = null;
		email = null;
		firstName = null;
		lastName = null;
		isAdmin = false;
		isItasser = false;

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		session.setAttribute("loggedIn", false);
		session.invalidate();
	}

	// TODO have this call server to retrieve and parse txt file
	private String retrieveTxtMsg() {
		RestTemplate restTemplate = new RestTemplate();

		String serviceUrl = "https://www.epa.gov/sites/production/files/2017-02/seqapass_not_available_message.txt";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response = null;
		try {
			response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
					String.class);
		} catch (RestClientException e) {
			//This works with try/catch but not without.  Maybe due to now initializing response as null
//			e.printStackTrace();
//			System.out.println(e);
		}
		String theFile = response.getBody();
	

		// String theFile = null;
		// try {
		// theFile = new String(Files.readAllBytes(
		// Paths.get("C:\\Users\\csimmo02\\git\\seq_frontend_repo\\seqapass\\src\\message.txt")));
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		List<String> msgList = new ArrayList<String>(Arrays.asList(theFile.split("\\r?\\n")));
		// trim all whitespace from beginning and end of strings
		msgList.replaceAll(String::trim);
		// find all comment lines and remove them
		List<String> commentLines = new ArrayList<String>();
		for (String line : msgList) {
			String trimmedLine = line.trim();
			if (trimmedLine.startsWith("#") || trimmedLine.isEmpty()) {
				commentLines.add(line);
			}
		}
		msgList.removeAll(commentLines);
		// remove all non-ASCII characters from message line
		for (String line : msgList) {
			line.replaceAll("[^\\x00-\\x7f]", "");
		}
		// Rebuild string from list
		StringBuilder sb = new StringBuilder();
		for (String s : msgList) {
			sb.append(s);
			sb.append("\r\n");
		}
		
		return sb.toString();
	}

	/**
	 * Request to retrieve all login/submit messages with block times
	 * 
	 * @return
	 */
	public UserMessage getUserMessage() {
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/getUserMessages";

		HttpHeaders headers = new HttpHeaders();
		// headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<UserMessage> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
				UserMessage.class);

		UserMessage theMsg = response.getBody();
		return theMsg;
	}
	
	
	public String requestJSCode(String name) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/javascript/getJSCode";

		serviceUrl += "?name=" + name;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		String theCode;
		try {
			ResponseEntity<String> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
					String.class);
			theCode = response.getBody();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Warning: could not find javascript code on database with name: " + name);
			theCode = "";
		}
		
		
		return theCode;
	}
	
	
	public void populateInfoText(String infoBox){
		switch (infoBox) {
		case "login":
			setInfoText("<html><body>"
					+ "<p><b>User Account Migration - Version 4 of SeqAPASS</b></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; All users external to EPA with login on the previous SeqAPASS version will automatically"
					+ " migrate to the new Web Application Access login, however they will need to reset their passwords.  More information is provided on the"
					+ " SeqAPASS Log In page (Want an account? Click <a href=\"newUser.xhtml\">here</a> for instructions).</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Your previous account including completed SeqAPASS jobs were transferred.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; If you are having any problems accessing your account, please email us at <a href=\"mailto:SeqAPASS.support@epa.gov\">SeqAPASS.support@epa.gov</a>.</p>"
					+ "</body></html>");
			setInfoHeaderText("SeqAPASS Login");
			break;
		case "browser":
			setInfoText("<html><body>"
					+ "<p>SeqAPASS was developed and tested for optimal compatibility with Chrome."
					+ "  Functionality may be limited when using alternate web browsers.</p>"
					+ "</body></html>");
			setInfoHeaderText("Optimal Compatibility");
			break;
		default:
			setInfoText("");
			break;
		}
		
		displayPreLoginMsg();
	}
	
	public void loginAction(ActionEvent event){
		login();
	}
	
	public void test(){
		System.out.println("Testing");
	}

	
	public boolean login() {

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		isAdmin = false;
		isItasser = false;
		disableLogin = true;
		
		User theUser = null;

		RestTemplate restTemplate = new RestTemplate();
		
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

		try {
			SAMLCredential creds = null;
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();

			if (!(auth instanceof AnonymousAuthenticationToken)){
//				currentUserName = auth.getName();
				creds = (SAMLCredential)auth.getCredentials();
				
				String email = creds.getAttributeAsString("mail");
				System.out.println("EMAIL: = " + email);
				System.out.println("creds: " + creds.toString());
				
				List<Attribute> atts = creds.getAttributes();
				
				for (Attribute att : atts){
					System.out.println("Att name: " + att.getName());
					System.out.println("Att value: " + creds.getAttributeAsString(att.getName()));
				}
				

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
				CloseableHttpClient instance = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
				HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
				factory.setHttpClient(instance);
				restTemplate.setRequestFactory(factory);
				
				System.out.println("attempting to connect frontend to backend");
				//login frontend user to backend
				try {
					
					ApplicationContext ac = new ClassPathXmlApplicationContext("beans.xml");
					
					SingletonUser FEUser = ac.getBean("singletonUser", SingletonUser.class);
					
					String FEUsername = FEUser.getUsername();
					System.out.println(FEUsername);
					String FEPassword = FEUser.getPassword();
					System.out.println(FEPassword);
					
					
					String loginUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/login";
					MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
					parameters.add("username", FEUsername);
					parameters.add("password", FEPassword);
				        System.out.println("loginUrl: " + loginUrl);

					//ResponseEntity<String> responseEntity = new ResponseEntity<String>(null);
					ResponseEntity<String> responseEntity = restTemplate.postForEntity(loginUrl, parameters,
							String.class);
					System.out.println(responseEntity.getHeaders().toString());
//					System.out.println("Login Successful");
//					session.setAttribute("loggedIn", true);
					
					HttpHeaders reqq = responseEntity.getHeaders();
					String ss = reqq.get("Set-Cookie").get(0);
					session.setAttribute("COOKIE", ss);
//					session.setAttribute("UserInfo", restTemplate.getForObject("http://localhost:8080/SeqAPASS-BE/protected/service/user?email=" + username, User.class));
					
					// Test for secured url
//					HttpHeaders headers = new HttpHeaders();
					
					headers.set("Cookie", ss);
					headers.setContentType(MediaType.APPLICATION_JSON);
					
//					headers.setContentType(MediaType.APPLICATION_JSON);
//					HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
//					CloseableHttpClient instance = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
//					HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
//					factory.setHttpClient(instance);
//					restTemplate.setRequestFactory(factory);
					
			
					// End test for secured url
					
					System.out.println("Successfully connected FE user to BE");

				} catch (HttpClientErrorException e) {
					System.out.println("Error: Could not connect to backend");
					System.out.println(e.getStatusCode());
					System.out.println(e.getResponseBodyAsString());
				}
				
				
				//1st check if user exists, if so then login, if not, add to database and continue with login
				String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/user/userExists?email="
						+ email;
				ResponseEntity<Boolean> responseBool = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
						boolean.class);
				boolean userExists = responseBool.getBody();
				
				if (!userExists){
					//TODO: add user to database
					serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/user/addUser?email="
							+ email;
					ResponseEntity<Integer> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
							int.class);
					int added = response.getBody();
					System.out.println("added = " + added);
				}
				
				
//				TODO: In future, this should just return userid, email, isAdmin
//				need to add user id (LAN id) to user object
//				need to handle error if user not in database (create new user?) 
				serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/user?email="
					+ email;
				ResponseEntity<User> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
					User.class);
				theUser = response.getBody();
//				session.setAttribute("UserInfo", theUser);

				if (theUser == null){
					System.out.println("Error: user is NULL");
				} else if (theUser.getIsEnabled().toLowerCase().equals("n")){
					throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "User is disabled");
				}else {
					if (theUser.getIsAdmin().toLowerCase().equals("y")) {
						isAdmin = true;
					}
					if (theUser.getIsItasser().toLowerCase().equals("y")) {
						isItasser = true;
					}
					disableLogin = false;
					
					//only uses SAML crds for uid
					theUser.setDisplayName(creds.getAttributeAsString("displayname"));
					theUser.setUid(creds.getAttributeAsString("uid"));
					theUser.setFirstName(creds.getAttributeAsString("givenname"));
					theUser.setLastName(creds.getAttributeAsString("sn"));
					theUser.setEmail(creds.getAttributeAsString("mail"));
					
					//Set user info to session cookie
					session.setAttribute("UserInfo", theUser);
					
					//Set username to display in GUI as display name from SAML response
					username = theUser.getDisplayName();
				}
			}

		} catch (HttpClientErrorException e) {
			System.out.println("Error: Unsuccessful Login");
			System.out.println(e.getStatusCode());
			System.out.println(e.getResponseBodyAsString());

			String theMsg2 = "Login Error";
			FacesMessage msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN, theMsg2,
					"Invalid credentials (Contact: SeqAPASS.support@epa.gov)");
			FacesContext.getCurrentInstance().addMessage("loginKey", msg2);
			return false;

		}
		
		return true;
		
//		System.out.println("UID is: " + theUser.getUid());
//		System.out.println("Email is: " + theUser.getEmail());
//		System.out.println("First name is: " + theUser.getFirstName());
//		System.out.println("Last name is: " + theUser.getLastName());
//		System.out.println("Is Admin: " + theUser.getIsAdmin());
//		System.out.println("User Id# is: " + theUser.getUserid());
	}
	
	public boolean isUserSessionEnabled(){
		
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
//		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
//		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/mainReport";

		User theUser = (User) session.getAttribute("UserInfo");
		if (theUser == null  || theUser.getIsEnabled().toLowerCase().equals("n")){
			return false;
		}
		
		return true;
	}

	public Boolean getShowBackendInfo() {
		return showBackendInfo;
	}

	public void setShowBackendInfo(Boolean showBackendInfo) {
		this.showBackendInfo = showBackendInfo;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}



//	public boolean isAuthenticated() {
//		return authenticated;
//	}
//
//	public boolean getShowLoginDialog() {
//		return !authenticated;
//	}

	public String getEmail() {
		return email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAffiliation() {
		return affiliation;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void remoteCommand(ActionEvent event) {
		System.out.print("Hello from remoteCommand");
	}

	// public void execute() {
	// System.out.print("Hello from execute()");
	// FacesContext.getCurrentInstance()
	// .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
	// "Executed", "Using RemoteCommand."));
	// }

	public List<ReportInfo> getUpdateInfo() {
		return updateInfo;
	}

	public void setUpdateInfo(List<ReportInfo> updateInfo) {
		this.updateInfo = updateInfo;
	}

	public String getTxtMsg() {
		return txtMsg;
	}

	public void setTxtMsg(String txtMsg) {
		this.txtMsg = txtMsg;
	}

	public boolean isDisableLogin() {
		return disableLogin;
	}

	public void setDisableLogin(boolean disableLogin) {
		this.disableLogin = disableLogin;
	}

	public String getInfoText() {
		return infoText;
	}

	public void setInfoText(String infoText) {
		this.infoText = infoText;
	}

	public String getInfoHeaderText() {
		return infoHeaderText;
	}

	public void setInfoHeaderText(String infoHeaderText) {
		this.infoHeaderText = infoHeaderText;
	}

	public boolean isItasser() {
		return isItasser;
	}

	public void setItasser(boolean isItasser) {
		this.isItasser = isItasser;
	}

}
