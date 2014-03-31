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
import javax.faces.bean.ManagedProperty;
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
        }
        else if (current == null) {
            current = new Topic();
            selectedItemIndex = -1;
        }
        return current;
    }
    
    public Post getFirstPost() {
        if(firstPost == null){
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

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getTopicFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getTopicFacade().findRange(new int[]{getPageFirstItem(), getPageLastItem()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
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

    public String create() {
        try {
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
            new CategoryController().recreateModel();
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Localization").getString("TopicCreated"));
            view(current);
            return null;
        } catch (Exception e) {
            //Logger.getLogger(TopicController.class.getName()).log(Level.SEVERE, ee.getCause().getMessage(), ee.getCause().getCause());
            Logger.getLogger(TopicController.class.getName()).log(Level.SEVERE, firstPost.toString(), e);
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Localization").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String pin() {
        current = (Topic) getItems().getRowData();
        current.setPinned(!current.getPinned());
        getTopicFacade().edit(current);
        JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Localization").getString("TopicUpdated"));
        return null;
    }

    public String lock() {
        current = (Topic) getItems().getRowData();
        current.setLocked(!current.getLocked());
        getTopicFacade().edit(current);
        JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Localization").getString("TopicUpdated"));
        return null;
    }

    public String destroy() {
        current = (Topic) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
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
            getTopicFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Localization").getString("TopicDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Localization").getString("PersistenceErrorOccured"));
        }
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

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    public DataModel getItems(Category category) {
        PaginationHelper pagination = new PaginationHelper(10) {

            @Override
            public int getItemsCount() {
                return getTopicFacade().count();
            }

            @Override
            public DataModel createPageDataModel() {
                return null;
            }
        };

        return new ListDataModel(getTopicFacade().findRangeForCategory(new int[]{pagination.getPageFirstItem(), pagination.getPageFirstItem() + pagination.getPageSize()}, category));
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
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
        return JsfUtil.getSelectItems(getTopicFacade().findAll());
    }

    public Topic getTopic(java.lang.Integer id) {
        return getTopicFacade().find(id);
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
