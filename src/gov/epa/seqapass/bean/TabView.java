package gov.epa.seqapass.bean;

import javax.el.ELContext;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.event.TabChangeEvent;

import gov.epa.seqapass.common.ReportChoiceEnum;
import gov.epa.seqapass.controller.LoginController;
import gov.epa.seqapass.controller.ReportController;

@ManagedBean
@SessionScoped
public class TabView {

	public void onTabChange(TabChangeEvent event) {
		if (event.getTab().getId().equals("reportTab")) {
			resetReportPage();
		} else if (event.getTab().getId().equals("requestTab")) {
			resetRequestPage();
		} else if (event.getTab().getId().equals("statusTab")){
			resetStatusPage();
		} else if (event.getTab().getId().equals("homeTab")) {
			resetHomePage();
		} else if (event.getTab().getId().equals("settingsTab")) {
			resetSettingsPage();
		}

	}
	
	// handles changing to home tab
	public void resetHomePage() {
		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		ReportView reportView = (ReportView) FacesContext.getCurrentInstance().getApplication().getELResolver()
				.getValue(elContext, null, "reportView");
		reportView.closeIcn3dDialogs();
	}
	
	// handles changing to settings tab
		public void resetSettingsPage() {
			ELContext elContext = FacesContext.getCurrentInstance().getELContext();
			ReportView reportView = (ReportView) FacesContext.getCurrentInstance().getApplication().getELResolver()
					.getValue(elContext, null, "reportView");
			reportView.closeIcn3dDialogs();
		}

	// handles changing to report tab
	public void resetReportPage() {
		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		ReportView reportView = (ReportView) FacesContext.getCurrentInstance().getApplication().getELResolver()
				.getValue(elContext, null, "reportView");
		reportView.closeIcn3dDialogs();
		reportView.setChosenMainReportOption(ReportChoiceEnum.View);
		reportView.changeMainReportOption();
//		if (reportView.isResetLevOneToPrimary()){
//			// only change to primary if boolean is set to true
//			reportView.setLevelOneReportType(ReportTypeEnum.Primary);
//		} else {
//			// boolean set to false in cutoff.  reset to true
//			reportView.setResetLevOneToPrimary(true);
//		}
//		if (reportView.isResetLevTwoToPrimary()){
//			// only change to primary if boolean is set to true
//			reportView.setLevelTwoReportType(ReportTypeEnum.Primary);
//		} else {
//			// boolean set to false in cutoff.  reset to true
//			reportView.setResetLevTwoToPrimary(true);
//		}
		reportView.changeLevelOneReportPage();
		reportView.setSelectedTaxGroup(null);
		reportView.updateFilter();
		reportView.selectedLevelOneRows.clear();
		reportView.changeLevelTwoReportPage();
		reportView.reloadLevelTwoAndThreeResults();
		
	}

	// handles changing to request tab
	public void resetRequestPage() {
		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		RequestRunView requestRunView = (RequestRunView) FacesContext.getCurrentInstance().getApplication().getELResolver()
				.getValue(elContext, null, "requestRunView");
		ReportView reportView = (ReportView) FacesContext.getCurrentInstance().getApplication().getELResolver()
				.getValue(elContext, null, "reportView");
		reportView.closeIcn3dDialogs();
		if (requestRunView.isResetPage()) {
			requestRunView.setSearchType("0");
			requestRunView.changeSearchName();
			requestRunView.setResetPage(false);
		}
	}
	
	// handles changing to status tab
	public void resetStatusPage() {
		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		ReportView reportView = (ReportView) FacesContext.getCurrentInstance().getApplication().getELResolver()
				.getValue(elContext, null, "reportView");
		reportView.closeIcn3dDialogs();
		StatusView statusView = (StatusView) FacesContext.getCurrentInstance().getApplication().getELResolver()
				.getValue(elContext, null, "statusView");
		statusView.setStatusLevel("1");
	}
	
	//end session and redirect
		public String endSession(){
			FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
			LoginController loginController = (LoginController) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
					.get("loginController");
			loginController.logout();
			return "/index.html?faces-redirect=true";
		}
		
		public void onIdle(){
			
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage("globalGrowl", new FacesMessage(FacesMessage.SEVERITY_WARN, 
	                "No activity for 10 minutes.", "5 minutes until auto-logout!"));
		}
		
		public void onActive(){
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			long lastAccess = session.getLastAccessedTime();     // in ms
//			int maxInterval = session.getMaxInactiveInterval();  //in seconds
			long currentTime = System.currentTimeMillis();
			
			int diffTime = (int)(currentTime - lastAccess)/1000;
			
			if (diffTime > 30){
				ReportController reportController = (ReportController) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("reportController");
				boolean success = reportController.pingBackend();
			}
		}

}
