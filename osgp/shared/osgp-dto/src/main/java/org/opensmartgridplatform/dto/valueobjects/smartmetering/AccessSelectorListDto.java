// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccessSelectorListDto implements Serializable {
  private static final long serialVersionUID = 6844148787752579231L;

  private final List<Integer> accessSelector;

  public AccessSelectorListDto(final List<Integer> accessSelector) {
    this.accessSelector = Collections.unmodifiableList(accessSelector);
  }

  public List<Integer> getAccessSelector() {
    return new ArrayList<>(this.accessSelector);
  }
}
