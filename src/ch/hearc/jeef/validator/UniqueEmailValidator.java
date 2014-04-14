/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.jeef.validator;

import ch.hearc.jeef.beans.LoginBean;
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
public class UniqueEmailValidator implements Validator {
    
    @Inject
    private UserFacade userFacade;
    @Inject
    private LoginBean loginBean;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String email = (String) value;
        if (userFacade.userExistForEmail(email) && !(loginBean.getUser()!= null && loginBean.getUser().getEmail().equals(email))) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "This email is already used", null));
        }
    }
}
