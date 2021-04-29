/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping.converters;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping.AdHocManagementMapper;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.EventNotificationType;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceStatus;
import org.opensmartgridplatform.domain.core.valueobjects.LightSensorStatus;
import org.opensmartgridplatform.domain.core.valueobjects.LightSensorStatusType;
import org.opensmartgridplatform.domain.core.valueobjects.LightType;
import org.opensmartgridplatform.domain.core.valueobjects.LightValue;
import org.opensmartgridplatform.domain.core.valueobjects.LinkType;
import org.opensmartgridplatform.domain.core.valueobjects.Status;

class StatusMappingTest {
  private final AdHocManagementMapper adHocManagementMapper = new AdHocManagementMapper();

  @Test
  void properlyMapsDeviceStatus() {
    final LightValue lv1 = new LightValue(2, false, 0);
    final LightValue lv2 = new LightValue(3, true, 100);
    final List<LightValue> lightValues = Arrays.asList(lv1, lv2);
    final LinkType preferredLinkType = LinkType.CDMA;
    final LinkType actualLinkType = LinkType.GPRS;
    final LightType lightType = LightType.RELAY;
    final Integer eventNotificationsMask = 35;
    final Status domainStatus =
        new DeviceStatus(
            lightValues, preferredLinkType, actualLinkType, lightType, eventNotificationsMask);
    final org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.Status
        wsStatus =
            this.adHocManagementMapper.map(
                domainStatus,
                org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.Status
                    .class);
    final org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.DeviceStatus
        expected =
            new org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement
                .DeviceStatus();
    expected.setActualLinkType(
        org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.LinkType.GPRS);
    expected.setPreferredLinkType(
        org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.LinkType.CDMA);
    expected.setLightType(
        org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.LightType.RELAY);
    final org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.LightValue
        eLv1 =
            new org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement
                .LightValue();
    eLv1.setIndex(2);
    eLv1.setOn(false);
    eLv1.setDimValue(0);
    final org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.LightValue
        eLv2 =
            new org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement
                .LightValue();
    eLv2.setIndex(3);
    eLv2.setOn(true);
    eLv2.setDimValue(100);
    expected.getLightValues().add(eLv1);
    expected.getLightValues().add(eLv2);
    expected.getEventNotifications().add(EventNotificationType.DIAG_EVENTS);
    expected.getEventNotifications().add(EventNotificationType.HARDWARE_FAILURE);
    expected.getEventNotifications().add(EventNotificationType.FIRMWARE_EVENTS);
    assertThat(wsStatus)
        .isInstanceOf(
            org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.DeviceStatus
                .class)
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

  @Test
  void properlyMapsLightSensorStatus() {
    final Status domainStatus = new LightSensorStatus(LightSensorStatusType.LIGHT);
    final org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.Status
        wsStatus =
            this.adHocManagementMapper.map(
                domainStatus,
                org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.Status
                    .class);
    final org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement
            .LightSensorStatus
        expected =
            new org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement
                .LightSensorStatus();
    expected.setStatus(
        org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement
            .LightSensorStatusType.LIGHT);
    assertThat(wsStatus)
        .isInstanceOf(
            org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement
                .LightSensorStatus.class)
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }
}
