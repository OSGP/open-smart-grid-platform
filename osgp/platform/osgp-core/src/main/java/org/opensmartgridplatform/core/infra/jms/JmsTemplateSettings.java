/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
