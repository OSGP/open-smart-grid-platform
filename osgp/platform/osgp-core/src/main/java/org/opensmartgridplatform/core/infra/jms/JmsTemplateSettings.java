//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.core.infra.jms;

public class JmsTemplateSettings {

  private boolean explicitQosEnabled;
  private long timeToLive;
  private boolean deliveryPersistent;

  public JmsTemplateSettings(
      final boolean explicitQosEnabled, final long timeToLive, final boolean deliveryPersistent) {
    this.explicitQosEnabled = explicitQosEnabled;
    this.timeToLive = timeToLive;
    this.deliveryPersistent = deliveryPersistent;
  }

  public boolean isExplicitQosEnabled() {
    return this.explicitQosEnabled;
  }

  public long getTimeToLive() {
    return this.timeToLive;
  }

  public boolean isDeliveryPersistent() {
    return this.deliveryPersistent;
  }
}
