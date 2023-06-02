//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.shared.domain.entities;

import java.util.List;

public class SliceInfo<T> {

  private final boolean nextPageAvailable;

  private final List<T> contents;

  public SliceInfo(final boolean nextPageAvailable, final List<T> contents) {
    this.nextPageAvailable = nextPageAvailable;
    this.contents = contents;
  }

  public boolean isNextPageAvailable() {
    return this.nextPageAvailable;
  }

  public List<T> getContents() {
    return this.contents;
  }
}
