// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class SetDeviceCommunicationSettingsData implements Serializable, ActionRequest {

  private static final long serialVersionUID = -6825967173128763377L;

  private final int challengeLength;
  private final boolean withListSupported;
  private final boolean selectiveAccessSupported;
  private final boolean ipAddressIsStatic;
  private final boolean useSn;
  private final boolean useHdlc;
  private final boolean polyphase;

  public SetDeviceCommunicationSettingsData(
      final int challengeLength,
      final boolean withListSupported,
      final boolean selectiveAccessSupported,
      final boolean ipAddressIsStatic,
      final boolean useSn,
      final boolean useHdlc,
      final boolean polyphase) {
    this.challengeLength = challengeLength;
    this.withListSupported = withListSupported;
    this.selectiveAccessSupported = selectiveAccessSupported;
    this.ipAddressIsStatic = ipAddressIsStatic;
    this.useSn = useSn;
    this.useHdlc = useHdlc;
    this.polyphase = polyphase;
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

  public boolean isPolyphase() {
    return this.polyphase;
  }

  @Override
  public void validate() throws FunctionalException {
    // not needed here
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.SET_DEVICE_COMMUNICATION_SETTINGS;
  }
}
