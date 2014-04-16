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
        return getPage() * pageSize;
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
        return (getPage() + 1) * pageSize + 1 <= getItemsCount();
    }

    public void nextPage() {
        if (isHasNextPage()) {
            page++;
        }
    }

    public void setPage(int page) {
        page--;
        if (page > getNumberPages() - 1) {
            lastPage();
        } else {
            this.page = page;
        }
        if (this.page < 0) {
            this.page = 0;
        }
    }

    public int getPage() {
        setPage(page + 1);
        return page;
    }

    public boolean isHasPreviousPage() {
        return getPage() > 0;
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
        for (int i = 0, j = 0; i < getPage() && j < 5; ++i, ++j) {
            pages.add(i + 1);
        }
        return pages;
    }

    public int getCurrentPage() {
        return getPage() + 1;
    }

    public List<Integer> getNextPages() {
        List<Integer> pages = new ArrayList<>();
        for (int i = getPage() + 1, j = 0; i < getNumberPages() && j < 5; ++i, ++j) {
            pages.add(i + 1);
        }
        return pages;
    }
}
