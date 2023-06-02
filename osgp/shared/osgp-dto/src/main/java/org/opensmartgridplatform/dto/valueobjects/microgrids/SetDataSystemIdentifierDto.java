//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.microgrids;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SetDataSystemIdentifierDto extends SystemIdentifierDto {

  private static final long serialVersionUID = 1491574329325798488L;

  private List<SetPointDto> setPoints;
  private List<ProfileDto> profiles;

  public SetDataSystemIdentifierDto(
      final int id,
      final String systemType,
      final List<SetPointDto> setPoints,
      final List<ProfileDto> profiles) {
    super(id, systemType);
    this.setPoints = new ArrayList<>(setPoints);
    this.profiles = new ArrayList<>(profiles);
  }

  public List<SetPointDto> getSetPoints() {
    return Collections.unmodifiableList(this.setPoints);
  }

  public List<ProfileDto> getProfiles() {
    return Collections.unmodifiableList(this.profiles);
  }
}
