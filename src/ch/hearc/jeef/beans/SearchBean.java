package ch.hearc.jeef.beans;

import ch.hearc.jeef.entities.Category;
import ch.hearc.jeef.entities.Topic;
import ch.hearc.jeef.facade.TopicFacade;
import ch.hearc.jeef.util.PaginationHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

/**
 *
 * @author Alexandre
 */
@ManagedBean
@SessionScoped
public class SearchBean {

    private static final String DATE = "Date";
    private static final String USERNAME = "Username";
    private static final String TOPIC = "Topic";
    private static final String CATEGORY = "Category";
    private static final String[] SORT_LIST = new String[]{DATE, USERNAME, TOPIC, CATEGORY};
    private static final String DESC = "Descending";
    private static final String ASC = "Ascending";
    private static final String[] ORDER_LIST = new String[]{DESC, ASC};
    private static final String SEPARATION = " ";

    private String keywords;
    private String username;
    private String sort;
    private String order;
    private Category category;
    private Boolean advanced;
    private Boolean useDates;
    @EJB
    private TopicFacade topicFacade;
    private PaginationHelper pagination;

    /**
     * Creates a new instance of SearchBean
     */
    public SearchBean() {
        advanced = false;
    }

    private TopicFacade getTopicFacade() {
        return topicFacade;
    }

    private List<String> getKeywordsList() {
        if (getDidSearch()) {
            List<String> keywords = Arrays.asList(getKeywords().split(SEPARATION));
            //TODO clean input
            return keywords;
        }
        return null;
    }

    private Integer getItemsCount() {
        if (!advanced) {
            return getTopicFacade().countForKeywords(getKeywordsList());
        } else {
            return getTopicFacade().countAdvanced(getKeywordsList(), getUsername(), getCategory());
        }
    }

    private DataModel createPageDataModel(int pageFirstItem, int pageLastItem) {
        if (!advanced) {
            return new ListDataModel(getTopicFacade().findRangeForKeywords(new int[]{pageFirstItem, pageLastItem}, getKeywordsList()));
        } else {
            return new ListDataModel(getTopicFacade().findRangeAdvanced(new int[]{pageFirstItem, pageLastItem}, getKeywordsList(), getUsername(), getCategory(), getFilteredSort(), isDesc(order)));
        }
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return SearchBean.this.getItemsCount();
                }

                @Override
                public DataModel createPageDataModel() {
                    return SearchBean.this.createPageDataModel(getPageFirstItem(), getPageLastItem());
                }
            };
        }

        return pagination;
    }

    public DataModel<Topic> getItems() {
        return getPagination().createPageDataModel();
    }

    public List<SelectItem> getSortAvailableSelect() {
        List<SelectItem> list = new ArrayList<>();
        for (String sort : SORT_LIST) {
            list.add(new SelectItem(sort, sort));
        }
        return list;
    }

    public List<SelectItem> getOrderAvailableSelect() {
        List<SelectItem> list = new ArrayList<>();
        for (String order : ORDER_LIST) {
            list.add(new SelectItem(order, order));
        }
        return list;
    }

    public String search() {
        advanced = false;
        return null;
    }

    public String searchAdvanced() {
        advanced = true;
        return null;
    }

    public String next() {
        getPagination().nextPage();
        return null;
    }

    public String previous() {
        getPagination().previousPage();
        return null;
    }

    public String setPage(int page) {
        getPagination().setPage(page);
        return null;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getSort() {
        return sort;
    }
    
    public String getFilteredSort() {
        if(Arrays.asList(SORT_LIST).contains(getSort())) {
            return getSort();
        }
        else {
            return DATE;
        }
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Boolean getUseDates() {
        return useDates;
    }

    public void setUseDates(Boolean useDates) {
        this.useDates = useDates;
    }

    public Boolean getDidSearch() {
        return getKeywords() != null && getKeywords().length() > 0;
    }

    private static Boolean isDesc(String order) {
        return order.equals(DESC);
    }
}
