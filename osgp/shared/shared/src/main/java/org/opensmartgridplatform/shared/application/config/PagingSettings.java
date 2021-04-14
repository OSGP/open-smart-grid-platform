/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.config;

public class PagingSettings {

  private int maximumPageSize;
  private int defaultPageSize;

  private int pageSize = 0;
  private int pageNumber = 0;

  public PagingSettings(final int maximumPageSize, final int defaultPageSize) {
    this.maximumPageSize = maximumPageSize;
    this.defaultPageSize = defaultPageSize;
  }

  public int getMaximumPageSize() {
    return this.maximumPageSize;
  }

  public int getDefaultPageSize() {
    return this.defaultPageSize;
  }

  public int getPageSize() {
    return this.pageSize;
  }

  public int getPageNumber() {
    return this.pageNumber;
  }

  public void updatePagingSettings(final PageSpecifier pageSpecifier) {
    updatePagingSettings(pageSpecifier.getPageSize(), pageSpecifier.getPageNumber());
  }

  private void updatePagingSettings(final Integer pageSize, final Integer pageNumber) {
    this.pageSize =
        pageSize == null
            ? this.defaultPageSize
            : (pageSize > this.maximumPageSize ? this.maximumPageSize : pageSize);
    this.pageNumber = pageNumber == null ? 0 : pageNumber;
  }
}
