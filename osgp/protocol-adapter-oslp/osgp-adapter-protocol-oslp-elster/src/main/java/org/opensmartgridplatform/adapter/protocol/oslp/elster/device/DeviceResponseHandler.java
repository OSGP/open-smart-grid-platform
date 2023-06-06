// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.device;

public interface DeviceResponseHandler {

  void handleResponse(DeviceResponse deviceResponse);

  void handleException(Throwable t, DeviceResponse deviceResponse);
}
