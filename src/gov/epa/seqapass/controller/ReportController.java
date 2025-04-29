package gov.epa.seqapass.controller;


import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import gov.epa.seqapass.common.AminoAcid;
import gov.epa.seqapass.common.Chemical;
import gov.epa.seqapass.common.CutoffData;
import gov.epa.seqapass.common.DensityRow;
import gov.epa.seqapass.common.LevelFourAccessionRow;
import gov.epa.seqapass.common.LevelFourRequestableRow;
import gov.epa.seqapass.common.LevelFourResultRow;
import gov.epa.seqapass.common.LevelFourStatusRow;
import gov.epa.seqapass.common.LevelOneReportRow;
import gov.epa.seqapass.common.LevelOneStatusRow;
import gov.epa.seqapass.common.LevelThreeReportRow;
import gov.epa.seqapass.common.LevelThreeRequestableRow;
import gov.epa.seqapass.common.LevelThreeStatusRow;
import gov.epa.seqapass.common.LevelThreeViewRequest;
import gov.epa.seqapass.common.LevelTwoReportRow;
import gov.epa.seqapass.common.LevelTwoRequestableRow;
import gov.epa.seqapass.common.LevelTwoStatusRow;
import gov.epa.seqapass.common.Protein;
import gov.epa.seqapass.common.ReportInfo;
import gov.epa.seqapass.common.ReportRow;
import gov.epa.seqapass.common.ReportTypeEnum;
import gov.epa.seqapass.common.SpeciesTaxGrouping;
import gov.epa.seqapass.common.TaxEcos;
import gov.epa.seqapass.common.UniprotMap;
import gov.epa.seqapass.common.ZipRequestable;
import gov.epa.seqapass.listener.SeqAPassServletContextListener;
import gov.epa.seqapass.model.User;

@ManagedBean
@ViewScoped
public class ReportController extends AbstractController {

	private static final long serialVersionUID = -743046763249381022L;

	static public List<ReportRow> getMainReportForUser() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/mainReport";

		User theUser = (User) session.getAttribute("UserInfo");
		int userId = theUser.getUserid();


		serviceUrl += "?userid=" + userId;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		ResponseEntity<ReportRow[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity, ReportRow[].class);

		List<ReportRow> theList = Arrays.asList(response.getBody());
		

