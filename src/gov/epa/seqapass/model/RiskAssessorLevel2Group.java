package gov.epa.seqapass.model;

import java.util.ArrayList;
import java.util.List;

//import org.primefaces.model.StreamedContent;

import gov.epa.seqapass.common.LevelTwoReportRow;
import gov.epa.seqapass.common.LevelTwoRequestableRow;
//import gov.epa.seqapass.common.ReportTypeEnum;

public class RiskAssessorLevel2Group {
	
	private LevelTwoRequestableRow info;
	private List<LevelTwoReportRow> report;
	private boolean downloadTable;
	private boolean downloadInfo;
	private boolean downloadViz;
//	private StreamedContent boxPlot;
	private byte[] boxPlot;
	private BoxPlotSettings boxPlotSettings;
	
//	private ReportTypeEnum level2ReportType;
//	private double levTwoEvalue;
//	private String levTwoTaxGroup;
//	private boolean levTwoSpeciesReadAcross;
//	private int levTwoOrthologCount;
//	private double levTwoCutoff;
//	private boolean levTwoEukaryotesOnly;
	
	private ReportSettings reportSettings;

	public RiskAssessorLevel2Group(){
		this.downloadInfo = false;
		this.downloadViz = false;
		this.boxPlot = null;
		this.report = new ArrayList<LevelTwoReportRow>();
		this.reportSettings = new ReportSettings();
		this.boxPlotSettings = new BoxPlotSettings();
	}
	
	public RiskAssessorLevel2Group(LevelTwoRequestableRow info, List<LevelTwoReportRow> report){
		this.info = info;
		this.report = report;
		this.downloadInfo = false;
		this.downloadViz = false;
		this.boxPlot = null;
		this.reportSettings = new ReportSettings();
		this.boxPlotSettings = new BoxPlotSettings();
	}
	
	@Override
	public String toString() {
		return "RiskAssessorLevel2Group [info=" + info.toString() + "]";
	}
	
	public String converterString(){
		return "RiskAssessorLevel2Group [info=" + info.toString() + ", report=" + report.toString() + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((info == null) ? 0 : info.hashCode());
		result = prime * result + ((report == null) ? 0 : report.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RiskAssessorLevel2Group other = (RiskAssessorLevel2Group) obj;
		if (info == null) {
			if (other.info != null)
				return false;
		} else if (!info.equals(other.info))
			return false;
		if (report == null) {
			if (other.report != null)
				return false;
		} else if (!report.equals(other.report))
			return false;
		if (reportSettings == null) {
			if (other.reportSettings != null)
				return false;
		} else if (!reportSettings.equals(other.reportSettings))
			return false;
		return true;
	}
	
	
	
	//Getters and Setters
	public LevelTwoRequestableRow getInfo() {
		return info;
	}

	public void setInfo(LevelTwoRequestableRow info) {
		this.info = info;
	}

	public List<LevelTwoReportRow> getReport() {
		return report;
	}

	public void setReport(List<LevelTwoReportRow> report) {
		this.report = report;
	}

	public boolean isDownloadInfo() {
		return downloadInfo;
	}

	public void setDownloadInfo(boolean downloadInfo) {
		this.downloadInfo = downloadInfo;
	}

	public boolean isDownloadViz() {
		return downloadViz;
	}

	public void setDownloadViz(boolean downloadViz) {
		this.downloadViz = downloadViz;
	}

	public byte[] getBoxPlot() {
		return boxPlot;
	}

	public void setBoxPlot(byte[] boxPlot) {
		this.boxPlot = boxPlot;
	}

//	public ReportTypeEnum getLevel2ReportType() {
//		return level2ReportType;
//	}
//
//	public void setLevel2ReportType(ReportTypeEnum level2ReportType) {
//		this.level2ReportType = level2ReportType;
//	}
//
//	public double getLevTwoEvalue() {
//		return levTwoEvalue;
//	}
//
//	public void setLevTwoEvalue(double levTwoEvalue) {
//		this.levTwoEvalue = levTwoEvalue;
//	}
//
//	public String getLevTwoTaxGroup() {
//		return levTwoTaxGroup;
//	}
//
//	public void setLevTwoTaxGroup(String levTwoTaxGroup) {
//		this.levTwoTaxGroup = levTwoTaxGroup;
//	}
//
//	public boolean isLevTwoSpeciesReadAcross() {
//		return levTwoSpeciesReadAcross;
//	}
//
//	public void setLevTwoSpeciesReadAcross(boolean levTwoSpeciesReadAcross) {
//		this.levTwoSpeciesReadAcross = levTwoSpeciesReadAcross;
//	}
//
//	public int getLevTwoOrthologCount() {
//		return levTwoOrthologCount;
//	}
//
//	public void setLevTwoOrthologCount(int levTwoOrthologCount) {
//		this.levTwoOrthologCount = levTwoOrthologCount;
//	}
//
//	public double getLevTwoCutoff() {
//		return levTwoCutoff;
//	}
//
//	public void setLevTwoCutoff(double levTwoCutoff) {
//		this.levTwoCutoff = levTwoCutoff;
//	}
//
//	public boolean isLevTwoEukaryotesOnly() {
//		return levTwoEukaryotesOnly;
//	}
//
//	public void setLevTwoEukaryotesOnly(boolean levTwoEukaryotesOnly) {
//		this.levTwoEukaryotesOnly = levTwoEukaryotesOnly;
//	}

	public BoxPlotSettings getBoxPlotSettings() {
		return boxPlotSettings;
	}

	public void setBoxPlotSettings(BoxPlotSettings boxPlotSettings) {
		this.boxPlotSettings = boxPlotSettings;
	}

	public boolean isDownloadTable() {
		return downloadTable;
	}

	public void setDownloadTable(boolean downloadTable) {
		this.downloadTable = downloadTable;
	}

	public ReportSettings getReportSettings() {
		return reportSettings;
	}

	public void setReportSettings(ReportSettings reportSettings) {
		this.reportSettings = reportSettings;
	}
}
