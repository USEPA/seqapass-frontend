package gov.epa.seqapass.convert;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.faces.convert.Converter;
import gov.epa.seqapass.bean.ReportView;
import gov.epa.seqapass.bean.RequestRunView;
import gov.epa.seqapass.common.Chemical;
import gov.epa.seqapass.common.Species;

@FacesConverter("searchChemicalConverter")
public class SearchChemicalConverter implements Converter{

	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		if (value != null && value.trim().length() > 0) {
			try {
				
				FacesContext facesContext = FacesContext.getCurrentInstance();
				ReportView reportView = (ReportView) facesContext.getApplication().evaluateExpressionGet(facesContext, "#{reportView}", ReportView.class);
				List<Chemical> chemicalList = reportView.getEcotoxPossibleChemicalList();
				
				for (Chemical chem : chemicalList){
        			if (chem.getDisplayName().equals(value.replaceAll("\\n",""))){
        				return chem;
        			}
        		}
				
				return null;
			} catch (NumberFormatException e) {
				System.out.println("Error in searchSpeciesConverter converter getAsObject (NumberFormatException)");
				throw new ConverterException();
			}
		} else {
			return null;
		}
	}

	public String getAsString(FacesContext fc, UIComponent uic, Object object) {
		if (object != null) {
			return String.valueOf(((Chemical) object).getDisplayName());
		} else {
			return null;
		}
	}
	
	
}
