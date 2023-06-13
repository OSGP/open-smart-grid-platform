// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

/** */
@SoapFault(faultCode = FaultCode.SERVER)
public class ChannelAlreadyOccupiedException extends PlatformException {

  /** Serial Version UID. */
  private static final long serialVersionUID = 1183467687756299089L;

  private static final String MESSAGE = "There is already a device coupled on Mbus channel %d";

  /**
   * Constructor
   *
   * @param channel the {@link Short} channel number
   * @param identification the device identification
   * @param throwable
   */
  public ChannelAlreadyOccupiedException(final Short channel, final Throwable throwable) {
    super(String.format(MESSAGE, channel), throwable);
  }

  /**
   * Constructor
   *
   * @param channel the {@link Short} channel number
   * @param identification the device identification
   */
  public ChannelAlreadyOccupiedException(final short channel) {
    super(String.format(MESSAGE, channel));
  }
}
