//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.exceptions;

import com.beanit.openiec61850.FcModelNode;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.ConnectionState;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;

/**
 * Thrown when a {@link FcModelNode} with objectReference does not exist in {@link DeviceConnection}
 */
public class NodeNotFoundException extends NodeException {

  /** Serial Version UID. */
  private static final long serialVersionUID = 4653197028402690415L;

  public NodeNotFoundException(final String message) {
    super(message);
  }

  public NodeNotFoundException(final String message, final Throwable throwable) {
    super(message, throwable);
  }

  public NodeNotFoundException(
      final String message, final Throwable throwable, final ConnectionState connectionState) {
    super(message, throwable, connectionState);
  }
}
