package gov.epa.seqapass.convert;

import gov.epa.seqapass.bean.ReportView;
import gov.epa.seqapass.common.LevelFourRequestableRow;

import java.util.List;

import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("levelFourRequestableConverter")

public class LevelFourRequestableConverter implements Converter {
	
	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		if (value != null && value.trim().length() > 0) {
			try {
				
				FacesContext facesContext = FacesContext.getCurrentInstance();
				ReportView reportView = (ReportView) facesContext.getApplication().evaluateExpressionGet(facesContext, "#{reportView}", ReportView.class);
				List<LevelFourRequestableRow> levelFourList = reportView.getCreatedLevelFourRuns();
				
				for (LevelFourRequestableRow row : levelFourList){
        			if (row.getDisplayName().equals(value.replaceAll("\\n",""))){
        				return row;
        			}
        		}
				
				return null;
			} catch (NumberFormatException e) {
				System.out.println("Error in levelFourRequestableConverter converter getAsObject (NumberFormatException)");
				throw new ConverterException();
			}
		} else {
			return null;
		}
	}

	public String getAsString(FacesContext fc, UIComponent uic, Object object) {
		if (object != null) {
			return String.valueOf(((LevelFourRequestableRow) object).getDisplayName());
		} else {
//			System.out.println("Error in LevelFourRequestableRow converter getAsString");
			return null;
		}
	}

}
