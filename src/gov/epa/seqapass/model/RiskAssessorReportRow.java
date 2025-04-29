package gov.epa.seqapass.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class RiskAssessorReportRow {
	
	
	private int dataVersion;  //called updateVersion in reportInfo class
	private String accession;
	private String filteredTaxGroup;
	private String commonName;
	private String scientificName;
	private Integer speciesTaxId;
	private String protein;
	private String susceptible;
	private Map<String, String> level2Run;
	private String level3Susceptible;
	
	
	public RiskAssessorReportRow(int dataVersion, String accession, String filteredTaxGroup, String commonName,
			String scientificName, Integer speciesTaxId, String protein, String susceptible){
		this.dataVersion = dataVersion;
		this.accession = accession;
		this.filteredTaxGroup = filteredTaxGroup;
		this.commonName = commonName;
		this.scientificName = scientificName;
		this.speciesTaxId = speciesTaxId;
		this.protein = protein;
		this.susceptible = susceptible;
		this.level2Run = new LinkedHashMap<String,String>();
		this.level3Susceptible = "NA";
	}
	
	
	//Getters and Setters
	public int getDataVersion() {
		return dataVersion;
	}

	public void setDataVersion(int dataVersion) {
		this.dataVersion = dataVersion;
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public String getFilteredTaxGroup() {
		return filteredTaxGroup;
	}

	public void setFilteredTaxGroup(String filteredTaxGroup) {
		this.filteredTaxGroup = filteredTaxGroup;
	}

	public String getProtein() {
		return protein;
	}

	public void setProtein(String protein) {
		this.protein = protein;
	}

	public String getSusceptible() {
		return susceptible;
	}

	public void setSusceptible(String susceptible) {
		this.susceptible = susceptible;
	}


	public Map<String, String> getLevel2Run() {
		return level2Run;
	}


	public void setLevel2Run(Map<String, String> level2Run) {
		this.level2Run = level2Run;
	}


	public Integer getSpeciesTaxId() {
		return speciesTaxId;
	}


	public void setSpeciesTaxId(Integer speciesTaxId) {
		this.speciesTaxId = speciesTaxId;
	}


	public String getCommonName() {
		return commonName;
	}


	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}


	public String getScientificName() {
		return scientificName;
	}


	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
	}


	public String getLevel3Susceptible() {
		return level3Susceptible;
	}


	public void setLevel3Susceptible(String level3Susceptible) {
		this.level3Susceptible = level3Susceptible;
	}
	
}
