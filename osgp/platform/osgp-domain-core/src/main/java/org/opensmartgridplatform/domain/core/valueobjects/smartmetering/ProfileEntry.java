// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

public class ProfileEntry implements Serializable {

  private static final long serialVersionUID = 991045734132231709L;

  private final List<ProfileEntryValue> profileEntryValues;

  public ProfileEntry(final List<ProfileEntryValue> profileEntryValues) {
    super();
    this.profileEntryValues = profileEntryValues;
  }

  public List<ProfileEntryValue> getProfileEntryValues() {
    return this.profileEntryValues;
  }
}
