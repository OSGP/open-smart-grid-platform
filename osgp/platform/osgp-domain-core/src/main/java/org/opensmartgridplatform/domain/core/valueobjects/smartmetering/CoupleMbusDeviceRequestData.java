/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

/**
 * this class holds the information needed beside the metadata of a request to couple a device and a
 * m-bus device
 */
public class CoupleMbusDeviceRequestData implements Serializable, ActionRequest {

  private static final long serialVersionUID = 8993111326494612480L;

  private final String mbusDeviceIdentification;

  /**
   * @param mbusDeviceIdentification the mbus device that needs to be coupled to the device in the
   *     metadata information of the request
   * @param channel the channel on which the mbus device needs to be coupled
   */
  public CoupleMbusDeviceRequestData(final String mbusDeviceIdentification) {
    this.mbusDeviceIdentification = mbusDeviceIdentification;
  }

  public String getMbusDeviceIdentification() {
    return this.mbusDeviceIdentification;
  }

  @Override
  public void validate() throws FunctionalException {
    // nothing to validate
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.COUPLE_MBUS_DEVICE;
  }
}
