package gov.epa.seqapass.model;

import java.util.List;

import gov.epa.seqapass.bean.VisualizationView.HeatmapReportType;
import gov.epa.seqapass.common.SpeciesNameType;

public class HeatMapSettings {
	
	public HeatMapSettings() {
		
	}
	
	public HeatMapSettings(List<String> selectedTaxGroups, HeatmapReportType reportType, SpeciesNameType speciesOption,
			String optionalSelection, boolean SPHeatmap, boolean SPText, boolean APHeatmap, boolean AA, boolean AAPos,
			List<String> selectedPositions) {
		this.selectedTaxGroups = selectedTaxGroups;
		this.reportType = reportType;
		this.speciesOption = speciesOption;
		this.optionalSelection = optionalSelection;
		this.SPHeatmap = SPHeatmap;
		this.SPText = SPText;
		this.APHeatmap = APHeatmap;
		this.AA = AA;
		this.AAPos = AAPos;
		this.selectedPositions = selectedPositions;
	}
	
	private List<String> selectedTaxGroups;
	private HeatmapReportType reportType;
	private SpeciesNameType speciesOption;
	private String optionalSelection;
	private boolean SPHeatmap;
	private boolean  SPText;
	private boolean  APHeatmap;
	private boolean AA;
	private boolean AAPos;
	private List<String> selectedPositions;
	
	
	
	
	//Getters and Setters
	public List<String> getSelectedTaxGroups() {
		return selectedTaxGroups;
	}

	public void setSelectedTaxGroups(List<String> selectedTaxGroups) {
		this.selectedTaxGroups = selectedTaxGroups;
	}

	public SpeciesNameType getSpeciesOption() {
		return speciesOption;
	}

	public void setSpeciesOption(SpeciesNameType speciesOption) {
		this.speciesOption = speciesOption;
	}

	public boolean isSPHeatmap() {
		return SPHeatmap;
	}

	public void setSPHeatmap(boolean sPHeatmap) {
		SPHeatmap = sPHeatmap;
	}

	public boolean isSPText() {
		return SPText;
	}

	public void setSPText(boolean sPText) {
		SPText = sPText;
	}

	public boolean isAPHeatmap() {
		return APHeatmap;
	}

	public void setAPHeatmap(boolean aPHeatmap) {
		APHeatmap = aPHeatmap;
	}

	public boolean isAA() {
		return AA;
	}

	public void setAA(boolean aA) {
		AA = aA;
	}

	public boolean isAAPos() {
		return AAPos;
	}

	public void setAAPos(boolean aAPos) {
		AAPos = aAPos;
	}

	public HeatmapReportType getReportType() {
		return reportType;
	}

	public void setReportType(HeatmapReportType reportType) {
		this.reportType = reportType;
	}

	public String getOptionalSelection() {
		return optionalSelection;
	}

	public void setOptionalSelection(String optionalSelection) {
		this.optionalSelection = optionalSelection;
	}

	public List<String> getSelectedPositions() {
		return selectedPositions;
	}

	public void setSelectedPositions(List<String> selectedPositions) {
		this.selectedPositions = selectedPositions;
	}

}
