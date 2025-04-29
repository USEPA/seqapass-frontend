package gov.epa.seqapass.bean;

import gov.epa.seqapass.common.LevelOneRequestable;
import gov.epa.seqapass.common.Protein;
//import gov.epa.seqapass.common.ReportInfo;
import gov.epa.seqapass.common.Species;
import gov.epa.seqapass.common.UserMessage;
import gov.epa.seqapass.common.Link;
import gov.epa.seqapass.controller.LoginController;
import gov.epa.seqapass.controller.RequestRunController;
import gov.epa.seqapass.listener.SeqAPassServletContextListener;
import gov.epa.seqapass.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ManagedBean
@SessionScoped
public class RequestRunView {

	@PostConstruct
	public void init() {
		speciesList = new ArrayList<Species>();
		selectedSpeciesList = new ArrayList<Species>();
		finalProteinList = new ArrayList<Protein>();
		finalSelectedProteinList = new ArrayList<Protein>();
		searchName = "request_run_by-species.xhtml";
		searchType = "0";
		resetPage = false;
	}

	// /////////////////////////////////////////////
	// request Run variable and methods
	// //////////////////////////////////////////////

	private String searchType;
	private String searchName;
	private boolean resetPage;
	private boolean disableSubmit;

	private static String failure1 = "accession id not found";
	private static String failure2 = "could not create fasta file";
	private static String failure3 = "could not write fasta file";
	private static String failure4 = "first line of fasta contains unacceptable characters";
	private static String failure5 = "fasta sequence contains unacceptable characters";


	public String changeSearchName() {
		switch (searchType) {
		case "0":
			searchName = "request_run_by-species.xhtml";
			break;
		case "1":
			searchName = "request_run_by-genbankid.xhtml";
			break;
		case "2":
			searchName = "request_run_by-accessionlist.xhtml";
			break;
		default:
			System.out.println("ERROR: Unknown search type " + searchType);
			return null;
		}
		return searchName;
	}

	/**
	 * Checks database for submit message and displays message if not null/empty
	 * @return boolean specifying whether submissions are allowed
	 */
	public boolean displaySubmitMessage() {
		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		UserLoginView userLoginView = (UserLoginView) FacesContext.getCurrentInstance().getApplication().getELResolver()
				.getValue(elContext, null, "userLoginView");

		FacesContext context = FacesContext.getCurrentInstance();
		UserMessage usrMsg = userLoginView.getUserMessage();
		Long submitBlockTime = usrMsg.getSubmitBlockTime();
		String submitPreBlockmsg = usrMsg.getSubmitPreBlockMsg();
		String submitPostBlockmsg = usrMsg.getSubmitPostBlockMsg();
		if (submitBlockTime == null || submitBlockTime > System.currentTimeMillis()) {
			if (submitPreBlockmsg != null && !submitPreBlockmsg.isEmpty()) {
				context.addMessage("userMessage", new FacesMessage(FacesMessage.SEVERITY_WARN, submitPreBlockmsg, ""));
			}
			// this.disableSubmit = false;
			System.out.println("Allowing submit");
			return true;
		} else {
			// after submitBlockTime
			if (submitPostBlockmsg != null && !submitPostBlockmsg.isEmpty()) {
				System.out.println("adding submit postBlockmsg");
				context.addMessage("userMessage", new FacesMessage(FacesMessage.SEVERITY_WARN, submitPostBlockmsg, ""));
			}
			// this.disableSubmit = true;
			return false;
		}

	}

	// ///////////////////////////////////////////////////
	// By accessionList variables and methods
	// //////////////////////////////////////////////////

	private String inputText;
	private List<String> accessionList;

	public void clearAccListButton() {
		inputText = "";
	}


	public void requestRunByAccButton() {
		FacesContext context = FacesContext.getCurrentInstance();
		accessionList = new ArrayList<String>();
		// String entries[] = inputText.split("\\s|\\n");

		// display user submit message if any (return value controls whether submission is allowed)
		if (displaySubmitMessage()) {

			Pattern accessionListPattern = Pattern.compile("[^a-zA-Z0-9:=_,.-]");

			if (inputText.trim().length() == 0) {
				context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Must enter NCBI accession"));
				return;
			}

			String entries[] = inputText.split("\\s+");
			for (String entry : entries) {
				Matcher m = accessionListPattern.matcher(entry);
				if (m.find()) {
					context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
							"Only alphanumeric and : = _ , . - allowed"));
					return;
				}
				accessionList.add(entry);
			}

			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			String hostName = SeqAPassServletContextListener.getHost();
			String port = SeqAPassServletContextListener.getPort();
			String protocol = SeqAPassServletContextListener.getProtocol();

			User theUser = (User) session.getAttribute("UserInfo");

