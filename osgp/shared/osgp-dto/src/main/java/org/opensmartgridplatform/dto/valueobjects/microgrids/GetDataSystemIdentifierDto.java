//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.microgrids;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetDataSystemIdentifierDto extends SystemIdentifierDto implements Serializable {

  private static final long serialVersionUID = -1981029545705567105L;

  private List<MeasurementDto> measurements;
  private List<ProfileDto> profiles;

  public GetDataSystemIdentifierDto(
      final int id, final String systemType, final List<MeasurementDto> measurements) {
    this(id, systemType, measurements, new ArrayList<>());
  }

  public GetDataSystemIdentifierDto(
      final int id,
      final String systemType,
      final List<MeasurementDto> measurements,
      final List<ProfileDto> profiles) {
    super(id, systemType);
    this.measurements = new ArrayList<>(measurements);
    this.profiles = new ArrayList<>(profiles);
  }

  public List<MeasurementDto> getMeasurements() {
    return Collections.unmodifiableList(this.measurements);
  }

  public List<ProfileDto> getProfiles() {
    return Collections.unmodifiableList(this.profiles);
  }
}
