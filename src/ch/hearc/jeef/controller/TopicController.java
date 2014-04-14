package ch.hearc.jeef.controller;

import ch.hearc.jeef.beans.LoginBean;
import ch.hearc.jeef.facade.TopicFacade;
import ch.hearc.jeef.entities.Category;
import ch.hearc.jeef.entities.Post;
import ch.hearc.jeef.entities.Topic;
import ch.hearc.jeef.entities.User;
import ch.hearc.jeef.facade.PostFacade;
import ch.hearc.jeef.util.JsfUtil;
import ch.hearc.jeef.util.PaginationHelper;
import java.io.IOException;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

@Named("topicController")
@SessionScoped
public class TopicController implements Serializable {

    private Post firstPost;
    private Topic current;
    private DataModel items = null;
    @EJB
    private ch.hearc.jeef.facade.TopicFacade topicFacade;
    @EJB
    private ch.hearc.jeef.facade.PostFacade postFacade;
    private PaginationHelper pagination;
    private Category category; // Need for Pagination
    private int selectedItemIndex;

    @Inject
    private LoginBean loginBean;

    public TopicController() {
    }

    public Topic getSelected() {
        Map<String, String> parameterMap = (Map<String, String>) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        final String ID_KEY = "id";
        if (parameterMap.containsKey(ID_KEY)) {
            current = getTopic(Integer.valueOf(parameterMap.get(ID_KEY)));
        } else if (current == null) {
            current = new Topic();
            selectedItemIndex = -1;
        }
        return current;
    }

    public Post getFirstPost() {
        if (firstPost == null) {
            firstPost = new Post();
        }
        return firstPost;
    }

    private TopicFacade getTopicFacade() {
        return topicFacade;
    }

    private PostFacade getPostFacade() {
        return postFacade;
    }

    public PaginationHelper getPagination(final Category category) {
        if (this.category == null || !this.category.equals(category)) {
            pagination = null;
            this.category = category;
        }
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getTopicFacade().countForCategory(category);
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getTopicFacade().findRangeForCategory(new int[]{getPageFirstItem(), getPageLastItem()}, category));
                }
            };
        }

        return pagination;
    }

    public void view(Topic topic) {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        try {
            context.redirect(context.getRequestContextPath() + "View.xhtml?id=" + Integer.toString(topic.getId()));
        } catch (IOException ex) {
            Logger.getLogger(TopicController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String prepareCreate(Category category) {
        current = new Topic();
        current.setCategory(category);
        selectedItemIndex = -1;
        return "/topic/Create";
    }

    public String create(Category category) {
        try {
            current.setCategory(category);
            current.setDate(new java.util.Date());
            current.setLocked(false);
            current.setPinned(false);
            getTopicFacade().create(current);
            User user = loginBean.getUser();
            firstPost.setTopic(current);
            firstPost.setCreator(user);
            firstPost.setLastEditor(user);
            firstPost.setCreatedDate(new Date());
            firstPost.setEditedDate(new Date());
            getPostFacade().create(firstPost);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Localization").getString("TopicCreated"));
            view(current);
            return null;
        } catch (Exception e) {
            Logger.getLogger(TopicController.class.getName()).log(Level.SEVERE, null, e);
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Localization").getString("PersistenceErrorOccured"));
            return CategoryController.categoryViewFullURL(current.getCategory());
        }
    }

    public String pin(Topic topic) {
        Category category = topic.getCategory();
        topic.setPinned(!topic.getPinned());
        getTopicFacade().edit(topic);
        JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Localization").getString("TopicUpdated"));
        return CategoryController.categoryViewFullURL(category);
    }

    public String lock(Topic topic) {
        Category category = topic.getCategory();
        topic.setLocked(!topic.getLocked());
        getTopicFacade().edit(topic);
        JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Localization").getString("TopicUpdated"));
        return CategoryController.categoryViewFullURL(category);
    }

    public String destroy(Topic topic) {
        Category category = topic.getCategory();
        selectedItemIndex = getPagination(category).getPageFirstItem() + getItems(category).getRowIndex();
        getTopicFacade().remove(topic);
        JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Localization").getString("TopicDeleted"));
        return CategoryController.categoryViewFullURL(category);
    }

    private void updateCurrentItem() {
        int count = getTopicFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getTopicFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel<Topic> getItems(Category category) {
        items = getPagination(category).createPageDataModel();
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next(Category category) {
        getPagination(category).nextPage();
        recreateModel();
        return CategoryController.categoryViewFullURL(category);
    }

    public String previous(Category category) {
        getPagination(category).previousPage();
        recreateModel();
        return CategoryController.categoryViewFullURL(category);
    }

    public String setPage(int page, Category category) {
        getPagination(category).setPage(page);
        recreateModel();
        return CategoryController.categoryViewFullURL(category);
    }

    public SelectItem[] getItemsAvailableSelect() {
        return JsfUtil.getSelectItems(getTopicFacade().findAll());
    }

    public Topic getTopic(java.lang.Integer id) {
        return getTopicFacade().find(id);
    }

    public static String topicViewFullURL(Topic topic) {
        return "/topic/View.xhtml?id=" + Integer.toString(topic.getId()) + "&amp;faces-redirect=true&amp;includeViewParams=true";
    }

    @FacesConverter(forClass = Topic.class)
    public static class TopicControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TopicController controller = (TopicController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "topicController");
            return controller.getTopic(Integer.valueOf(value));
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Topic) {
                Topic o = (Topic) object;
                return String.valueOf(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Topic.class.getName());
            }
        }

    }

}
