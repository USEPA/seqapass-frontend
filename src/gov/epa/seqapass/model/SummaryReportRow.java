package gov.epa.seqapass.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class SummaryReportRow {
	
	public SummaryReportRow() {
	}

	public SummaryReportRow(String taxGroup, String filteredTaxGroup, int numSpecies, double meanSim, double medSim, String susceptible) {
		this.taxGroup = taxGroup;
		this.filteredTaxGroup = filteredTaxGroup;
		this.numSpecies = numSpecies;
		this.meanSim = meanSim;
		this.medSim = medSim;
		this.susceptible = susceptible;
		
		this.numY = 0;
		this.numN = 0;
		this.flatResidues = new LinkedHashMap<String,String>();
	}
	
	public SummaryReportRow(String taxGroup, int numSpecies, String susceptible, int numY, int numN, Map<String,String> flatResidues) {
		this.taxGroup = taxGroup;
		this.numSpecies = numSpecies;
		this.susceptible = susceptible;
		this.numY = numY;
		this.numN = numN;
		
		this.filteredTaxGroup = "";
		this.meanSim = 0;
		this.medSim = 0;
		this.flatResidues = flatResidues;
	}

	private String taxGroup;
	private String filteredTaxGroup;
	private int numSpecies;
	private double meanSim;
	private double medSim;
	private String susceptible;
	//for level 3 only
	private int numY;
	private int numN;
	private Map<String, String> flatResidues;
	

	@Override
	public String toString() {
		return "Link [taxGroup=" + taxGroup + ", filteredTaxGroup=" + filteredTaxGroup + ", numSpecies=" + numSpecies + ", meanSim=" + meanSim + ", medSim=" + medSim + ", susceptible=" + susceptible +
				", numY=" + numY +", numN=" + numN + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((taxGroup == null) ? 0 : taxGroup.hashCode());
		result = prime * result + ((filteredTaxGroup == null) ? 0 : filteredTaxGroup.hashCode());
		result = prime * result + numSpecies;
		result = prime * result + (int) meanSim;
		result = prime * result + (int) medSim;
		result = prime * result + ((susceptible == null) ? 0 : susceptible.hashCode());
		result = prime * result + numY;
		result = prime * result + numN;
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
		SummaryReportRow other = (SummaryReportRow) obj;
		if (taxGroup == null) {
			if (other.taxGroup != null)
				return false;
		} else if (!taxGroup.equals(other.taxGroup))
			return false;
		if (filteredTaxGroup == null) {
			if (other.filteredTaxGroup != null)
				return false;
		} else if (!filteredTaxGroup.equals(other.filteredTaxGroup))
			return false;
		if (numSpecies != other.numSpecies)
			return false;
		if (meanSim != other.meanSim)
			return false;
		if (medSim != other.medSim)
			return false;
		if (susceptible == null) {
			if (other.susceptible != null)
				return false;
		} else if (!susceptible.equals(other.susceptible))
			return false;
		if (numY != other.numY)
			return false;
		if (numN != other.numN)
			return false;
		return true;
	}


	public String getTaxGroup() {
		return taxGroup;
	}

	public void setTaxGroup(String taxGroup) {
		this.taxGroup = taxGroup;
	}

	public String getFilteredTaxGroup() {
		return filteredTaxGroup;
	}

	public void setFilteredTaxGroup(String filteredTaxGroup) {
		this.filteredTaxGroup = filteredTaxGroup;
	}

	public int getNumSpecies() {
		return numSpecies;
	}

	public void setNumSpecies(int numSpecies) {
		this.numSpecies = numSpecies;
	}

	public double getMeanSim() {
		return meanSim;
	}

	public void setMeanSim(double meanSim) {
		this.meanSim = meanSim;
	}

	public double getMedSim() {
		return medSim;
	}

	public void setMedSim(double medSim) {
		this.medSim = medSim;
	}

	public String getSusceptible() {
		return susceptible;
	}

	public void setSusceptible(String susceptible) {
		this.susceptible = susceptible;
	}

	public int getNumY() {
		return numY;
	}

	public void setNumY(int numY) {
		this.numY = numY;
	}

	public int getNumN() {
		return numN;
	}

	public void setNumN(int numN) {
		this.numN = numN;
	}

	public Map<String, String> getFlatResidues() {
		return flatResidues;
	}

	public void setFlatResidues(Map<String, String> flatResidues) {
		this.flatResidues = flatResidues;
	}

}
