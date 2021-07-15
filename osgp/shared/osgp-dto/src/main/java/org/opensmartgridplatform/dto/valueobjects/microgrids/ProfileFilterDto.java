/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
