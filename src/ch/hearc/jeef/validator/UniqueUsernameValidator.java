/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.jeef.validator;

import ch.hearc.jeef.facade.UserFacade;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@RequestScoped
public class UniqueUsernameValidator implements Validator {
    
    @Inject
    private UserFacade userFacade;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String username = (String) value;
        if (userFacade.userExistForUsername(username)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "This username is already used", null));
        }
    }
}
