/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.jeef.facade;

import ch.hearc.jeef.entities.User;
import ch.hearc.jeef.util.HashUtil;
import com.sun.xml.ws.security.impl.policy.Constants;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Alexandre
 */
@Stateless
public class UserFacade extends AbstractFacade<User> {

    @PersistenceContext(unitName = "jeefPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserFacade() {
        super(User.class);
    }

    public Boolean userExistForEmail(String email) {
        return !em.createNamedQuery("User.findByEmail").setParameter("email", email).getResultList().isEmpty();
    }

    public Boolean userExistForUsername(String username) {
        return !em.createNamedQuery("User.findByUsername").setParameter("username", username).getResultList().isEmpty();
    }

    public User find(String username, String password) {
        try {
            User userDB = (User) em.createNamedQuery("User.findByUsername").setParameter("username", username).getResultList().get(0);
            password = HashUtil.hashSHA512(password.concat(userDB.getSalt()));
            
            if (userDB.getPassword().equals(password)) {
                return userDB;
            }
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | ArrayIndexOutOfBoundsException ex) {
            return null;
        }
        return null;
    }
}
