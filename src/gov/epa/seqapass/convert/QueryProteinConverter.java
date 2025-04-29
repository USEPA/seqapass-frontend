package gov.epa.seqapass.convert;

import gov.epa.seqapass.bean.RequestRunView;
import gov.epa.seqapass.common.Protein;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("queryProteinConverter")
public class QueryProteinConverter implements Converter {
	

	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		if (value != null && value.trim().length() > 0) {
			try {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				RequestRunView requestRunView = (RequestRunView) facesContext.getApplication().evaluateExpressionGet(facesContext, "#{requestRunView}", RequestRunView.class);
				List<Protein> proteinList = requestRunView.getProteinList();
//				for (Protein protein : proteinList){
//        			if (protein.getAccession().equals(value.replaceAll("\\n",""))){
//        				return protein;
//        			}
//        		}
				for (Protein protein : proteinList){
        			if (protein.getDisplayName().equals(value.replaceAll("\\n",""))){
        				return protein;
        			}
        		}
                return null;
			} catch (Exception e) {
				e.printStackTrace();
				throw new ConverterException();
			}
		} else {
			return null;
		}
		
	}


	public String getAsString(FacesContext fc, UIComponent uic, Object object) {
		if(object != null) {
            return String.valueOf(((Protein) object).getDisplayName());
//			return String.valueOf(((Protein) object).getAccession());
        }
        else {
            return null;
        }
	}
}
