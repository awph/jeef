package ch.hearc.jeef.util;

import java.util.ArrayList;
import java.util.List;
import javax.faces.model.DataModel;

public abstract class PaginationHelper {

    private int pageSize;
    private int page;

    public PaginationHelper(int pageSize) {
        this.pageSize = pageSize;
    }

    public abstract int getItemsCount();

    public abstract DataModel createPageDataModel();

    public int getPageFirstItem() {
        return page * pageSize;
    }

    public int getPageLastItem() {
        int i = getPageFirstItem() + pageSize - 1;
        int count = getItemsCount() - 1;
        if (i > count) {
            i = count;
        }
        if (i < 0) {
            i = 0;
        }
        return i;
    }

    public boolean isHasNextPage() {
        return (page + 1) * pageSize + 1 <= getItemsCount();
    }

    public void nextPage() {
        if (isHasNextPage()) {
            page++;
        }
    }

    public void setPage(int page) {
        this.page = page - 1;
    }

    public boolean isHasPreviousPage() {
        return page > 0;
    }

    public void previousPage() {
        if (isHasPreviousPage()) {
            page--;
        }
    }

    public void lastPage() {
        page = getNumberPages() - 1;
    }

    public int getNumberPages() {
        return (int) Math.ceil(getItemsCount() / (double) getPageSize());
    }

    public int getPageSize() {
        return pageSize;
    }

    public List<Integer> getPreviousPages() {
        List<Integer> pages = new ArrayList<>();
        for (int i = 0, j = 0; i < page && j < 5; ++i, ++j) {
            pages.add(i + 1);
        }
        return pages;
    }

    public int getCurrentPage() {
        return page + 1;
    }

    public List<Integer> getNextPages() {
        List<Integer> pages = new ArrayList<>();
        for (int i = page + 1, j = 0; i < getNumberPages() && j < 5; ++i, ++j) {
            pages.add(i + 1);
        }
        return pages;
    }
}
