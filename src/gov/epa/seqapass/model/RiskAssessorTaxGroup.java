package gov.epa.seqapass.model;

public class RiskAssessorTaxGroup {
	
	public RiskAssessorTaxGroup(){
		
	}
	
	public RiskAssessorTaxGroup(String taxGroup, String commonName, String scientificName, int speciesTaxId){
		this.taxGroup = taxGroup;
		this.commonName = commonName;
		this.scientificName = scientificName;
		this.speciesTaxId = speciesTaxId;
		this.disabled = true;
		this.ecotox = false;
	}
	
	public RiskAssessorTaxGroup(String taxGroup, String commonName, String scientificName, int speciesTaxId, boolean ecotox){
		this.taxGroup = taxGroup;
		this.commonName = commonName;
		this.scientificName = scientificName;
		this.speciesTaxId = speciesTaxId;
		this.ecotox = ecotox;
		this.disabled = !ecotox;
	}
	
	private String taxGroup;
	private String commonName;
	private String scientificName;
	private int speciesTaxId;
	private boolean ecotox;
	private boolean disabled;
	
	@Override
	public String toString() {
		
		return "RiskAssessorTaxGroup [taxGroup=" + taxGroup + ", commonName=" + commonName + 
				", scientificName= " + scientificName + ", disabled=" + disabled + "]";
		
	}
	
	public String converterString(){
		return "RiskAssessorTaxGroup [taxGroup=" + taxGroup + ", commonName=" + commonName + 
				", scientificName= " + scientificName + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((taxGroup == null) ? 0 : taxGroup.hashCode());
		result = prime * result + ((commonName == null) ? 0 : commonName.hashCode());
		result = prime * result + ((scientificName == null) ? 0 : scientificName.hashCode());
		return result;

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
//		System.out.println(" getClass()=" + getClass());
//		System.out.println(" obj.getClass()=" + obj.getClass());
		if (getClass() != obj.getClass())
			return false;
		RiskAssessorTaxGroup other = (RiskAssessorTaxGroup) obj;
		if (taxGroup == null) {
			if (other.taxGroup != null)
				return false;
		} else if (!taxGroup.equals(other.taxGroup))
			return false;
		
		if (commonName == null) {
			if (other.commonName != null)
				return false;
		} else if (!commonName.equals(other.commonName))
			return false;
		
		if (scientificName == null) {
			if (other.scientificName != null)
				return false;
		} else if (!scientificName.equals(other.scientificName))
			return false;
		
		return true;
	
	}
	
	//Getters and Setters

	public String getTaxGroup() {
		return taxGroup;
	}

	public void setTaxGroup(String taxGroup) {
		this.taxGroup = taxGroup;
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

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public int getSpeciesTaxId() {
		return speciesTaxId;
	}

	public void setSpeciesTaxId(int speciesTaxId) {
		this.speciesTaxId = speciesTaxId;
	}

	public boolean isEcotox() {
		return ecotox;
	}

	public void setEcotox(boolean ecotox) {
		this.ecotox = ecotox;
	}


}
