/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.util.List;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion;

public class UpdateFirmwareResponse extends FirmwareVersionResponse {

  private static final long serialVersionUID = 7383932230545675913L;

  public UpdateFirmwareResponse(final List<FirmwareVersion> firmwareVersions) {
    super(firmwareVersions);
  }
}
