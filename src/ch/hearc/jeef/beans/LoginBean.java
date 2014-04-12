/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.jeef.beans;

import ch.hearc.jeef.entities.User;
import ch.hearc.jeef.facade.UserFacade;
import java.io.IOException;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author Alexandre
 */
@Named("loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    private static final String LOGIN_PAGE_URL = "/Login.xhtml";

    private User user;
    private String username;
    private String password;
    private String originalURL;

    @EJB
    private UserFacade userFacade;

    public void login() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        user = userFacade.find(username, password);
        if (user != null) {
            externalContext.redirect(getOriginalURL());
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unknown login", null));
        }
    }

    public void logout() throws IOException {
        user = null;
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.invalidateSession();
        externalContext.redirect(externalContext.getRequestContextPath() + LOGIN_PAGE_URL);
    }

    public User getUser() {
        return this.user;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setOriginalURL(String originalURL) {
        this.originalURL = originalURL;
    }

    public String getOriginalURL() {
        if (originalURL == null || originalURL.equals(LOGIN_PAGE_URL)) {
            return "/";
        } else {
            return originalURL;
        }
    }

}