		return theList;
	}

	static public List<LevelOneReportRow> getLevelOneReportForUser(int accessionRunId) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/levelOneReport";

		serviceUrl += "?accessionRunId=" + accessionRunId;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<LevelOneReportRow[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
				LevelOneReportRow[].class);

		List<LevelOneReportRow> theList = Arrays.asList(response.getBody());
		

		return theList;
	}
	
	static public SpeciesTaxGrouping getTaxGroups(SpeciesTaxGrouping speciesTaxGrp) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/getTaxGroups";
		

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);

		
		HttpEntity<?> requestEntity = new HttpEntity<Object>(speciesTaxGrp, headers);
		ResponseEntity<SpeciesTaxGrouping> response = restTemplate.exchange(serviceUrl, HttpMethod.POST, requestEntity, SpeciesTaxGrouping.class);

		SpeciesTaxGrouping resultGrp = response.getBody();
		
		return resultGrp;
	}
	
	static public List<LevelTwoReportRow> getLevelTwoReportForUser(int accessionRunId, int lev2Id) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/levelTwoReport";

		serviceUrl += "?accessionRunId=" + accessionRunId + "&lev2Id=" + lev2Id;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<LevelTwoReportRow[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
				LevelTwoReportRow[].class);

		List<LevelTwoReportRow> theList = Arrays.asList(response.getBody());

		return theList;
	}
	
	
	static public List<LevelThreeReportRow> getLevelThreeReportForUser(LevelThreeViewRequest request) {

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();

		User theUser = (User) session.getAttribute("UserInfo");

		RestTemplate restTemplate = new RestTemplate();
		String url = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/levelThreeReport/";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.set("UserID", Integer.toString(theUser.getUserid()));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> requestEntity = new HttpEntity<Object>(request, headers);

		ResponseEntity<LevelThreeReportRow[]> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, LevelThreeReportRow[].class);


		List<LevelThreeReportRow> theList = Arrays.asList(response.getBody());

		return theList;
	}
	
	
	static public byte[] requestZippedReports(List<ZipRequestable> zipRequests) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/requestZippedReports";
		

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);

		
		HttpEntity<?> requestEntity = new HttpEntity<Object>(zipRequests, headers);
		ResponseEntity<byte[]> response = restTemplate.exchange(serviceUrl, HttpMethod.POST, requestEntity, byte[].class);

		byte[] resultGrp = response.getBody();
		
		return resultGrp;
	}

	
	static public List<LevelOneStatusRow> getLevelOneStatusForUser() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/levelOneStatus";
		
		User theUser = (User) session.getAttribute("UserInfo");
		int userId = theUser.getUserid();

		serviceUrl += "?userid=" + userId;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<LevelOneStatusRow[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
				LevelOneStatusRow[].class);

		List<LevelOneStatusRow> theList = Arrays.asList(response.getBody());
		

		return theList;
	}
	
	static public List<LevelTwoStatusRow> getLevelTwoStatusForUser() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/levelTwoStatus";
		
		User theUser = (User) session.getAttribute("UserInfo");
		int userId = theUser.getUserid();

		serviceUrl += "?userid=" + userId;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<LevelTwoStatusRow[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
				LevelTwoStatusRow[].class);

		List<LevelTwoStatusRow> theList = Arrays.asList(response.getBody());


		return theList;
	}
	
	static public List<LevelThreeStatusRow> getLevelThreeStatusForUser() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/levelThreeStatus";
		
		User theUser = (User) session.getAttribute("UserInfo");
		int userId = theUser.getUserid();

		serviceUrl += "?userid=" + userId;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<LevelThreeStatusRow[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
				LevelThreeStatusRow[].class);

		List<LevelThreeStatusRow> theList = Arrays.asList(response.getBody());

		return theList;
	}
	
	
//	static public List<HistogramRow> getLevelOneHistogramData(int accessionRunId, int binCount) {
//		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
//		RestTemplate restTemplate = new RestTemplate();
//
//		String hostName = SeqAPassServletContextListener.getHost();
//		String port = SeqAPassServletContextListener.getPort();
//		String protocol = SeqAPassServletContextListener.getProtocol();
//		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/levelOneHistogram";
//
//		serviceUrl += "?accessionRunId=" + accessionRunId + "&binCount=" + binCount;
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.set("Cookie", session.getAttribute("COOKIE").toString());
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
//
//		ResponseEntity<HistogramRow[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
//				HistogramRow[].class);
//
//		List<HistogramRow> theList = Arrays.asList(response.getBody());
//
//		return theList;
//	}
	
	static public CutoffData getCutoffData(int level, ReportTypeEnum reportType, int runId, double eValue, int commonDomains, boolean eukaryotesOnly){
		
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/getDensityData";

		serviceUrl += "?level=" + level ;
		serviceUrl += "&reportType=" + reportType ;
		serviceUrl += "&runId=" + runId ;
		serviceUrl += "&eValue=" + eValue ;
		serviceUrl += "&commonDomains=" + commonDomains ;
		serviceUrl += "&euksOnly=" + eukaryotesOnly;
		
		System.out.println("serviceURL: ");
		System.out.println(serviceUrl);
		

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<DensityRow[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
				DensityRow[].class);

		List<DensityRow> theList = Arrays.asList(response.getBody());
		
//		System.out.println("Retrieved " +  theList.size() + " density rows");

		return CutoffData.newInstance(theList, level);
	}
	
//	static public CutoffData getLevelOnePrimaryCutoffData(int accessionRunId) {
//		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
//		RestTemplate restTemplate = new RestTemplate();
//
//		String hostName = SeqAPassServletContextListener.getHost();
//		String port = SeqAPassServletContextListener.getPort();
//		String protocol = SeqAPassServletContextListener.getProtocol();
//		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/levelOnePrimaryCutoff";
//
//		serviceUrl += "?accessionRunId=" + accessionRunId ;
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.set("Cookie", session.getAttribute("COOKIE").toString());
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
//
//		ResponseEntity<CutoffData> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
//				CutoffData.class);
//
//		CutoffData theData = response.getBody();
//		return theData;
//	}
	
