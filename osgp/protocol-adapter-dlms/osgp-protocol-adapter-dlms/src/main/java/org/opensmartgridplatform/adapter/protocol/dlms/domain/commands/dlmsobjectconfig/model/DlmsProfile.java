/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model;

import java.util.List;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsCaptureObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;

public class DlmsProfile extends DlmsObject {
  private static final int CLASS_ID_PROFILE = 7;

  private final List<DlmsCaptureObject> captureObjects;
  private final ProfileCaptureTime captureTime;
  private final Medium medium;

  public DlmsProfile(
      final DlmsObjectType type,
      final String obisCode,
      final List<DlmsCaptureObject> captureObjects,
      final ProfileCaptureTime captureTime,
      final Medium medium) {
    super(type, CLASS_ID_PROFILE, obisCode);
    this.captureObjects = captureObjects;
    this.captureTime = captureTime;
    this.medium = medium;
  }

  public List<DlmsCaptureObject> getCaptureObjects() {
    return this.captureObjects;
  }

  public ProfileCaptureTime getCaptureTime() {
    return this.captureTime;
  }

  public Medium getMedium() {
    return this.medium;
  }
}
