// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.opensmartgridplatform.domain.core.validation.PageInfoConstraints;

@PageInfoConstraints
public class PageInfo implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 8425736412320281464L;

  @NotNull
  @Min(0)
  private final Integer currentPage;

  @NotNull
  @Min(1)
  private final Integer pageSize;

  @NotNull
  @Min(1)
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
