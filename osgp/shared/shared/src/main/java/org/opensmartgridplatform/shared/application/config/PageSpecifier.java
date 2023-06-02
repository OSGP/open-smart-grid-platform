//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
