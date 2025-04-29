package gov.epa.seqapass.convert;

import gov.epa.seqapass.bean.ReportView;
//import gov.epa.seqapass.bean.RequestRunView;


import gov.epa.seqapass.common.LevelTwoRequestableRow;

import java.util.List;

import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("levelTwoRequestableConverter")
@SessionScoped
public class LevelTwoRequestableConverter implements Converter {
	
	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		if (value != null && value.trim().length() > 0) {
			try {
				
				FacesContext facesContext = FacesContext.getCurrentInstance();
				ReportView reportView = (ReportView) facesContext.getApplication().evaluateExpressionGet(facesContext, "#{reportView}", ReportView.class);
				List<LevelTwoRequestableRow> domainList = reportView.getLevelTwoDomains();
				
				for (LevelTwoRequestableRow row : domainList){
        			if (row.getDisplayText().equals(value.replaceAll("\\n",""))){
        				return row;
        			}
        		}
//				int keyVal = Integer.parseInt(value);
//				for (LevelTwoRequestableRow row : domainList){
//					if (row.getKey() == keyVal){
//						return row;
//					}
//				}
				
				return null;
			} catch (NumberFormatException e) {
				System.out.println("Error in levelTwoRequestableConverter converter getAsObject (NumberFormatException)");
				throw new ConverterException();
			}
		} else {
			return null;
		}
	}

	public String getAsString(FacesContext fc, UIComponent uic, Object object) {
		if (object != null) {
			return String.valueOf(((LevelTwoRequestableRow) object).getDisplayText());
		} else {
			return null;
		}
	}

}
