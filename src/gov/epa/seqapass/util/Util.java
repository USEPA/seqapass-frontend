package gov.epa.seqapass.util;

import gov.epa.seqapass.bean.UserLoginView;

import java.util.Date;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

public class Util {
	public static final FacesContext facesContext = FacesContext.getCurrentInstance();
	public static final ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msg");
	
	public static String getResource(String str){
		return resourceBundle.getString(str);		
	}
	public static boolean getResourceBoolean(String str){
		return Boolean.valueOf(getResource(str));
	}

	public static void println(boolean enabled, Object o) {
		if( !enabled ){
			return;
		}
		Exception e = new Exception();
		StackTraceElement element = e.getStackTrace()[1];
		String mName = element.getMethodName();
		String cName = element.getClassName();
		String fName = element.getFileName();
		int line = element.getLineNumber();
		String s = cName + " " + mName + "() " + fName + " " + line + ": " + new Date(System.currentTimeMillis());
		System.out.println("<---------\n" + s + "\n" + o + "\n--------->");
	}

	public static void println(boolean enabled) {
		if( !enabled ){
			return;
		}
		Exception e = new Exception();
		StackTraceElement element = e.getStackTrace()[1];
		String mName = element.getMethodName();
		String cName = element.getClassName();
		String fName = element.getFileName();
		int line = element.getLineNumber();
		String s = cName + " " + mName + "() " + fName + " " + line + ": " + new Date(System.currentTimeMillis());
		System.out.println("<---------\n" + s + "\n--------->");
	}

	public static UserLoginView getUserLoginView(){
		UserLoginView userLoginView = (UserLoginView) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userLoginView");
		return userLoginView;
	}
	
}
