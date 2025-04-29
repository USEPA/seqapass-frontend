package gov.epa.seqapass.model;

import java.util.ArrayList;
import java.util.List;

//import org.primefaces.model.StreamedContent;

//import gov.epa.seqapass.bean.VisualizationView.boxPlotSpeciesNameType;
import gov.epa.seqapass.common.LevelOneReportRow;
import gov.epa.seqapass.common.LevelThreeReportRow;
import gov.epa.seqapass.common.LevelTwoReportRow;
import gov.epa.seqapass.common.ReportTypeEnum;

public class RiskAssessorReport {
	
	private List<RiskAssessorReportRow> report;
	
	//Level 1
	private List<LevelOneReportRow> levelOneReport;
	private byte[] lev1Boxplot;
	private BoxPlotSettings lev1BoxPlotSettings;
	
	private ReportTypeEnum levelOneReportType;

	//Level 2
	private List<List<LevelTwoReportRow>> levelTwoReportColl;
	
	//Level 3
	private List<LevelThreeReportRow> levelThreeReport;
	private String levelThreeTemplateProtein;
	private String levelThreeTemplateSpecies;
	private String levelThreeRunName;
	private byte[] lev3Heatmap;
	private HeatMapSettings lev3HeatMapSettings;
	
	
	
	
	public RiskAssessorReport(){
		levelOneReport = new ArrayList<LevelOneReportRow>();
		levelTwoReportColl = new ArrayList<List<LevelTwoReportRow>>();
		levelThreeReport = new ArrayList<LevelThreeReportRow>();
		report = new ArrayList<RiskAssessorReportRow>();
		lev1Boxplot = null;
		lev1BoxPlotSettings = new BoxPlotSettings();
		lev3HeatMapSettings = new HeatMapSettings();
	}
	
	
	
	//getters and setters

	public List<LevelOneReportRow> getLevelOneReport() {
		return levelOneReport;
	}

	public void setLevelOneReport(List<LevelOneReportRow> levelOneReport) {
		this.levelOneReport = levelOneReport;
	}



	public List<List<LevelTwoReportRow>> getLevelTwoReportColl() {
		return levelTwoReportColl;
	}



	public void setLevelTwoReportColl(List<List<LevelTwoReportRow>> levelTwoReportColl) {
		this.levelTwoReportColl = levelTwoReportColl;
	}



	public List<RiskAssessorReportRow> getReport() {
		return report;
	}



	public void setReport(List<RiskAssessorReportRow> report) {
		this.report = report;
	}



	public List<LevelThreeReportRow> getLevelThreeReport() {
		return levelThreeReport;
	}



	public void setLevelThreeReport(List<LevelThreeReportRow> levelThreeReport) {
		this.levelThreeReport = levelThreeReport;
	}



	public ReportTypeEnum getLevelOneReportType() {
		return levelOneReportType;
	}



	public void setLevelOneReportType(ReportTypeEnum levelOneReportType) {
		this.levelOneReportType = levelOneReportType;
	}


	public String getLevelThreeTemplateProtein() {
		return levelThreeTemplateProtein;
	}



	public void setLevelThreeTemplateProtein(String levelThreeTemplateProtein) {
		this.levelThreeTemplateProtein = levelThreeTemplateProtein;
	}



	public String getLevelThreeTemplateSpecies() {
		return levelThreeTemplateSpecies;
	}



	public void setLevelThreeTemplateSpecies(String levelThreeTemplateSpecies) {
		this.levelThreeTemplateSpecies = levelThreeTemplateSpecies;
	}



	public byte[] getLev1Boxplot() {
		return lev1Boxplot;
	}



	public void setLev1Boxplot(byte[] lev1Boxplot) {
		this.lev1Boxplot = lev1Boxplot;
	}



	public String getLevelThreeRunName() {
		return levelThreeRunName;
	}



	public void setLevelThreeRunName(String levelThreeRunName) {
		this.levelThreeRunName = levelThreeRunName;
	}



	public BoxPlotSettings getLev1BoxPlotSettings() {
		return lev1BoxPlotSettings;
	}



	public void setLev1BoxPlotSettings(BoxPlotSettings lev1BoxPlotSettings) {
		this.lev1BoxPlotSettings = lev1BoxPlotSettings;
	}



	public byte[] getLev3Heatmap() {
		return lev3Heatmap;
	}



	public void setLev3Heatmap(byte[] lev3Heatmap) {
		this.lev3Heatmap = lev3Heatmap;
	}



	public HeatMapSettings getLev3HeatMapSettings() {
		return lev3HeatMapSettings;
	}



	public void setLev3HeatMapSettings(HeatMapSettings lev3HeatMapSettings) {
		this.lev3HeatMapSettings = lev3HeatMapSettings;
	}

}
