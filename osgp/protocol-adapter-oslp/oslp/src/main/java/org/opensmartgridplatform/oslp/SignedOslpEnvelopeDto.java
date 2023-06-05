// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.oslp;

import java.io.Serializable;

public class SignedOslpEnvelopeDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 2598572730380822536L;

  /** The signed envelope which can be sent to a device. */
  private OslpEnvelope oslpEnvelope;

  /** The DTO which was sent to the signing server. */
  private UnsignedOslpEnvelopeDto unsignedOslpEnvelopeDto;

  public SignedOslpEnvelopeDto(
      final OslpEnvelope oslpEnvelope, final UnsignedOslpEnvelopeDto unsignedOslpEnvelopeDto) {
    this.oslpEnvelope = oslpEnvelope;
    this.unsignedOslpEnvelopeDto = unsignedOslpEnvelopeDto;
  }

  public OslpEnvelope getOslpEnvelope() {
    return this.oslpEnvelope;
  }

  public UnsignedOslpEnvelopeDto getUnsignedOslpEnvelopeDto() {
    return this.unsignedOslpEnvelopeDto;
  }
}
