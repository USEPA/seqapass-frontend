package gov.epa.seqapass.controller;

import gov.epa.seqapass.common.LevelFourAccessionRow;
import gov.epa.seqapass.common.LevelFourRequestableRow;
import gov.epa.seqapass.common.LevelThreeRequestableRow;
import gov.epa.seqapass.common.Link;
import gov.epa.seqapass.listener.SeqAPassServletContextListener;
import gov.epa.seqapass.model.User;

import java.util.Arrays;
import java.util.List;

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
public class RequestRunController extends AbstractController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7031928609798799570L;

	static public String requestLevelTwoRun(int accessionRunId, String key, int startPosition, int userId) {

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();

		User theUser = (User) session.getAttribute("UserInfo");

		RestTemplate restTemplate = new RestTemplate();
		String url = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/analysis/levelTwoRun";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.set("UserID", Integer.toString(theUser.getUserid()));
		headers.setContentType(MediaType.APPLICATION_JSON);

		url += "?accessionRunId=" + accessionRunId + "&key=" + key + "&startPosition=" + startPosition + "&userId=" + userId;

		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

		String status = response.getBody();
		return status;
	}

	// static public String requestLevelThreeRun(int accessionRunId, String templateText, int taxGroup){
	//
	// HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
	// String hostName = SeqAPassServletContextListener.getHost();
	// String port = SeqAPassServletContextListener.getPort();
	// String protocol = SeqAPassServletContextListener.getProtocol();
	//
	// User theUser = (User) session.getAttribute("UserInfo");
	//
	// RestTemplate restTemplate = new RestTemplate();
	// String url = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/analysis/levelThreeRun/";
	// HttpHeaders headers = new HttpHeaders();
	// headers.set("Cookie", session.getAttribute("COOKIE").toString());
	// headers.set("UserID", Integer.toString(theUser.getUserid()));
	// headers.setContentType(MediaType.APPLICATION_JSON);
	//
	// // RequestContext requestContext = new RequestContext(accessionList, theUser.getUserid());
	// // MultiValueMap<String,String> parameters = new LinkedMultiValueMap<String, String>();
	// // parameters.add("accessionRunId", String.valueOf(accessionRunId));
	// // parameters.add("templateText", templateText);
	// // parameters.add("taxGroup", String.valueOf(taxGroup));
	//
	// Map<String,String> parameters = new LinkedHashMap<String, String>();
	// parameters.put("accessionRunId", String.valueOf(accessionRunId));
	// parameters.put("templateText", templateText);
	// parameters.put("taxGroup", String.valueOf(taxGroup));
	//
	// HttpEntity<?> requestEntity = new HttpEntity<Object>(parameters, headers);
	//
	// ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
	// String status = response.getBody();
	//
	// return status;
	// }

	// static public String requestLevelThreeRun(int accessionRunId, String templateText, List<Integer> taxGroups) {
	//
	// HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
	// String hostName = SeqAPassServletContextListener.getHost();
	// String port = SeqAPassServletContextListener.getPort();
	// String protocol = SeqAPassServletContextListener.getProtocol();
	//
	// User theUser = (User) session.getAttribute("UserInfo");
	//
	// RestTemplate restTemplate = new RestTemplate();
	// String url = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/analysis/levelThreeRun/";
	// HttpHeaders headers = new HttpHeaders();
	// headers.set("Cookie", session.getAttribute("COOKIE").toString());
	// headers.set("UserID", Integer.toString(theUser.getUserid()));
	// headers.setContentType(MediaType.APPLICATION_JSON);
	//
	// LevelThreeRequestContext requestContext = new LevelThreeRequestContext(accessionRunId, templateText, taxGroups);
	//
	// HttpEntity<?> requestEntity = new HttpEntity<Object>(requestContext, headers);
	//
	// ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
	// String status = response.getBody();
	//
	// return status;
	// }

	static public String requestLevelThreeRun(LevelThreeRequestableRow request) {

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();

		User theUser = (User) session.getAttribute("UserInfo");

		RestTemplate restTemplate = new RestTemplate();
		String url = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/analysis/levelThreeRun/";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.set("UserID", Integer.toString(theUser.getUserid()));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> requestEntity = new HttpEntity<Object>(request, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
		String status = response.getBody();

		return status;
	}
	
	
	static public String createNewLevelFourRun(LevelFourRequestableRow request) {

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();

		User theUser = (User) session.getAttribute("UserInfo");

		RestTemplate restTemplate = new RestTemplate();
		String url = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/analysis/levelFourRun/";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.set("UserID", Integer.toString(theUser.getUserid()));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> requestEntity = new HttpEntity<Object>(request, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
		String status = response.getBody();

//		String status = "Stub method for submitting Level Four Run";
		return status;
	}
	
	public static String requestLevel4FASTAs(LevelFourRequestableRow level4Run){
		
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();

		User theUser = (User) session.getAttribute("UserInfo");

		RestTemplate restTemplate = new RestTemplate();
		String url = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/analysis/levelFourFASTAs/";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.set("UserID", Integer.toString(theUser.getUserid()));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> requestEntity = new HttpEntity<Object>(level4Run, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
		String status = response.getBody();

//		String status = "Stub method for submitting Level Four Run";
		return status;		
	}
	
	public static String requestLevel4Itasser(LevelFourRequestableRow level4Run) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();

		User theUser = (User) session.getAttribute("UserInfo");

		RestTemplate restTemplate = new RestTemplate();
		String url = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/analysis/levelFourItasser/";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.set("UserID", Integer.toString(theUser.getUserid()));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> requestEntity = new HttpEntity<Object>(level4Run, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
		String status = response.getBody();

//		String status = "Stub method for submitting Level Four Run";
		return status;		
	}
	
	public static String requestLevel4TMAlign(List<LevelFourRequestableRow> level4Runs) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();

		User theUser = (User) session.getAttribute("UserInfo");

		RestTemplate restTemplate = new RestTemplate();
		String url = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/analysis/levelFourTMAlign/";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.set("UserID", Integer.toString(theUser.getUserid()));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> requestEntity = new HttpEntity<Object>(level4Runs, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
		String status = response.getBody();

//		String status = "Stub method for submitting Level Four Run";
		return status;		
	}
	
	
	static public List<Link> getLinks(String group){
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();

		RestTemplate restTemplate = new RestTemplate();
		String url = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/getLinks";
		url += "?group=" + group;
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		ResponseEntity<Link[]> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Link[].class);

		List<Link> theList = Arrays.asList(response.getBody());
		
		return theList;
		
	}
	
	static public String test(String acc){
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();

		RestTemplate restTemplate = new RestTemplate();
		String url = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/analysis/mapUniprot";
		url += "?acc=" + acc;
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

		String uniProtAcc = response.getBody();
		
		return uniProtAcc;
		
	}

}
