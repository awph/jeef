package ch.hearc.jeef.controller;

import ch.hearc.jeef.beans.LoginBean;
import ch.hearc.jeef.facade.UserFacade;
import ch.hearc.jeef.entities.Role;
import ch.hearc.jeef.entities.User;
import ch.hearc.jeef.facade.RoleFacade;
import ch.hearc.jeef.util.HashUtil;
import ch.hearc.jeef.util.JsfUtil;
import ch.hearc.jeef.util.PaginationHelper;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

@Named("userController")
@SessionScoped
public class UserController implements Serializable {

    private static final String ID_KEY = "userid";

    @EJB
    private RoleFacade roleFacade;

    private User current;
    // Stuff for update user ------
    private String checkPassword;
    private String newPassword;
    // ----------------------------
    private DataModel items = null;
    @EJB
    private ch.hearc.jeef.facade.UserFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    @Inject
    LoginBean loginBean;

    public UserController() {
    }

    public User getSelected() {
        Map<String, String> parameterMap = (Map<String, String>) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        if (parameterMap.containsKey(ID_KEY)) {
            current = getUser(Integer.valueOf(parameterMap.get(ID_KEY)));
        } else if (current == null) {
            current = new User();
            selectedItemIndex = -1;
        }
        return current;
    }

    private UserFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageLastItem()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public void prepareView() {
        clean();
    }

    public String create() {
        try {
            Role role = roleFacade.find(3);
            current.setRole(role);
            current.setSalt(HashUtil.generateSalt(128));
            current.setPassword(HashUtil.hashSHA512(current.getPassword().concat(current.getSalt())));
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Localization").getString("UserCreated"));
            return "Login";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Localization").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public void prepareEdit() {
        clean();
    }

    public String manage(User user) {
        getFacade().edit(user);
        return userViewFullURL(user);
    }

    public String update(User user) {
        if (getFacade().find(loginBean.getUser().getUsername(), checkPassword) != null) {
            if (newPassword != null && newPassword.length() > 0) {
                user.setSalt(HashUtil.generateSalt(128));
                try {
                    user.setPassword(HashUtil.hashSHA512(newPassword.concat(user.getSalt())));
                } catch (Exception ex) {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Localization").getString("PersistenceErrorOccured"));
                }
            }
            getFacade().edit(user);
        } else {
            JsfUtil.addErrorMessage(null, ResourceBundle.getBundle("/Localization").getString("PersistenceErrorOccured"));
        }
        return userViewFullURL(user);
    }

    public String delete(User user) {
        getFacade().remove(user);
        recreatePagination();
        recreateModel();
        return userListFullURL();
    }

    public String block(User user) {
        user.setBanned(!user.getBanned());
        getFacade().edit(user);
        JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Localization").getString("UserUpdated"));
        return userViewFullURL(user);
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    private void clean() {
        current = new User();
        selectedItemIndex = -1;
        checkPassword = null;
        newPassword = null;
    }
    
    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelect() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public User getUser(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    public String getCheckPassword() {
        return checkPassword;
    }

    public void setCheckPassword(String checkPassword) {
        this.checkPassword = checkPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String userViewFullURLJSF(User user) {
        return userViewFullURL(user);
    }

    public static String userViewFullURL(User user) {
        return "/user/View.xhtml?" + ID_KEY + "=" + Integer.toString(user.getId()) + "&amp;faces-redirect=true&amp;includeViewParams=true";
    }

    public String userEditFullURLJSF(User user) {
        return userEditFullURL(user);
    }

    public static String userEditFullURL(User user) {
        return "/user/Edit.xhtml?" + ID_KEY + "=" + Integer.toString(user.getId()) + "&amp;faces-redirect=true&amp;includeViewParams=true";
    }

    public static String userListFullURL() {
        return "/user/List.xhtml";
    }

    @FacesConverter(forClass = User.class)
    public static class UserControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UserController controller = (UserController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "userController");
            return controller.getUser(Integer.valueOf(value));
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof User) {
                User o = (User) object;
                return String.valueOf(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + User.class.getName());
            }
        }

    }

}
