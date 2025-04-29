package gov.epa.seqapass.convert;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import gov.epa.seqapass.bean.RiskAssessorView;
import gov.epa.seqapass.model.RiskAssessorLevel2Group;
//import gov.epa.seqapass.model.RiskAssessorTaxGroup;

@FacesConverter("riskAssessorLevel2GroupConverter")
public class RiskAssessorLevel2GroupConverter implements Converter {
	
	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		if (value != null && value.trim().length() > 0) {
			try {	
				FacesContext facesContext = FacesContext.getCurrentInstance();
				RiskAssessorView riskAssessorView = (RiskAssessorView) facesContext.getApplication().evaluateExpressionGet(facesContext, "#{riskAssessorView}", RiskAssessorView.class);
				List<RiskAssessorLevel2Group> lev2Grps = riskAssessorView.getLevel2Groups();
				for(RiskAssessorLevel2Group grp : lev2Grps){
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
			return String.valueOf(((RiskAssessorLevel2Group) object).converterString());
		} else {
			return null;
		}
	}

}
