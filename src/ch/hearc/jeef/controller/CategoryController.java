package ch.hearc.jeef.controller;

import ch.hearc.jeef.entities.Category;
import ch.hearc.jeef.facade.CategoryFacade;
import ch.hearc.jeef.util.JsfUtil;
import ch.hearc.jeef.util.PaginationHelper;
import java.io.Serializable;
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
import javax.inject.Named;

@Named("categoryController")
@SessionScoped
public class CategoryController implements Serializable {

    private Category current;
    private DataModel items = null;
    @EJB
    private CategoryFacade categoryFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public CategoryController() {
    }

    public Category getSelected() {
        recreateModel();
        final String ID_KEY = "id";
        String id = JsfUtil.getRequestParameter(ID_KEY);
        if (id != null) {
            current = getCategory(Integer.valueOf(id));
        }
        else if (current == null) {
            current = new Category();
            selectedItemIndex = -1;
        }
        return current;
    }

    private CategoryFacade getFacade() {
        return categoryFacade;
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
        return "/category/List";
    }

    public void prepareEdit() {
        current = null;
        selectedItemIndex = -1;
    }

    public void prepareCreate() {
        current = new Category();
        selectedItemIndex = -1;
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Localization").getString("CategoryCreated"));
            return categoryViewFullURL(current);
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Localization").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Localization").getString("CategoryUpdated"));
            return categoryViewFullURL(current);
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Localization").getString("PersistenceErrorOccured"));
            return "/category/Edit.xhtml?id=" + Integer.toString(current.getId());
        }
    }

    public String destroy() {
        current = (Category) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "/category/List";
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Localization").getString("CategoryDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Localization").getString("PersistenceErrorOccured"));
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    public void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "/category/List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "/category/List";
    }

    public String setPage(int page) {
        getPagination().setPage(page);
        recreateModel();
        return "/category/List";
    }

    public SelectItem[] getItemsAvailableSelect() {
        return JsfUtil.getSelectItems(categoryFacade.findAll());
    }

    public Category getCategory(java.lang.Integer id) {
        return categoryFacade.find(id);
    }

    public static String categoryCreateFullURL() {
        return "/category/Create.xhtml";
    }

    public static String categoryViewFullURL(Category category) {
        return "/category/View.xhtml?id=" + Integer.toString(category.getId()) + "&amp;faces-redirect=true&amp;includeViewParams=true";
    }

    public static String categoryEditFullURL(Category category) {
        return "/category/Edit.xhtml?id=" + Integer.toString(category.getId()) + "&amp;faces-redirect=true&amp;includeViewParams=true";
    }

    @FacesConverter(forClass = Category.class)
    public static class CategoryControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CategoryController controller = (CategoryController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "categoryController");
            return controller.getCategory(Integer.valueOf(value));
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Category) {
                Category o = (Category) object;
                return String.valueOf(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Category.class.getName());
            }
        }

    }

}
