// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping.converters;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping.AdHocManagementMapper;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.EventNotificationType;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceStatus;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceStatusMapped;
import org.opensmartgridplatform.domain.core.valueobjects.LightSensorStatus;
import org.opensmartgridplatform.domain.core.valueobjects.LightSensorStatusType;
import org.opensmartgridplatform.domain.core.valueobjects.LightType;
import org.opensmartgridplatform.domain.core.valueobjects.LightValue;
import org.opensmartgridplatform.domain.core.valueobjects.LinkType;
import org.opensmartgridplatform.domain.core.valueobjects.Status;
import org.opensmartgridplatform.domain.core.valueobjects.TariffValue;

class StatusMappingTest {

  private static final LinkType PREFERRED_LINK_TYPE = LinkType.CDMA;
  private static final LinkType ACTUAL_LINK_TYPE = LinkType.GPRS;
  private static final LightType LIGHT_TYPE = LightType.RELAY;
  private static final Integer EVENT_NOTIFICATIONS_MASK = 35;

  private static final LightValue LV1 = new LightValue(2, false, 0);
  private static final LightValue LV2 = new LightValue(3, true, 100);
  private static final List<LightValue> LIGHT_VALUES = Arrays.asList(LV1, LV2);

  private final AdHocManagementMapper adHocManagementMapper = new AdHocManagementMapper();

  @Test
  void properlyMapsDeviceStatus() {
    final Status domainStatus =
        new DeviceStatus(
            LIGHT_VALUES,
            PREFERRED_LINK_TYPE,
            ACTUAL_LINK_TYPE,
            LIGHT_TYPE,
            EVENT_NOTIFICATIONS_MASK);

    final org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.Status
        wsStatus =
            this.adHocManagementMapper.map(
                domainStatus,
                org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.Status
                    .class);

    final org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.DeviceStatus
        expected = expectedDeviceStatus();

    assertThat(wsStatus)
        .isInstanceOf(
            org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.DeviceStatus
                .class)
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

  @Test
  void properlyMapsDeviceStatusMapped() {
    final TariffValue tv1 = new TariffValue();
    tv1.setHigh(true);
    final List<TariffValue> tariffValues = Arrays.asList(tv1);

    final Status domainStatus =
        new DeviceStatusMapped(
            tariffValues,
            LIGHT_VALUES,
            PREFERRED_LINK_TYPE,
            ACTUAL_LINK_TYPE,
            LIGHT_TYPE,
            EVENT_NOTIFICATIONS_MASK);

    final org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.Status
        wsStatus =
            this.adHocManagementMapper.map(
                domainStatus,
                org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.Status
                    .class);

    final org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.DeviceStatus
        expected = expectedDeviceStatus();

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

  private static org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement
          .DeviceStatus
      expectedDeviceStatus() {
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
    eLv1.setDimValue(null);
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

    return expected;
  }
}