//	static public CutoffData getLevelOneFullCutoffData(int accessionRunId) {
//		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
//		RestTemplate restTemplate = new RestTemplate();
//
//		String hostName = SeqAPassServletContextListener.getHost();
//		String port = SeqAPassServletContextListener.getPort();
//		String protocol = SeqAPassServletContextListener.getProtocol();
//		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/levelOneFullCutoff";
//
//		serviceUrl += "?accessionRunId=" + accessionRunId ;
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.set("Cookie", session.getAttribute("COOKIE").toString());
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
//
//		ResponseEntity<CutoffData> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
//				CutoffData.class);
//
//		CutoffData theData = response.getBody();
//		return theData;
//	}
	
//	static public CutoffData getLevelTwoPrimaryCutoffData(int domainRunId) {
//		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
//		RestTemplate restTemplate = new RestTemplate();
//
//		String hostName = SeqAPassServletContextListener.getHost();
//		String port = SeqAPassServletContextListener.getPort();
//		String protocol = SeqAPassServletContextListener.getProtocol();
//		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/levelTwoPrimaryCutoff";
//
//		serviceUrl += "?domainRunId=" + domainRunId ;
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.set("Cookie", session.getAttribute("COOKIE").toString());
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
//
//		ResponseEntity<CutoffData> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
//				CutoffData.class);
//
//		CutoffData theData = response.getBody();
//		return theData;
//	}
	
