/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.jeef.validator;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;

@Named
@RequestScoped
public class SamePasswordValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        UIInput passwordInput = (UIInput) component.getAttributes().get("password");
        UIInput newPasswordInput = (UIInput) component.getAttributes().get("newpassword");

        String confirm = (String) value;
        if (!(newPasswordInput != null && ((String) newPasswordInput.getValue()).length() == 0 && confirm.length() == 0)) {
            String password = null;
            if (passwordInput != null) {
                password = (String) passwordInput.getValue();
            } else if (newPasswordInput != null) {
                password = (String) newPasswordInput.getValue();
            }
            if (password == null || !password.equals(confirm)) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "The passwords doesn't match", null));
            }
        }
    }
}
