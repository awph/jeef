/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.hearc.jeef.controller;

import ch.hearc.jeef.entities.User;
import ch.hearc.jeef.facades.UserFacade;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Alexandre
 */
@ManagedBean
@SessionScoped
public class UserController {
    
    @EJB
    private UserFacade userFacade;

    /**
     * Creates a new instance of UserController
     */
    public UserController() {
    }
    
    public List<User> getAllUsers() {
        return userFacade.findAll();
    }
    
}
