//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

public class ProfileEntryDto implements Serializable {

  private static final long serialVersionUID = 2123390296585369209L;

  private final List<ProfileEntryValueDto> profileEntryValues;

  public ProfileEntryDto(final List<ProfileEntryValueDto> profileEntryValues) {
    super();
    this.profileEntryValues = profileEntryValues;
  }

  public List<ProfileEntryValueDto> getProfileEntryValues() {
    return this.profileEntryValues;
  }
}
