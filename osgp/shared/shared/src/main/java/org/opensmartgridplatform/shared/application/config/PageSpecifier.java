/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.config;

public class PageSpecifier {
  private final Integer pageSize;
  private final Integer pageNumber;

  public PageSpecifier(final Integer pageSize, final Integer pageNumber) {
    this.pageSize = pageSize;
    this.pageNumber = pageNumber;
  }

  public Integer getPageSize() {
    return this.pageSize;
  }

  public Integer getPageNumber() {
    return this.pageNumber;
  }
}
