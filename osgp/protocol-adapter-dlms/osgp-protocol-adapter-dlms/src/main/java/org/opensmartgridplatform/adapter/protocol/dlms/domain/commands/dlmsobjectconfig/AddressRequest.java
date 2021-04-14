/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.Medium;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;

public class AddressRequest {
  private final DlmsDevice device;
  private final DlmsObject dlmsObject;
  private final Integer channel;
  private final DateTime from;
  private final DateTime to;
  private final Medium filterMedium;

  public AddressRequest(
      final DlmsDevice device,
      final DlmsObject dlmsObject,
      final Integer channel,
      final DateTime from,
      final DateTime to,
      final Medium filterMedium) {
    this.device = device;
    this.dlmsObject = dlmsObject;
    this.channel = channel;
    this.from = from;
    this.to = to;
    this.filterMedium = filterMedium;
  }

  public DlmsDevice getDevice() {
    return this.device;
  }

  public DlmsObject getDlmsObject() {
    return this.dlmsObject;
  }

  public Integer getChannel() {
    return this.channel;
  }

  public DateTime getFrom() {
    return this.from;
  }

  public DateTime getTo() {
    return this.to;
  }

  public Medium getFilterMedium() {
    return this.filterMedium;
  }
}
