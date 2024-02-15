// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class SetDeviceCommunicationSettingsRequestDataDto implements ActionRequestDto {

  private static final long serialVersionUID = 3283815451328003248L;

  private final int challengeLength;
  private final int withListMax;
  private final boolean selectiveAccessSupported;
  private final boolean ipAddressIsStatic;
  private final boolean useSn;
  private final boolean useHdlc;
  private final boolean polyphase;

  public SetDeviceCommunicationSettingsRequestDataDto(
      final int challengeLength,
      final int withListMax,
      final boolean selectiveAccessSupported,
      final boolean ipAddressIsStatic,
      final boolean useSn,
      final boolean useHdlc,
      final boolean polyphase) {
    this.challengeLength = challengeLength;
    this.withListMax = withListMax;
    this.selectiveAccessSupported = selectiveAccessSupported;
    this.ipAddressIsStatic = ipAddressIsStatic;
    this.useSn = useSn;
    this.useHdlc = useHdlc;
    this.polyphase = polyphase;
  }

  public int getChallengeLength() {
    return this.challengeLength;
  }

  public int getWithListMax() {
    return this.withListMax;
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

  public boolean isPolyphase() {
    return this.polyphase;
  }
}
