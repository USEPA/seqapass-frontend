package gov.epa.seqapass.util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class FacesUtils {

	/**
	 * Utility method to lookup a managed bean.
	 * 
	 * @param name
	 *            The managed bean name.
	 * @return A managed bean object.
	 */
	public static Object getManagedBean(String name) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		return ctx.getApplication().evaluateExpressionGet(ctx, "#{" + name + "}", Object.class);
	}

	/**
	 * Adds a {@link FacesMessage} to the {@link FacesContext}.
	 * 
	 * @param message
	 *            The FacesMessage
	 */
	public static void addMessage(FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T findBean(String beanName) {
	    FacesContext context = FacesContext.getCurrentInstance();
	    return (T) context.getApplication().evaluateExpressionGet(context, "#{" + beanName + "}", Object.class);
	}
}