//	static public CutoffData getLevelTwoFullCutoffData(int domainRunId) {
//		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
//		RestTemplate restTemplate = new RestTemplate();
//
//		String hostName = SeqAPassServletContextListener.getHost();
//		String port = SeqAPassServletContextListener.getPort();
//		String protocol = SeqAPassServletContextListener.getProtocol();
//		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/levelTwoFullCutoff";
//
//		serviceUrl += "?domainRunId=" + domainRunId ;
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.set("Cookie", session.getAttribute("COOKIE").toString());
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
//
//		ResponseEntity<CutoffData> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
//				CutoffData.class);
//
//		CutoffData theData = response.getBody();
//		return theData;
//	}
	
	
//	static public List<LevelTwoRequestableRow> getLevelTwoDomains(String accessionIdName, int userId) {
//		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
//		RestTemplate restTemplate = new RestTemplate();
//
//		String hostName = SeqAPassServletContextListener.getHost();
//		String port = SeqAPassServletContextListener.getPort();
//		String protocol = SeqAPassServletContextListener.getProtocol();
//		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/requestDomains";
//
//		serviceUrl += "?accessionIdName=" + accessionIdName + "&userId=" + userId ;
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.set("Cookie", session.getAttribute("COOKIE").toString());
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
//
//		ResponseEntity<LevelTwoRequestableRow[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
//				LevelTwoRequestableRow[].class);
//
//		List<LevelTwoRequestableRow> theList = Arrays.asList(response.getBody());
//
//		return theList;
//	}
	
	static public List<LevelTwoRequestableRow> getLevelTwoDomainsNew(int accessionRunId, int userId) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/requestDomainsNew";

		serviceUrl += "?accessionRunId=" + accessionRunId + "&userId=" + userId ;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<LevelTwoRequestableRow[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
				LevelTwoRequestableRow[].class);

		List<LevelTwoRequestableRow> theList = Arrays.asList(response.getBody());

		return theList;
	}
	
	
	static public List<LevelThreeRequestableRow> getCompletedLevelThree(int accessionRunId, int userId) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/requestCompleteLevelThree";

		serviceUrl += "?accessionRunId=" + accessionRunId +"&userId=" + userId;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<LevelThreeRequestableRow[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
				LevelThreeRequestableRow[].class);

		List<LevelThreeRequestableRow> theList = Arrays.asList(response.getBody());

		return theList;
	}
	
	public static List<LevelFourRequestableRow> getStartedLevelFour(int accessionRunId, int userId) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/requestStartedLevelFour";

		serviceUrl += "?accessionRunId=" + accessionRunId +"&userId=" + userId;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<LevelFourRequestableRow[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
				LevelFourRequestableRow[].class);

		List<LevelFourRequestableRow> theList = Arrays.asList(response.getBody());

		return theList;
}
	
	static public List<LevelFourRequestableRow> getCreatedLevelFour(int accessionRunId, int userId) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/requestCreatedLevelFour";

		serviceUrl += "?accessionRunId=" + accessionRunId +"&userId=" + userId;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<LevelFourRequestableRow[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
				LevelFourRequestableRow[].class);

		List<LevelFourRequestableRow> theList = Arrays.asList(response.getBody());

		return theList;
	}
	
	public static int getLevel4DataCount(int level4RunId) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/getLevelFourDataCount";

		serviceUrl += "?level4RunId=" + level4RunId;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<Integer> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
				Integer.class);

		int count = response.getBody();

		return count;
	}
	
	public static List<LevelFourAccessionRow> getLevel4Data(int level4RunId) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/getLevelFourDataRows";

		serviceUrl += "?level4RunId=" + level4RunId;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<LevelFourAccessionRow[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
				LevelFourAccessionRow[].class);

		List<LevelFourAccessionRow> rows = Arrays.asList(response.getBody());

		return rows;
	}
	
	public static List<LevelFourStatusRow> getLevelFourStatusForUser() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/levelFourStatus";
		
		User theUser = (User) session.getAttribute("UserInfo");
		int userId = theUser.getUserid();

		serviceUrl += "?userid=" + userId;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<LevelFourStatusRow[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
				LevelFourStatusRow[].class);

		List<LevelFourStatusRow> theList = Arrays.asList(response.getBody());

		return theList;
	}
	
	public static List<LevelFourStatusRow> getTMAlignStatusForUser() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/tmalignStatus";
		
		User theUser = (User) session.getAttribute("UserInfo");
		int userId = theUser.getUserid();

		serviceUrl += "?userid=" + userId;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<LevelFourStatusRow[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
				LevelFourStatusRow[].class);

		List<LevelFourStatusRow> theList = Arrays.asList(response.getBody());

		return theList;
	}
	
	public static List<LevelFourResultRow> getLevel4Results(int level4RunId) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/getLevelFourResultRows";

		serviceUrl += "?level4RunId=" + level4RunId;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<LevelFourResultRow[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
				LevelFourResultRow[].class);

		List<LevelFourResultRow> rows = Arrays.asList(response.getBody());

		return rows;
	}
	
	public static List<LevelFourRequestableRow> getAvailableTmalignReports(int userId, List<Integer> level4RunIds) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/getAvailableTmalignReports";

		serviceUrl += "?userId=" + userId;
		serviceUrl += "&level4RunIds=" + level4RunIds.toString().replaceAll("[\\[|\\]|\\s]","");

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<LevelFourRequestableRow[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
				LevelFourRequestableRow[].class);

		List<LevelFourRequestableRow> rows = Arrays.asList(response.getBody());

		return rows;
	}
	
	
	public static List<LevelFourResultRow> getTmalignReport(int userId, int tmalignRunId) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/getTmalignReport";

		serviceUrl += "?userId=" + userId;
		serviceUrl += "&tmAlignRunId=" + tmalignRunId;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<LevelFourResultRow[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
				LevelFourResultRow[].class);

		List<LevelFourResultRow> rows = Arrays.asList(response.getBody());

		return rows;
	}
	
