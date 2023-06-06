// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;
import java.util.List;

public class LightValueMessageDataContainerDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 4725254533964342905L;

  private List<LightValueDto> lightValues;

  public LightValueMessageDataContainerDto(final List<LightValueDto> lightValues) {
    this.lightValues = lightValues;
  }

  public List<LightValueDto> getLightValues() {
    return this.lightValues;
  }
}
