//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.microgrids;

import java.io.Serializable;

public class ProfileFilterDto extends ProfileIdentifierDto implements Serializable {

  private static final long serialVersionUID = -6058020706641320400L;

  private boolean all;

  public ProfileFilterDto(final int id, final String node, final boolean all) {
    super(id, node);
    this.all = all;
  }

  public boolean isAll() {
    return this.all;
  }
}
