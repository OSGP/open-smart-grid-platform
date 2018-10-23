/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.valueobjects;

// 1-based pager
public class Pager {
    private int currentPage = 1;
    private int itemCount = 0;
    private int pageSize = 5;
    private int numberOfPages = 1;

    public Pager() {
        // Default constructor.
    }

    public Pager(final int itemCount, final int pageSize) {
        this.itemCount = itemCount;
        this.pageSize = pageSize;
        this.numberOfPages = (int) Math.ceil((double) itemCount / (double) pageSize);
    }

    public Pager(final int numberOfPages, final int pageSize, final int currentPage) {
        this.numberOfPages = numberOfPages;
        this.pageSize = pageSize;
        this.currentPage = currentPage;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public int getIndexFrom() {
        return this.pageSize * (this.currentPage - 1);
    }

    public int getIndexTo() {
        return Math.min(this.pageSize * this.currentPage, this.itemCount);
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public int getNumberOfPages() {
        return this.numberOfPages;
    }

    public void setNumberOfPages(final int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public void nextPage() {
        this.currentPage++;
    }

    public boolean isLastPage() {
        return this.currentPage == this.numberOfPages;
    }

    public void setCurrentPage(final int currentPage) {
        this.currentPage = currentPage;
    }
}
