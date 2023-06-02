//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.networking;

import org.opensmartgridplatform.oslp.OslpEnvelope;

public interface OslpResponseHandler {

  void handleResponse(OslpEnvelope oslpResponse);

  void handleException(Throwable t);
}
