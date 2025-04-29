package gov.epa.seqapass.model;

//import gov.epa.seqapass.common.LevelThreeReportRow;
import gov.epa.seqapass.common.ReportTypeEnum;

public class ReportSettings {
	
	public ReportSettings(){
		
	}
	
	public ReportSettings(double evalueLimit, String taxGroup, int commonDomainLimit, boolean speciesReadAcross,
			double cutValue, ReportTypeEnum reportType, int orthologCount, boolean eukaryotesOnly) {
		this.evalueLimit = evalueLimit;
		this.taxGroup = taxGroup;
		this.commonDomainLimit = commonDomainLimit;
		this.speciesReadAcross = speciesReadAcross;
		this.cutValue = cutValue;
		this.reportType = reportType;
		this.orthologCount = orthologCount;
		this.eukaryotesOnly = eukaryotesOnly;
	}


	private double evalueLimit;
	private String taxGroup;
	private int commonDomainLimit;
	private boolean speciesReadAcross;
	private double cutValue;
	private ReportTypeEnum reportType;
	private int orthologCount;
	private boolean eukaryotesOnly;
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReportSettings other = (ReportSettings) obj;
		if (evalueLimit != other.evalueLimit)
			return false;
		if (taxGroup == null) {
			if (other.taxGroup != null)
				return false;
		} else if (!taxGroup.equals(other.taxGroup))
			return false;
		if (speciesReadAcross != other.speciesReadAcross)
			return false;
		if (cutValue != other.cutValue)
			return false;
		if (reportType != other.reportType)
			return false;
		if (orthologCount != other.orthologCount)
			return false;
		if (eukaryotesOnly != other.eukaryotesOnly)
			return false;
		
		return true;
	}
	
	
	//Getters and Setters
	public double getEvalueLimit() {
		return evalueLimit;
	}

	public void setEvalueLimit(double evalueLimit) {
		this.evalueLimit = evalueLimit;
	}

	public String getTaxGroup() {
		return taxGroup;
	}

	public void setTaxGroup(String taxGroup) {
		this.taxGroup = taxGroup;
	}

	public int getCommonDomainLimit() {
		return commonDomainLimit;
	}

	public void setCommonDomainLimit(int commonDomainLimit) {
		this.commonDomainLimit = commonDomainLimit;
	}

	public boolean isSpeciesReadAcross() {
		return speciesReadAcross;
	}

	public void setSpeciesReadAcross(boolean speciesReadAcross) {
		this.speciesReadAcross = speciesReadAcross;
	}

	public double getCutValue() {
		return cutValue;
	}

	public void setCutValue(double cutValue) {
		this.cutValue = cutValue;
	}

	public ReportTypeEnum getReportType() {
		return reportType;
	}

	public void setReportType(ReportTypeEnum reportType) {
		this.reportType = reportType;
	}

	public int getOrthologCount() {
		return orthologCount;
	}

	public void setOrthologCount(int orthologCount) {
		this.orthologCount = orthologCount;
	}

	public boolean isEukaryotesOnly() {
		return eukaryotesOnly;
	}

	public void setEukaryotesOnly(boolean eukaryotesOnly) {
		this.eukaryotesOnly = eukaryotesOnly;
	}
}

