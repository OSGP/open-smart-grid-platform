// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.domain.entities;

import java.util.List;

/**
 * Simple information class for exposing page information.
 *
 * @param <T> object stored in contents.
 */
public class PageInfo<T> {
  /** Backing field */
  private int totalPages;

  /** Backing field */
  private List<T> contents;

  /**
   * Create instance of PageInfo.
   *
   * @param totalPages total number of pages available.
   * @param contents contents of the current page.
   */
  public PageInfo(final int totalPages, final List<T> contents) {
    this.totalPages = totalPages;
    this.contents = contents;
  }

  /**
   * Gets the total number of pages.
   *
   * @return total number of pages.
   */
  public int getTotalPages() {
    return this.totalPages;
  }

  /**
   * Gets the contents of the page.
   *
   * @return contents of the page.
   */
  public List<T> getContents() {
    return this.contents;
  }
}