//	public static List<LevelFourAccessionRow> getLevelFourReportForUser(int accessionRunId, int lev4Id) {
//		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
//		RestTemplate restTemplate = new RestTemplate();
//
//		String hostName = SeqAPassServletContextListener.getHost();
//		String port = SeqAPassServletContextListener.getPort();
//		String protocol = SeqAPassServletContextListener.getProtocol();
//		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/levelFourReport";
//
//		serviceUrl += "?accessionRunId=" + accessionRunId + "&lev4Id=" + lev4Id;
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.set("Cookie", session.getAttribute("COOKIE").toString());
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
//
//		ResponseEntity<LevelFourAccessionRow[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
//				LevelFourAccessionRow[].class);
//
//		List<LevelFourAccessionRow> theList = Arrays.asList(response.getBody());
//
//		return theList;
//	}
	
	static public String requestLevelThreeSequence(int levelThreeRunId) {

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();

		User theUser = (User) session.getAttribute("UserInfo");

		RestTemplate restTemplate = new RestTemplate();
		String url = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/levelThreeSequence";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.set("UserID", Integer.toString(theUser.getUserid()));
		headers.setContentType(MediaType.APPLICATION_JSON);

		url += "?levelThreeRunId=" + levelThreeRunId ;

		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

		String sequence = response.getBody();
		return sequence;
	}

	public static Long getNcbiDate() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		
		User theUser = (User) session.getAttribute("UserInfo");
		
		RestTemplate restTemplate = new RestTemplate();
		String url = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/ncbiDate";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.set("UserID", Integer.toString(theUser.getUserid()));
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		ResponseEntity<Long> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, long.class);

		Long date = response.getBody();
		return date;
	}
	
//	public static Long getLevelOneNcbiDate(int accessionRunId) {
//		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
//		String hostName = SeqAPassServletContextListener.getHost();
//		String port = SeqAPassServletContextListener.getPort();
//		String protocol = SeqAPassServletContextListener.getProtocol();
//		
//		User theUser = (User) session.getAttribute("UserInfo");
//		
//		RestTemplate restTemplate = new RestTemplate();
//		String url = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/levelOneNCBIDate";
//		url += "?accessionRunId=" + accessionRunId ;
//		HttpHeaders headers = new HttpHeaders();
//		headers.set("Cookie", session.getAttribute("COOKIE").toString());
//		headers.set("UserID", Integer.toString(theUser.getUserid()));
//		headers.setContentType(MediaType.APPLICATION_JSON);
//
//		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
//		ResponseEntity<Long> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, long.class);
//
//		Long date = response.getBody();
//		return date;
//	}
	
	public static ReportInfo getReportInfo(int accessionRunId, int lev2Id, int lev3Id, int lev4Id) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();
		
		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String url = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/reportInfo";
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
				.queryParam("accessionRunId",accessionRunId)
				.queryParam("lev2Id", lev2Id)
				.queryParam("lev3Id", lev3Id)
				.queryParam("lev4Id", lev4Id);
		ResponseEntity<ReportInfo> response = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, ReportInfo.class);
		
		ReportInfo info = response.getBody();
		return info;
	}
	
	
	public static String getBackendInfo() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();
		
		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String url = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/backendInfo";
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
		String info = response.getBody();
		
		return info;
	}
	
	static public List<Integer> getBadTaxGroupIds() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/getBadTaxGroupIds";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<Integer[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
				Integer[].class);

		List<Integer> theList = Arrays.asList(response.getBody());
		

		return theList;
	}
	
	
	
	static public String getSciNameAtTaxId(String accessionId, String rank) {

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();

		User theUser = (User) session.getAttribute("UserInfo");

		RestTemplate restTemplate = new RestTemplate();
		String url = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/getSciNameAtRank";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.set("UserID", Integer.toString(theUser.getUserid()));
		headers.setContentType(MediaType.APPLICATION_JSON);

		url += "?accessionId=" + accessionId ;
		url += "&rank=" + rank;

		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

		String sciName = response.getBody();
		return sciName;
	}
	
	static public Protein getProtein(String accessionId) {

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();

		User theUser = (User) session.getAttribute("UserInfo");

		RestTemplate restTemplate = new RestTemplate();
		String url = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/protein/";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.set("UserID", Integer.toString(theUser.getUserid()));
		headers.setContentType(MediaType.APPLICATION_JSON);

		url += accessionId;

		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		ResponseEntity<Protein> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Protein.class);

		Protein theProtein = response.getBody();
		return theProtein;
	}

	
