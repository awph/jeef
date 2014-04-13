package ch.hearc.jeef.controller;

import ch.hearc.jeef.beans.LoginBean;
import ch.hearc.jeef.entities.Post;
import ch.hearc.jeef.entities.Topic;
import ch.hearc.jeef.entities.User;
import ch.hearc.jeef.facade.PostFacade;
import ch.hearc.jeef.util.JsfUtil;
import ch.hearc.jeef.util.PaginationHelper;
import java.io.Serializable;
import java.util.Date;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

@Named("postController")
@SessionScoped
public class PostController implements Serializable {

    private Post current;
    private DataModel items = null;
    @EJB
    private ch.hearc.jeef.facade.PostFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    @Inject
    private LoginBean loginBean;

    public PostController() {
    }

    public Post getSelected() {
        if (current == null) {
            current = new Post();
            selectedItemIndex = -1;
        }
        return current;
    }

    private PostFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination(final Topic topic) {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().countForTopic(topic);
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRangeForTopic(new int[]{getPageFirstItem(), getPageLastItem()}, topic));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public void prepareCreate() {
        current = new Post();
        selectedItemIndex = -1;
    }

    public String create(Topic topic) {
        try {
            User user = loginBean.getUser();
            current.setTopic(topic);
            current.setCreator(user);
            current.setLastEditor(user);
            current.setCreatedDate(new Date());
            current.setEditedDate(new Date());
            getFacade().create(current);
            prepareCreate();
            getPagination(topic).lastPage();
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Localization").getString("PostCreated"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Localization").getString("PersistenceErrorOccured"));
        }
        return topicViewFullURL(topic);
    }

    public String prepareEdit(Topic topic) {
        current = (Post) getItems(topic).getRowData();
        selectedItemIndex = getPagination(topic).getPageFirstItem() + getItems(topic).getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Localization").getString("PostUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Localization").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy(Topic topic) {
        current = (Post) getItems(topic).getRowData();
        selectedItemIndex = getPagination(topic).getPageFirstItem() + getItems(topic).getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView(Topic topic) {
        performDestroy();
        recreateModel();
        updateCurrentItem(topic);
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Localization").getString("PostDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Localization").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem(Topic topic) {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (getPagination(topic).getPageFirstItem() >= count) {
                getPagination(topic).previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel<Post> getItems(Topic topic) {
        if (items == null) {
            items = getPagination(topic).createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next(Topic topic) {
        getPagination(topic).nextPage();
        recreateModel();
        return topicViewFullURL(topic);
    }

    public String previous(Topic topic) {
        getPagination(topic).previousPage();
        recreateModel();
        return topicViewFullURL(topic);
    }

    public String setPage(int page, Topic topic) {
        getPagination(topic).setPage(page);
        recreateModel();
        return topicViewFullURL(topic);
    }

    public SelectItem[] getItemsAvailableSelect() {
        return JsfUtil.getSelectItems(ejbFacade.findAll());
    }

    public Post getPost(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    private String topicViewFullURL(Topic topic) {
        return "/topic/View.xhtml?id=" + Integer.toString(topic.getId()) + "&amp;faces-redirect=true&amp;includeViewParams=true";
    }

    @FacesConverter(forClass = Post.class)
    public static class PostControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PostController controller = (PostController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "postController");
            return controller.getPost(Integer.valueOf(value));
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Post) {
                Post o = (Post) object;
                return String.valueOf(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Post.class.getName());
            }
        }

    }

}
