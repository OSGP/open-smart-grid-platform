// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.microgrids.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Profile implements Serializable {

  private static final long serialVersionUID = 7317782056712941895L;

  private Integer id;
  private String node;
  private List<ProfileEntry> profileEntries;

  public Profile(final Integer id, final String node, final List<ProfileEntry> profileEntries) {
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

  public List<ProfileEntry> getProfileEntries() {
    return new ArrayList<>(this.profileEntries);
  }
}