//	static public List<DensityRow> getDensityData(int level, int reportType, int runId, double eValue, int commonDomains) {
//		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
//		RestTemplate restTemplate = new RestTemplate();
//
//		String hostName = SeqAPassServletContextListener.getHost();
//		String port = SeqAPassServletContextListener.getPort();
//		String protocol = SeqAPassServletContextListener.getProtocol();
//		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/getDensityData";
//
//		serviceUrl += "?level=" + level;
//		serviceUrl += "&reportType=" + reportType;
//		serviceUrl += "&runId=" + runId;
//		serviceUrl += "&eValue=" + eValue;
//		serviceUrl += "&commonDomains=" + commonDomains;
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.set("Cookie", session.getAttribute("COOKIE").toString());
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
//
//		ResponseEntity<DensityRow[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
//				DensityRow[].class);
//
//		List<DensityRow> theList = Arrays.asList(response.getBody());
//		
//
//		return theList;
//	}
	
	
	static public List<TaxEcos> getEndangered() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/getEndangered";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<TaxEcos[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
				TaxEcos[].class);

		List<TaxEcos> theList = Arrays.asList(response.getBody());
		

		return theList;
	}
	
	static public List<TaxEcos> getThreatened() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/getThreatened";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<TaxEcos[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
				TaxEcos[].class);

		List<TaxEcos> theList = Arrays.asList(response.getBody());
		

		return theList;
	}
	
	static public List<Integer> getModelOrganisms() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/getModelOrganisms";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<Integer[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
				Integer[].class);

		List<Integer> theList = Arrays.asList(response.getBody());
		

		return theList;
	}
	
	static public List<AminoAcid> getAminoAcidInfo() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/getAminoAcidInfo";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<AminoAcid[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
				AminoAcid[].class);

		List<AminoAcid> theList = Arrays.asList(response.getBody());
		

		return theList;
	}
	
	static public List<Chemical> getChemicalSearch(String searchStr) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/getChemicalSearch";

		serviceUrl += "?search=" + searchStr ;
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<Chemical[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
				Chemical[].class);

		List<Chemical> theList = Arrays.asList(response.getBody());
		

		return theList;
	}
	
	static public boolean pingBackend() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		long lastAccess = session.getLastAccessedTime();     // in ms
		int maxInterval = session.getMaxInactiveInterval();  //in seconds
		long currentTime = System.currentTimeMillis();
		
		int diffTime = (int)(currentTime - lastAccess)/1000;
		
		System.out.println("lastAccess: " + lastAccess);
		System.out.println("maxInterval: " + maxInterval);
		System.out.println("currentTime: " + currentTime);
		System.out.println("diffTime: " + diffTime);
		
		if (diffTime >= maxInterval) {
			return false;
		}
		
		
		
		
		RestTemplate restTemplate = new RestTemplate();
		
		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/user/ping";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		
		ResponseEntity<Boolean> responseBool = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity,
				boolean.class);
		
		boolean success = responseBool.getBody();
		
		return success;
	}

	public static List<UniprotMap> getUniprotMappings(List<String> accs) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/getUniprotMaps";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<?> requestEntity = new HttpEntity<Object>(accs, headers);
		ResponseEntity<UniprotMap[]> response = restTemplate.exchange(serviceUrl, HttpMethod.POST, requestEntity, UniprotMap[].class);

		List<UniprotMap> resultMap = Arrays.asList(response.getBody());
		
		return resultMap;
	}	
	
	public static List<UniprotMap> getUniprotMappingsByAccId(int accessionRunId, boolean eukaryotesOnly) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		RestTemplate restTemplate = new RestTemplate();

		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();
		String serviceUrl = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/report/getUniprotMapsByAccId";
		
		serviceUrl += "?accessionRunId=" + accessionRunId;
		serviceUrl += "&euksOnly=" + eukaryotesOnly;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<?> requestEntity = new HttpEntity<Object>(headers);
		ResponseEntity<UniprotMap[]> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, requestEntity, UniprotMap[].class);

		List<UniprotMap> resultMap = Arrays.asList(response.getBody());
		
		return resultMap;
	}
	

	
}
