//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.security;

import lombok.AccessLevel;
import lombok.Getter;
import org.openmuc.jdlms.SecurityUtils;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;

@Getter
class ReplaceKeyInput {
  @Getter(AccessLevel.PROTECTED)
  private final byte[] bytes;

  private final SecurityUtils.KeyId keyId;
  private final SecurityKeyType securityKeyType;
  private final boolean isGenerated;

  public ReplaceKeyInput(
      final byte[] bytes,
      final SecurityUtils.KeyId keyId,
      final SecurityKeyType securityKeyType,
      final boolean isGenerated) {
    this.bytes = bytes;
    this.keyId = keyId;
    this.securityKeyType = securityKeyType;
    this.isGenerated = isGenerated;
  }

  static ReplaceKeyInput from(
      final byte[] bytes,
      final SecurityUtils.KeyId keyId,
      final SecurityKeyType securityKeyType,
      final boolean isGenerated) {
    return new ReplaceKeyInput(bytes, keyId, securityKeyType, isGenerated);
  }
}
