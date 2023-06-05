// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.microgrids;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SetDataRequestDto implements Serializable {

  private static final long serialVersionUID = 2354993345497992666L;

  private List<SetDataSystemIdentifierDto> setDataSystemIdentifiers;

  public SetDataRequestDto(final List<SetDataSystemIdentifierDto> setDataSystemIdentifiers) {
    this.setDataSystemIdentifiers = new ArrayList<>(setDataSystemIdentifiers);
  }

  public List<SetDataSystemIdentifierDto> getSetDataSystemIdentifiers() {
    return Collections.unmodifiableList(this.setDataSystemIdentifiers);
  }
}
