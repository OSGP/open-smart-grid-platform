// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging;

import org.opensmartgridplatform.oslp.SignedOslpEnvelopeDto;

public interface OslpEnvelopeProcessor {

  void processSignedOslpEnvelope(
      String deviceIdentification, SignedOslpEnvelopeDto signedOslpEnvelopeDto);
}
