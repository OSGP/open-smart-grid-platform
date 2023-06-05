// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
