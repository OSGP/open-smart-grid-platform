// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.networking;

import org.opensmartgridplatform.oslp.LegacyOslpEnvelope;

public interface OslpResponseHandler {

  void handleResponse(LegacyOslpEnvelope oslpResponse);

  void handleException(Throwable t);
}
