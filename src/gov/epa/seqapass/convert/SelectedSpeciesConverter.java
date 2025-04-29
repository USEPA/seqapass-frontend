package gov.epa.seqapass.convert;

import java.util.List;

import gov.epa.seqapass.bean.RequestRunView;
import gov.epa.seqapass.common.Species;

//import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("selectedSpeciesConverter")
@SessionScoped
public class SelectedSpeciesConverter implements Converter {

	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		if (value != null && value.trim().length() > 0) {
			try {	
				FacesContext facesContext = FacesContext.getCurrentInstance();
				RequestRunView requestRunView = (RequestRunView) facesContext.getApplication().evaluateExpressionGet(facesContext, "#{requestRunView}", RequestRunView.class);
				List<Species> speciesList = requestRunView.getSelectedSpeciesList();
				
//				int taxId = Integer.parseInt(value);
//				for (Species species : speciesList) {
//					if (species.getTaxId() == taxId) {
//						return species;
//					}
//				}				
				for (Species species : speciesList){
        			if (species.getDisplayName().equals(value.replaceAll("\\n",""))){
        				return species;
        			}
        		}
				return null;
			} catch (NumberFormatException e) {
				System.out.println("Error in selectedSpeciesConverter converter getAsObject (NumberFormatException)");
				throw new ConverterException();
			}
		} else {
			return null;
		}
	}

	public String getAsString(FacesContext fc, UIComponent uic, Object object) {
		if (object != null) {
//			return String.valueOf(((Species) object).getTaxId());
			return String.valueOf(((Species) object).getDisplayName());
		} else {
			return null;
		}
	}
}
