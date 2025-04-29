package gov.epa.seqapass.bean;

import gov.epa.seqapass.common.LevelFourStatusRow;
//import gov.epa.seqapass.common.LevelOneReportRow;
import gov.epa.seqapass.common.LevelOneStatusRow;
import gov.epa.seqapass.common.LevelThreeStatusRow;
import gov.epa.seqapass.common.LevelTwoStatusRow;
import gov.epa.seqapass.controller.ReportController;

//import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
//import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
//import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class StatusView {

	private String statusLevel;
	private String statusPage;
	private String selectedAction;

	@PostConstruct
	public void init() {
		statusPage = "levelOneStatus.xhtml";
		statusLevel = "1";
		selectedAction = "";
		
		// Check if user has been logged in.  If not, call login method.
		// This method runs when user first reaches the logged-in version of index.xhtml
//		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
//		UserLoginView userLoginView = (UserLoginView) FacesContext.getCurrentInstance().getApplication().getELResolver()
//				.getValue(elContext, null, "userLoginView");
//		String userEmail = userLoginView.getEmail();
//		if (userEmail == null || userEmail.isEmpty()){
//			boolean success = userLoginView.login();
//			if (!success) {
//				return;
//			}
//		}
		
		setLevelOneStatusList(ReportController.getLevelOneStatusForUser());
		setLevelTwoStatusList(ReportController.getLevelTwoStatusForUser());
		setLevelThreeStatusList(ReportController.getLevelThreeStatusForUser());
		setLevelFourStatusList(ReportController.getLevelFourStatusForUser());
		
		filteredLevelOneStatusList.clear();
		for (int i = 0; i < levelOneStatusList.size(); i++) {
			filteredLevelOneStatusList.add(levelOneStatusList.get(i));
		}
		
		filteredLevelTwoStatusList.clear();
		for (int i = 0; i < levelTwoStatusList.size(); i++){
			filteredLevelTwoStatusList.add(levelTwoStatusList.get(i));
		}
		
		filteredLevelThreeStatusList.clear();
		for (int i = 0; i < levelThreeStatusList.size(); i++){
			filteredLevelThreeStatusList.add(levelThreeStatusList.get(i));
		}
		
		filteredLevelFourStatusList.clear();
		for (int i = 0; i < levelFourStatusList.size(); i++){
			filteredLevelFourStatusList.add(levelFourStatusList.get(i));
		}

	}

	List<LevelOneStatusRow> levelOneStatusList = new ArrayList<LevelOneStatusRow>();
	List<LevelOneStatusRow> filteredLevelOneStatusList = new ArrayList<LevelOneStatusRow>();
	List<LevelTwoStatusRow> levelTwoStatusList = new ArrayList<LevelTwoStatusRow>();
	List<LevelTwoStatusRow> filteredLevelTwoStatusList = new ArrayList<LevelTwoStatusRow>();
	List<LevelThreeStatusRow> levelThreeStatusList = new ArrayList<LevelThreeStatusRow>();
	List<LevelThreeStatusRow> filteredLevelThreeStatusList = new ArrayList<LevelThreeStatusRow>();
	List<LevelFourStatusRow> levelFourStatusList = new ArrayList<LevelFourStatusRow>();
	List<LevelFourStatusRow> filteredLevelFourStatusList = new ArrayList<LevelFourStatusRow>();
	List<LevelFourStatusRow> tmAlignStatusList = new ArrayList<LevelFourStatusRow>();
	List<LevelFourStatusRow> filteredTMAlignStatusList = new ArrayList<LevelFourStatusRow>();
	List<LevelOneStatusRow> selectedLevelOneRuns = new ArrayList<LevelOneStatusRow>();
	List<LevelTwoStatusRow> selectedLevelTwoRuns = new ArrayList<LevelTwoStatusRow>();
	List<LevelThreeStatusRow> selectedLevelThreeRuns = new ArrayList<LevelThreeStatusRow>();
	private String queryString;

	public String changeStatusLevel() {
		switch (statusLevel) {
		case "1":
			statusPage = "levelOneStatus.xhtml";
			refreshLevelOneStatus();
			break;
		case "2":
			statusPage = "levelTwoStatus.xhtml";
			refreshLevelTwoStatus();
			break;
		case "3":
			statusPage = "levelThreeStatus.xhtml";
			refreshLevelThreeStatus();
			break;
		case "4":
			statusPage = "levelFourStatus.xhtml";
			refreshLevelFourStatus();
			break;
		default:
			return null;
		}
		return statusPage;
	}

	public void deleteLevelOneRecord() {
	}

	public void deleteLevelTwoRecord() {
	}

	public void deleteLevelThreeRecord() {
	}

	/**
	 * Converts job length from seconds to formatted string with number of weeks, days, hours, minutes, and seconds
	 * @param timeRemaining (long)
	 * @return string containing formatted job length
	 */
	public String convertRunDuration(long timeRemaining) {
		if (timeRemaining > 0) {
			int weeks = (int) TimeUnit.SECONDS.toDays(timeRemaining / 7);
			timeRemaining -= TimeUnit.DAYS.toSeconds(weeks * 7);
			int days = (int) TimeUnit.SECONDS.toDays(timeRemaining);
			timeRemaining -= TimeUnit.DAYS.toSeconds(days);
			int hours = (int) TimeUnit.SECONDS.toHours(timeRemaining);
			timeRemaining -= TimeUnit.HOURS.toSeconds(hours);
			int minutes = (int) TimeUnit.SECONDS.toMinutes(timeRemaining);
			int seconds = (int) (timeRemaining - TimeUnit.MINUTES.toSeconds(minutes));
			String result = "";
			if (weeks > 0)
				result = String.format("%d Week(s) %d day(s) %d hour(s) %d minute(s) %d second(s)", weeks, days, hours, minutes, seconds);
			else if (days > 0)
				result = String.format("%d day(s) %d hour(s) %d minute(s) %d second(s)", days, hours, minutes, seconds);
			else if (hours > 0)
				result = String.format("%d hour(s) %d minute(s) %d second(s)", hours, minutes, seconds);
			else if (minutes > 0)
				result = String.format("%d minute(s) %d second(s)", minutes, seconds);
			else
				result = String.format("%d seconds", seconds);

			return result;
		} else if (timeRemaining == 0) {
			return "-";
		} else
			return "EndDate < StartDate";
	}

	public void refreshButton() {
		refreshLevelOneStatus();
		refreshLevelTwoStatus();
		refreshLevelThreeStatus();
		refreshLevelFourStatus();
	}

	public void refreshLevelOneStatus() {
		setLevelOneStatusList(ReportController.getLevelOneStatusForUser());
		
		filteredLevelOneStatusList.clear();
		for (int i = 0; i < levelOneStatusList.size(); i++) {
			filteredLevelOneStatusList.add(levelOneStatusList.get(i));
		}
	}

	public void refreshLevelTwoStatus() {
		setLevelTwoStatusList(ReportController.getLevelTwoStatusForUser());
		
		filteredLevelTwoStatusList.clear();
		for (int i = 0; i < levelTwoStatusList.size(); i++){
			filteredLevelTwoStatusList.add(levelTwoStatusList.get(i));
		}
	}

	public void refreshLevelThreeStatus() {
		setLevelThreeStatusList(ReportController.getLevelThreeStatusForUser());

		filteredLevelThreeStatusList.clear();
		for (int i = 0; i < levelThreeStatusList.size(); i++){
			filteredLevelThreeStatusList.add(levelThreeStatusList.get(i));
		}
	}
	
	public void refreshLevelFourStatus() {
		setLevelFourStatusList(ReportController.getLevelFourStatusForUser());

		filteredLevelFourStatusList.clear();
		filteredLevelFourStatusList.addAll(levelFourStatusList);
//		for (int i = 0; i < levelFourStatusList.size(); i++){
//			filteredLevelFourStatusList.add(levelFourStatusList.get(i));
//		}
		
		setTmAlignStatusList(ReportController.getTMAlignStatusForUser());
		
		filteredTMAlignStatusList.clear();
		filteredTMAlignStatusList.addAll(tmAlignStatusList);
	}

	public void refreshAndResetStatus() {
		setStatusLevel("1");
		changeStatusLevel();
	}

	public String formatStatus(String input) {
		String result = input.replaceAll("%", "");
		try {
			Integer num = 99 - Integer.parseInt(input);
			NumberFormat newFormat = new DecimalFormat("00");
			result = "     " + newFormat.format(num);
		} catch (NumberFormatException e) {
		}

		return result;
	}

	/**
	 * Sorts status strings for datatable
	 * @param obj1 
	 * @param obj2
	 * @return 1 for correct order, 0 for equal, -1 for swap order
	 */
	public int sortStatus(Object obj1, Object obj2) {
		String obj1Type = null;
		String obj2Type = null;
		int obj1Int = 0;
		int obj2Int = 0;
		int result = 0;

		// Determine type of each object ("queue", "perc", "text")
		if (obj1.toString().contains("in queue")) {
			obj1Type = "queue";
		} else {
			try {
				obj1Int = Integer.parseInt(obj1.toString());
				obj1Type = "perc";
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				obj1Type = "text";
			}
		}
		if (obj2.toString().contains("in queue")) {
			obj2Type = "queue";
		} else {
			try {
				obj2Int = Integer.parseInt(obj2.toString());
				obj2Type = "perc";
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				obj2Type = "text";
			}
		}

		// Sort each item
		if (obj1Type.equals(obj2Type)) {
			if (obj1Type.equals("perc")) {
				// both objects are percentages, sort by integer
				obj1Int = Integer.parseInt(obj1.toString().replaceAll("%", "").trim());
				obj2Int = Integer.parseInt(obj2.toString().replaceAll("%", "").trim());
				result = statusIntSort(obj1Int, obj2Int, "asc");
			} else if (obj1Type.equals("queue")) {
				obj1Int = Integer.parseInt(obj1.toString().replaceAll(".. in queue", "").trim());
				obj2Int = Integer.parseInt(obj2.toString().replaceAll(".. in queue", "").trim());
				result = statusIntSort(obj1Int, obj2Int, "desc");
			} else {
				// both objects are text, sort by ("complete", "analyzing", "started")
				if (obj1.toString().trim().equals(obj2.toString().trim())) {
					return 0;
				} else if (obj1.toString().trim().equals("complete") || obj1.toString().trim().equals("no hits found")) {
					return 1;
				} else if (obj1.toString().trim().equals("analyzing")) {
					if (obj2.toString().trim().equals("complete") || obj2.toString().trim().equals("no hits found")) {
						return -1;
					} else {
						return 1;
					}
				} else {
					// obj1 is started
					return -1;
				}
			}
			return result;
		} else if (obj1Type.equals("perc")) {
			// obj1 is percentage
			if (obj2Type.equals("queue")) {
				return 1;
			} else {
				return 1;
			}
		} else if (obj1Type.equals("queue")) {
			// obj1 is queued
			if (obj2Type.equals("perc")) {
				return -1;
			} else {
				return -1;
			}

		} else {
			// obj1 is text
			if (obj2Type.equals("perc")) {
				return 1;
			} else {
				return 1;
			}
		}

	}

	// used for determining proper order based on ascending or descending sort
	public int statusIntSort(int obj1Int, int obj2Int, String direction) {
		if (obj1Int < obj2Int) {
			if (direction.equals("asc")) {
				return -1;
			} else
				return 1;
		} else if (obj1Int == obj2Int) {
			return 0;
		} else {
			if (direction.equals("asc")) {
				return 1;
			} else
				return -1;
		}
	}

	// getters and setters

	public List<LevelOneStatusRow> getLevelOneStatusTable() {
		// levelOneStatusList = StatusQuery.getLevelOneStatusList();
		return levelOneStatusList;
	}

	public List<LevelTwoStatusRow> getLevelTwoStatusTable() {
		// levelTwoStatusList = StatusQuery.getLevelTwoStatusList();
		return levelTwoStatusList;
	}

	public List<LevelThreeStatusRow> getLevelThreeStatusTable() {
		// levelThreeStatusList = StatusQuery.getLevelThreeStatusList();
		return levelThreeStatusList;
	}

	public List<LevelOneStatusRow> getLevelOneStatusList() {
		return levelOneStatusList;
	}

	public void setLevelOneStatusList(List<LevelOneStatusRow> levelOneStatusList) {
		this.levelOneStatusList = levelOneStatusList;
	}

	public List<LevelTwoStatusRow> getLevelTwoStatusList() {
		return levelTwoStatusList;
	}

	public void setLevelTwoStatusList(List<LevelTwoStatusRow> levelTwoStatusList) {
		this.levelTwoStatusList = levelTwoStatusList;
	}

	public List<LevelThreeStatusRow> getLevelThreeStatusList() {
		return levelThreeStatusList;
	}

	public void setLevelThreeStatusList(List<LevelThreeStatusRow> levelThreeStatusList) {
		this.levelThreeStatusList = levelThreeStatusList;
	}

	public String getStatusLevel() {
		return statusLevel;
	}

	public void setStatusLevel(String statusLevel) {
		this.statusLevel = statusLevel;
	}

	public String getStatusPage() {
		return statusPage;
	}

	public void setStatusPage(String statusPage) {
		this.statusPage = statusPage;
	}

	public String getSelectedAction() {
		return selectedAction;
	}

	public void setSelectedAction(String selectedAction) {
		this.selectedAction = selectedAction;
	}

	public List<LevelOneStatusRow> getSelectedLevelOneRuns() {
		return selectedLevelOneRuns;
	}

	public void setSelectedLevelOneRuns(List<LevelOneStatusRow> selectedLevelOneRuns) {
		this.selectedLevelOneRuns = selectedLevelOneRuns;
	}

	public List<LevelTwoStatusRow> getSelectedLevelTwoRuns() {
		return selectedLevelTwoRuns;
	}

	public void setSelectedLevelTwoRuns(List<LevelTwoStatusRow> selectedLevelTwoRuns) {
		this.selectedLevelTwoRuns = selectedLevelTwoRuns;
	}

	public List<LevelThreeStatusRow> getSelectedLevelThreeRuns() {
		return selectedLevelThreeRuns;
	}

	public void setSelectedLevelThreeRuns(List<LevelThreeStatusRow> selectedLevelThreeRuns) {
		this.selectedLevelThreeRuns = selectedLevelThreeRuns;
	}

	public List<LevelOneStatusRow> getFilteredLevelOneStatusList() {
		return filteredLevelOneStatusList;
	}

	public void setFilteredLevelOneStatusList(List<LevelOneStatusRow> filteredLevelOneStatusList) {
		this.filteredLevelOneStatusList = filteredLevelOneStatusList;
	}

	public List<LevelTwoStatusRow> getFilteredLevelTwoStatusList() {
		return filteredLevelTwoStatusList;
	}

	public void setFilteredLevelTwoStatusList(List<LevelTwoStatusRow> filteredLevelTwoStatusList) {
		this.filteredLevelTwoStatusList = filteredLevelTwoStatusList;
	}

	public List<LevelThreeStatusRow> getFilteredLevelThreeStatusList() {
		return filteredLevelThreeStatusList;
	}

	public void setFilteredLevelThreeStatusList(List<LevelThreeStatusRow> filteredLevelThreeStatusList) {
		this.filteredLevelThreeStatusList = filteredLevelThreeStatusList;
	}
	
	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public List<LevelFourStatusRow> getLevelFourStatusList() {
		return levelFourStatusList;
	}

	public void setLevelFourStatusList(List<LevelFourStatusRow> levelFourStatusList) {
		this.levelFourStatusList = levelFourStatusList;
	}

	public List<LevelFourStatusRow> getFilteredLevelFourStatusList() {
		return filteredLevelFourStatusList;
	}

	public void setFilteredLevelFourStatusList(List<LevelFourStatusRow> filteredLevelFourStatusList) {
		this.filteredLevelFourStatusList = filteredLevelFourStatusList;
	}

	public List<LevelFourStatusRow> getTmAlignStatusList() {
		return tmAlignStatusList;
	}

	public void setTmAlignStatusList(List<LevelFourStatusRow> tmAlignStatusList) {
		this.tmAlignStatusList = tmAlignStatusList;
	}

	public List<LevelFourStatusRow> getFilteredTMAlignStatusList() {
		return filteredTMAlignStatusList;
	}

	public void setFilteredTMAlignStatusList(List<LevelFourStatusRow> filteredTMAlignStatusList) {
		this.filteredTMAlignStatusList = filteredTMAlignStatusList;
	}

}