			RestTemplate restTemplate = new RestTemplate();
			String url = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/analysis/levelOneRun/";
			HttpHeaders headers = new HttpHeaders();
			headers.set("Cookie", session.getAttribute("COOKIE").toString());
			headers.set("UserID", Integer.toString(theUser.getUserid()));
			headers.setContentType(MediaType.APPLICATION_JSON);

			LevelOneRequestable requestObject = new LevelOneRequestable(accessionList, theUser.getUserid());
			HttpEntity<?> requestEntity = new HttpEntity<Object>(requestObject, headers);

			ResponseEntity<List<String>> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					new ParameterizedTypeReference<List<String>>() {
					});
			List<String> result = new ArrayList<String>();
			result = response.getBody();

			if (result.size() > 0) {
				for (String message : result) {
					String lowMes = message.toLowerCase();
					if (lowMes.equals(failure1) || lowMes.equals(failure2) || lowMes.equals(failure3) || lowMes.equals(failure4)
							|| lowMes.equals(failure5)) {
						context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", message));
					} else {
						context.addMessage("growl", new FacesMessage("Success", message));
					}
				}
			} else {
				context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure", "Error in submitting accessions"));
			}
			clearAccListButton();
			// setSearchType("0");
			// changeSearchName();
			setResetPage(true);
		} else {
			context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_WARN, "Submissions Disabled", "Please try again later."));
		}

		// get the latest update info
		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		UserLoginView userLoginView = (UserLoginView) FacesContext.getCurrentInstance().getApplication().getELResolver()
				.getValue(elContext, null, "userLoginView");
		userLoginView.setUpdateInfo(LoginController.getUpdateInfo());

	}

	// ////////////////////////////////////////////////
	// By Species variables and methods
	// //////////////////////////////////////////////

	private Species searchSpecies;
	private Species selectedSpeciesItem;

	private String queryProteinString;

	private List<Species> speciesList;
	private List<Species> selectedSpeciesList;

	private List<Protein> proteinList;
	private List<Protein> selectedProteinList;
	private List<Protein> finalProteinList;
	private List<Protein> finalSelectedProteinList;

	private String searchText;

	

	// autocomplete query
	public List<Species> completeSpecies(String query) {

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();

		RestTemplate restTemplate = new RestTemplate();
		String url = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/species/?search=" + query;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		ResponseEntity<List<Species>> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
				new ParameterizedTypeReference<List<Species>>() {
				});

		setSpeciesList(response.getBody());

		return speciesList;

	}

	public void addSpeciesButton() {

		if (searchSpecies != null && !selectedSpeciesList.contains(searchSpecies)) {
			selectedSpeciesList.add(searchSpecies);
		} else {
			System.out.println("returning  BAD!!!");
			return;
		}
		selectedSpeciesItem = searchSpecies;

		if (selectedSpeciesItem != null) {
			onSpeciesSelected();
		}
		setSearchSpecies(null);

	}

	public void onSpeciesSelected() {

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();

		RestTemplate restTemplate = new RestTemplate();
		String url = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/protein/?taxid="
				+ selectedSpeciesItem.getTaxId();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		ResponseEntity<List<Protein>> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
				new ParameterizedTypeReference<List<Protein>>() {
				});

		proteinList = response.getBody();

	}

	public void filterProteinButton() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String hostName = SeqAPassServletContextListener.getHost();
		String port = SeqAPassServletContextListener.getPort();
		String protocol = SeqAPassServletContextListener.getProtocol();

		RestTemplate restTemplate = new RestTemplate();
		String url = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/protein/?taxid="
				+ selectedSpeciesItem.getTaxId() + "&search=" + queryProteinString;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", session.getAttribute("COOKIE").toString());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		if (selectedSpeciesItem != null) {
			ResponseEntity<List<Protein>> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
					new ParameterizedTypeReference<List<Protein>>() {
					});
			proteinList = response.getBody();
		}
		queryProteinString = "";
	}

	public void addProteinButton() {
		for (Protein protein : selectedProteinList) {
			if (!finalProteinList.contains(protein)) {
				finalProteinList.add(protein);
			}
		}
	}

	public void addAllProteinButton() {
		for (Protein protein : proteinList) {
			if (!finalProteinList.contains(protein)) {
				finalProteinList.add(protein);
			}
		}
	}

	public void removeProteinButton() {
		for (Protein protein : finalSelectedProteinList) {
			finalProteinList.remove(protein);
		}
	}

	public void removeAllProteinButton() {
		finalProteinList.clear();
	}

	public void clearButton() {
		selectedSpeciesList.clear();
		proteinList.clear();
		selectedProteinList.clear();
		finalProteinList.clear();
		finalSelectedProteinList.clear();
		searchSpecies = null;
		queryProteinString = "";

	}

	public void pageReload() {
		searchSpecies = null;
	}

	public void requestRunButton() {
		FacesContext context = FacesContext.getCurrentInstance();

		// display user submit message if any (return value controls whether submission is allowed)
		if (displaySubmitMessage()) {
			if (finalProteinList.size() == 0) {
				context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Must select query proteins"));
				return;
			}

			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			String hostName = SeqAPassServletContextListener.getHost();
			String port = SeqAPassServletContextListener.getPort();
			String protocol = SeqAPassServletContextListener.getProtocol();

			User theUser = (User) session.getAttribute("UserInfo");

			RestTemplate restTemplate = new RestTemplate();
			String url = protocol + "://" + hostName + ":" + port + "/SeqAPASS-BE/protected/service/analysis/levelOneRun/";
			HttpHeaders headers = new HttpHeaders();
			headers.set("Cookie", session.getAttribute("COOKIE").toString());
			headers.set("UserID", Integer.toString(theUser.getUserid()));
			headers.setContentType(MediaType.APPLICATION_JSON);

			List<String> accessions = new ArrayList<String>();
			for (Protein p : finalProteinList) {
				accessions.add(p.getAccession());
			}

			LevelOneRequestable requestObject = new LevelOneRequestable(accessions, theUser.getUserid());
			HttpEntity<?> requestEntity = new HttpEntity<Object>(requestObject, headers);

			ResponseEntity<List<String>> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					new ParameterizedTypeReference<List<String>>() {
					});
			List<String> result = new ArrayList<String>();
			result = response.getBody();

			if (result.size() > 0) {
				for (String acc : result) {
					context.addMessage("growl", new FacesMessage("Success", "Submitted " + acc));
				}
			} else {
				context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure", "Error in submitting accessions"));
			}
			clearButton();

			// get the latest update info
			ELContext elContext = FacesContext.getCurrentInstance().getELContext();
			UserLoginView userLoginView = (UserLoginView) FacesContext.getCurrentInstance().getApplication().getELResolver()
					.getValue(elContext, null, "userLoginView");
			userLoginView.setUpdateInfo(LoginController.getUpdateInfo());

		}
	}
	
	public List<Link> getLinks(String listName) {		
		return RequestRunController.getLinks(listName);
	}
	
	/***********************************************************
	 * Getter and Setter Methods
	 ********************************************************/

	public Species getSearchSpecies() {
		return searchSpecies;
	}

	public void setSearchSpecies(Species searchSpecies) {
		this.searchSpecies = searchSpecies;
	}

	public List<Species> getSpeciesList() {
		return speciesList;
	}

	public void setSpeciesList(List<Species> speciesList) {
		this.speciesList = speciesList;
	}

	public List<Species> getSelectedSpeciesList() {
		return selectedSpeciesList;
	}

	public void setSelectedSpeciesList(List<Species> selectedSpeciesList) {
		this.selectedSpeciesList = selectedSpeciesList;
	}

	public Species getSelectedSpeciesItem() {
		return selectedSpeciesItem;
	}

	public void setSelectedSpeciesItem(Species selectedSpeciesItem) {
		this.selectedSpeciesItem = selectedSpeciesItem;
	}

	public List<Protein> getProteinList() {
		return proteinList;
	}

	public void setProteinList(List<Protein> proteinList) {
		this.proteinList = proteinList;
	}

	public List<Protein> getSelectedProteinList() {
		return selectedProteinList;
	}

	public void setSelectedProteinList(List<Protein> selectedProteinList) {
		this.selectedProteinList = selectedProteinList;
	}

	public String getQueryProteinString() {
		return queryProteinString;
	}

	public void setQueryProteinString(String queryProteinString) {
		this.queryProteinString = queryProteinString;
	}

	public List<Protein> getFinalProteinList() {
		return finalProteinList;
	}

	public void setFinalProteinList(List<Protein> finalProteinList) {
		this.finalProteinList = finalProteinList;
	}

	public List<Protein> getFinalSelectedProteinList() {
		return finalSelectedProteinList;
	}

	public void setFinalSelectedProteinList(List<Protein> finalSelectedProteinList) {
		this.finalSelectedProteinList = finalSelectedProteinList;
	}

	public String getInputText() {
		return inputText;
	}

	public void setInputText(String inputText) {
		this.inputText = inputText;
	}

	public List<String> getAccessionList() {
		return accessionList;
	}

	public void setAccessionList(List<String> accessionList) {
		this.accessionList = accessionList;
	}

	public boolean isResetPage() {
		return resetPage;
	}

	public void setResetPage(boolean resetPage) {
		this.resetPage = resetPage;
	}

	public boolean isDisableSubmit() {
		return disableSubmit;
	}

	public void setDisableSubmit(boolean disableSubmit) {
		this.disableSubmit = disableSubmit;
	}
	
	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}
	
	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

}