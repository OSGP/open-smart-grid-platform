// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.microgrids;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileDto implements Serializable {

  private static final long serialVersionUID = 7279719312339028843L;

  private Integer id;
  private String node;
  private List<ProfileEntryDto> profileEntries;

  public ProfileDto(
      final Integer id, final String node, final List<ProfileEntryDto> profileEntries) {
    this.id = id;
    this.node = node;
    this.profileEntries = new ArrayList<>(profileEntries);
  }

  public Integer getId() {
    return this.id;
  }

  public String getNode() {
    return this.node;
  }

  public List<ProfileEntryDto> getProfileEntries() {
    return Collections.unmodifiableList(this.profileEntries);
  }
}
