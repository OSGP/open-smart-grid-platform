/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
