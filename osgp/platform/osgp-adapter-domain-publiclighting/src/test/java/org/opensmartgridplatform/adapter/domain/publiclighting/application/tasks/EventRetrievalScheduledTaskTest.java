// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.publiclighting.application.tasks;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class EventRetrievalScheduledTaskTest {

  @InjectMocks private EventRetrievalScheduledTask task;

  @BeforeEach
  public void initProperties() {
    ReflectionTestUtils.setField(this.task, "eventRetrievalScheduledTaskBackOffMultiplier", 2);
    ReflectionTestUtils.setField(this.task, "eventRetrievalScheduledTaskDefaultWaitTime", 30);
    ReflectionTestUtils.setField(this.task, "eventRetrievalScheduledTaskMaxBackoff", 1440);
  }

  /*
   * Happy case, device without communication errors should be included in the
   * resulting list.
   */
  @Test
  public void applyExponentialBackOffToListOfDevicesWithoutCommunicationErrors() {
    final List<Device> devices = new ArrayList<>();
    devices.add(
        this.createDevice(
            "device-01",
            Date.from(ZonedDateTime.now(ZoneId.of("UTC")).minusDays(15).toInstant()),
            0));

    final List<Device> filteredList = this.task.filterByExponentialBackOff(devices);

    assertThat(filteredList.size()).isEqualTo(1);
  }

  /*
   * Device with failed communication time stamp older than 24 hours
   * (eventRetrievalScheduledTaskMaxBackoff) should be included in the
   * resulting list. The failed connection count is not relevant in this case.
   */
  @Test
  public void
      applyExponentialBackOffToListOfDevicesWithCommunicationTimestampOlderThan24HoursAndManyConnectionsFailure() {
    final List<Device> devices = new ArrayList<>();
    devices.add(
        this.createDevice(
            "device-02",
            Date.from(ZonedDateTime.now(ZoneId.of("UTC")).minusDays(2).toInstant()),
            100));

    final List<Device> filteredList = this.task.filterByExponentialBackOff(devices);

    assertThat(filteredList.size()).isEqualTo(1);
  }

  /*
   * Device with failed communication time stamp younger than 24 hours and 1
   * connection failure should be included in the resulting list ( threshold
   * is 2^1*30=60 ).
   */
  @Test
  public void
      applyExponentialBackOffToListOfDevicesWithCommunicationTimestampYoungerThan24HoursAnd1ConnectionFailure() {
    final List<Device> devices = new ArrayList<>();
    devices.add(
        this.createDevice(
            "device-03",
            Date.from(ZonedDateTime.now(ZoneId.of("UTC")).minusHours(23).toInstant()),
            1));

    final List<Device> filteredList = this.task.filterByExponentialBackOff(devices);

    assertThat(filteredList.size()).isEqualTo(1);
  }

  /*
   * Device with failed communication time stamp older than 1 hour and 1
   * connection failure should be included in the resulting list ( threshold
   * is 2^1*30=60 ).
   */
  @Test
  public void
      applyExponentialBackOffToListOfDevicesWithCommunicationTimestampExactly1HourAnd1ConnectionFailure() {
    final List<Device> devices = new ArrayList<>();
    devices.add(
        this.createDevice(
            "device-04",
            Date.from(ZonedDateTime.now(ZoneId.of("UTC")).minusMinutes(61).toInstant()),
            1));

    final List<Device> filteredList = this.task.filterByExponentialBackOff(devices);

    assertThat(filteredList.size()).isEqualTo(1);
  }

  /*
   * Device with failed communication time stamp younger than 1 hour and 1
   * connection failure should be excluded from the resulting list ( threshold
   * is 2^1*30=60 ).
   */
  @Test
  public void
      applyExponentialBackOffToListOfDevicesWithCommunicationTimestampYoungerThan1HourAnd1ConnectionFailure() {
    final List<Device> devices = new ArrayList<>();
    devices.add(
        this.createDevice(
            "device-05",
            Date.from(ZonedDateTime.now(ZoneId.of("UTC")).minusMinutes(59).toInstant()),
            1));

    final List<Device> filteredList = this.task.filterByExponentialBackOff(devices);

    assertThat(filteredList.size()).isEqualTo(0);
  }

  /*
   * Device with failed communication time stamp older than 1 hour and 2
   * connection failure should be excluded from the resulting list ( threshold
   * is 2^2*30=120 ).
   */
  @Test
  public void
      applyExponentialBackOffToListOfDevicesWithCommunicationTimestampYoungerThan1HourAnd2ConnectionFailures() {
    final List<Device> devices = new ArrayList<>();
    devices.add(
        this.createDevice(
            "device-06",
            Date.from(ZonedDateTime.now(ZoneId.of("UTC")).minusHours(1).toInstant()),
            2));

    final List<Device> filteredList = this.task.filterByExponentialBackOff(devices);

    assertThat(filteredList.size()).isEqualTo(0);
  }

  /*
   * Device with failed communication time stamp older than 2 hours and 1
   * connection failure should be included in the resulting list ( threshold
   * is 2^1*30=60 ).
   */
  @Test
  public void
      applyExponentialBackOffToListOfDevicesWithCommunicationTimestampYoungerThan2HoursAnd1ConnectionFailure() {
    final List<Device> devices = new ArrayList<>();
    devices.add(
        this.createDevice(
            "device-07",
            Date.from(ZonedDateTime.now(ZoneId.of("UTC")).minusHours(2).toInstant()),
            1));

    final List<Device> filteredList = this.task.filterByExponentialBackOff(devices);

    assertThat(filteredList.size()).isEqualTo(1);
  }

  /*
   * Device with failed communication time stamp older than 2 hours and 2
   * connection failure should be excluded from the resulting list ( threshold
   * is 2^2*30=120 ).
   */
  @Test
  public void
      applyExponentialBackOffToListOfDevicesWithCommunicationTimestampYoungerThan2HoursAnd2ConnectionFailures() {
    final List<Device> devices = new ArrayList<>();
    devices.add(
        this.createDevice(
            "device-08",
            Date.from(ZonedDateTime.now(ZoneId.of("UTC")).minusHours(2).plusMinutes(1).toInstant()),
            2));

    final List<Device> filteredList = this.task.filterByExponentialBackOff(devices);

    assertThat(filteredList.size()).isEqualTo(0);
  }

  /*
   * Device with failed communication time stamp older than 2 hours and 3
   * connection failures should be excluded from the resulting list (
   * threshold is 2^3*30=240 ).
   */
  @Test
  public void
      applyExponentialBackOffToListOfDevicesWithCommunicationTimestampYoungerThan2HoursAnd3ConnectionFailures() {
    final List<Device> devices = new ArrayList<>();
    devices.add(
        this.createDevice(
            "device-09",
            Date.from(ZonedDateTime.now(ZoneId.of("UTC")).minusHours(2).toInstant()),
            3));

    final List<Device> filteredList = this.task.filterByExponentialBackOff(devices);

    assertThat(filteredList.size()).isEqualTo(0);
  }

  private Device createDevice(
      final String deviceId,
      final Date lastFailedConnectionTimestamp,
      final int failedConnectionCount) {
    final Device device = new Device(deviceId);
    device.setActivated(true);
    device.setDeviceLifecycleStatus(DeviceLifecycleStatus.IN_USE);
    device.setLastFailedConnectionTimestamp(lastFailedConnectionTimestamp);
    device.setFailedConnectionCount(failedConnectionCount);

    return device;
  }
}
