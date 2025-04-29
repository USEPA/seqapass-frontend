package gov.epa.seqapass.convert;

import java.util.List;

//import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import gov.epa.seqapass.bean.RiskAssessorView;
import gov.epa.seqapass.model.RiskAssessorTaxGroup;

@FacesConverter("riskAssessorTaskGroupConverter")
public class RiskAssessorTaxGroupConverter implements Converter {
	
	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		if (value != null && value.trim().length() > 0) {
			try {	
				FacesContext facesContext = FacesContext.getCurrentInstance();
				RiskAssessorView riskAssessorView = (RiskAssessorView) facesContext.getApplication().evaluateExpressionGet(facesContext, "#{riskAssessorView}", RiskAssessorView.class);
//				List<RiskAssessorTaxGroup> taxGrps = riskAssessorView.getSelectedSpeciesColl();
				List<RiskAssessorTaxGroup> taxGrps = riskAssessorView.getSpeciesColl();
				for(RiskAssessorTaxGroup grp : taxGrps){
					if (grp.converterString().equals(value.replaceAll("\\n",""))){
						return grp;
					}
				}
				return null;
			} catch (Exception e) {
				System.out.println("Error in riskAssessorTaskGroupConverter converter getAsObject");
				throw new ConverterException();
			}
		} else {
			return null;
		}
	}
	
	public String getAsString(FacesContext fc, UIComponent uic, Object object){
		if (object != null){
			return String.valueOf(((RiskAssessorTaxGroup) object).converterString());
		} else {
			return null;
		}
	}


}
