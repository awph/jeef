package ch.hearc.jeef.beans;

import ch.hearc.jeef.entities.Topic;
import ch.hearc.jeef.facade.TopicFacade;
import ch.hearc.jeef.util.PaginationHelper;
import java.util.Arrays;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 *
 * @author Alexandre
 */
@ManagedBean
@SessionScoped
public class SearchBean {

    private static final String SEPARATION = " ";

    private String keywords;
    private Boolean advanced;
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
        if (getKeywords() != null) {
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
            return 0;
        }
    }

    private DataModel createPageDataModel(int pageFirstItem, int pageLastItem) {
        if (!advanced) {
            return new ListDataModel(getTopicFacade().findRangeForKeywords(new int[]{pageFirstItem, pageLastItem}, getKeywordsList()));
        } else {
            return null;
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
}
