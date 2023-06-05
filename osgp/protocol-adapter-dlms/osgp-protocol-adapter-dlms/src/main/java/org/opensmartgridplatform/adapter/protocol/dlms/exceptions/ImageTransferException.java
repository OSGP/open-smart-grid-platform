// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.exceptions;

import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;

public class ImageTransferException extends OsgpException {

  private static final long serialVersionUID = -3723899623610781058L;

  public ImageTransferException(final String message, final Throwable cause) {
    super(ComponentType.PROTOCOL_DLMS, message, cause);
  }

  public ImageTransferException(final String message) {
    super(ComponentType.PROTOCOL_DLMS, message);
  }
}
