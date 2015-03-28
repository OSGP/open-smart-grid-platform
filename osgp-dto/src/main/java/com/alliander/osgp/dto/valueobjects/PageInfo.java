package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;

public class PageInfo implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 796943881244400914L;

    private final Integer currentPage;

    private final Integer pageSize;

    private final Integer totalPages;

    public PageInfo(final Integer currentPage, final Integer pageSize, final Integer totalPages) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
    }

    public Integer getCurrentPage() {
        return this.currentPage;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public Integer getTotalPages() {
        return this.totalPages;
    }
}
