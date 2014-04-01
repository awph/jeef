/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.jeef.validator;

import ch.hearc.jeef.controller.UserController;
import ch.hearc.jeef.facade.UserFacade;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@RequestScoped
public class SamePasswordValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        UIInput passwordInput = (UIInput) component.getAttributes().get("password");
        String password = (String) passwordInput.getValue();
        String confirm = (String) value;
        if (password == null || !password.equals(confirm)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "The passwords doesn't match", null));
        }
    }
}
