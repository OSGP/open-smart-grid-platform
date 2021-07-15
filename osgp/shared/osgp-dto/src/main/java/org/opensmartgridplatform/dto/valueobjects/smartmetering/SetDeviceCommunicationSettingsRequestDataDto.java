/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class SetDeviceCommunicationSettingsRequestDataDto implements ActionRequestDto {

  private static final long serialVersionUID = 3283815451328003248L;

  private final int challengeLength;
  private final boolean withListSupported;
  private final boolean selectiveAccessSupported;
  private final boolean ipAddressIsStatic;
  private final boolean useSn;
  private final boolean useHdlc;

  public SetDeviceCommunicationSettingsRequestDataDto(
      final int challengeLength,
      final boolean withListSupported,
      final boolean selectiveAccessSupported,
      final boolean ipAddressIsStatic,
      final boolean useSn,
      final boolean useHdlc) {
    this.challengeLength = challengeLength;
    this.withListSupported = withListSupported;
    this.selectiveAccessSupported = selectiveAccessSupported;
    this.ipAddressIsStatic = ipAddressIsStatic;
    this.useSn = useSn;
    this.useHdlc = useHdlc;
  }

  public int getChallengeLength() {
    return this.challengeLength;
  }

  public boolean isWithListSupported() {
    return this.withListSupported;
  }

  public boolean isSelectiveAccessSupported() {
    return this.selectiveAccessSupported;
  }

  public boolean isIpAddressIsStatic() {
    return this.ipAddressIsStatic;
  }

  public boolean isUseSn() {
    return this.useSn;
  }

  public boolean isUseHdlc() {
    return this.useHdlc;
  }
}
