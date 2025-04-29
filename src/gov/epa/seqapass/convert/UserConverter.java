package gov.epa.seqapass.convert;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import gov.epa.seqapass.model.User;
import gov.epa.seqapass.service.UserService;
import gov.epa.seqapass.util.FacesUtils;

@FacesConverter(forClass = User.class)
public class UserConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null) {
			return null;
		}
		UserService service = (UserService) FacesUtils.getManagedBean("userService");
		return service.findUserById(Integer.valueOf(value));
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value instanceof User) {
			return String.valueOf(((User) value).getId());
		} else if (value instanceof String) {
			return ((String) value);
		}
		return null;
	}

}
