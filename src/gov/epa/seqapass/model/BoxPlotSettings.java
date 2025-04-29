package gov.epa.seqapass.model;

import java.util.List;

import gov.epa.seqapass.bean.VisualizationView.boxPlotSpeciesNameType;
//import gov.epa.seqapass.common.ReportTypeEnum;

public class BoxPlotSettings {
	
	public BoxPlotSettings() {
		this.reportSettings = new ReportSettings();
	}
	
	public BoxPlotSettings(List<String> selectedTaxGroups, List<String> selectedSpecies, boxPlotSpeciesNameType speciesOption,
			boolean groupByCommonName, boolean ortholog, boolean threatened, boolean endangered, boolean modelOrganisms,
			ReportSettings reportSettings) {
		this.selectedTaxGroups = selectedTaxGroups;
		this.selectedSpecies = selectedSpecies;
		this.speciesOption = speciesOption;
		this.groupByCommonName = groupByCommonName;
		this.ortholog = ortholog;
		this.threatened = threatened;
		this.endangered = endangered;
		this.modelOrganisms = modelOrganisms;
//		this.boxPlotReportType = boxPlotReportType;
		this.reportSettings = reportSettings;
	}
	
	private List<String> selectedTaxGroups;
	private List<String> selectedSpecies;
	private boxPlotSpeciesNameType speciesOption;
	private boolean groupByCommonName;
	private boolean ortholog;
	private boolean threatened;
	private boolean endangered;
	private boolean modelOrganisms;
//	private ReportTypeEnum boxPlotReportType;
	private ReportSettings reportSettings;
	
	
	
	
	//Getters and Setters
	public List<String> getSelectedTaxGroups() {
		return selectedTaxGroups;
	}

	public void setSelectedTaxGroups(List<String> selectedTaxGroups) {
		this.selectedTaxGroups = selectedTaxGroups;
	}

	public List<String> getSelectedSpecies() {
		return selectedSpecies;
	}

	public void setSelectedSpecies(List<String> selectedSpecies) {
		this.selectedSpecies = selectedSpecies;
	}

	public boxPlotSpeciesNameType getSpeciesOption() {
		return speciesOption;
	}

	public void setSpeciesOption(boxPlotSpeciesNameType speciesOption) {
		this.speciesOption = speciesOption;
	}

	public boolean isOrtholog() {
		return ortholog;
	}

	public void setOrtholog(boolean ortholog) {
		this.ortholog = ortholog;
	}

	public boolean isThreatened() {
		return threatened;
	}

	public void setThreatened(boolean threatened) {
		this.threatened = threatened;
	}

	public boolean isEndangered() {
		return endangered;
	}

	public void setEndangered(boolean endangered) {
		this.endangered = endangered;
	}

	public boolean isModelOrganisms() {
		return modelOrganisms;
	}

	public void setModelOrganisms(boolean modelOrganisms) {
		this.modelOrganisms = modelOrganisms;
	}

	public boolean isGroupByCommonName() {
		return groupByCommonName;
	}

	public void setGroupByCommonName(boolean groupByCommonName) {
		this.groupByCommonName = groupByCommonName;
	}

//	public ReportTypeEnum getBoxPlotReportType() {
//		return boxPlotReportType;
//	}
//
//	public void setBoxPlotReportType(ReportTypeEnum boxPlotReportType) {
//		this.boxPlotReportType = boxPlotReportType;
//	}

	public ReportSettings getReportSettings() {
		return reportSettings;
	}

	public void setReportSettings(ReportSettings reportSettings) {
		this.reportSettings = reportSettings;
	}

}
