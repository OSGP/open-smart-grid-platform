//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccessSelectorList implements Serializable {
  private static final long serialVersionUID = -923356665614842594L;

  private final List<Integer> accessSelector;

  public AccessSelectorList(final List<Integer> accessSelector) {
    this.accessSelector = Collections.unmodifiableList(accessSelector);
  }

  public List<Integer> getAccessSelector() {
    return new ArrayList<>(this.accessSelector);
  }
}
