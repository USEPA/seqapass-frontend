package gov.epa.seqapass.convert;

import gov.epa.seqapass.bean.ReportView;
import gov.epa.seqapass.common.LevelThreeRequestableRow;

import java.util.List;

import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("levelThreeRequestableConverter")
@SessionScoped
public class LevelThreeRequestableConverter implements Converter {
	
	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		if (value != null && value.trim().length() > 0) {
			try {
				
				FacesContext facesContext = FacesContext.getCurrentInstance();
				ReportView reportView = (ReportView) facesContext.getApplication().evaluateExpressionGet(facesContext, "#{reportView}", ReportView.class);
				List<LevelThreeRequestableRow> levelThreeList = reportView.getCompletedLevelThreeRuns();
				
				for (LevelThreeRequestableRow row : levelThreeList){
        			if (row.getJobName().equals(value.replaceAll("\\n",""))){
        				return row;
        			}
        		}
				
				return null;
			} catch (NumberFormatException e) {
				System.out.println("Error in levelThreeRequestableConverter converter getAsObject (NumberFormatException)");
				throw new ConverterException();
			}
		} else {
			return null;
		}
	}

	public String getAsString(FacesContext fc, UIComponent uic, Object object) {
		if (object != null) {
			return String.valueOf(((LevelThreeRequestableRow) object).getJobName());
		} else {
//			System.out.println("Error in LevelThreeRequestableRow converter getAsString");
			return null;
		}
	}

}
