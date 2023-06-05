// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.core.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.opensmartgridplatform.logging.domain.repositories.DeviceLogItemSlicingRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

public class TransactionalDeviceLogItemServiceTest {

  private final Date now = DateTime.now().toDate();

  @InjectMocks private TransactionalDeviceLogItemService transactionalDeviceLogItemService;

  @Mock private DeviceLogItemSlicingRepository deviceLogItemSlicingRepository;

  @BeforeEach
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void serviceReturnsOneDeviceLogItem() {
    final Slice<DeviceLogItem> mockSlice = this.mockSliceOfDeviceLogItems(1);
    final PageRequest pageRequest = PageRequest.of(0, 10, Sort.Direction.ASC, "id");
    Mockito.when(
            this.deviceLogItemSlicingRepository.findByModificationTimeBefore(this.now, pageRequest))
        .thenReturn(mockSlice);

    final List<DeviceLogItem> deviceLogItems =
        this.transactionalDeviceLogItemService.findDeviceLogItemsBeforeDate(this.now, 10);
    assertThat(deviceLogItems.size()).isEqualTo(1);
  }

  @Test
  public void serviceReturnsTenDeviceLogItems() {
    final Slice<DeviceLogItem> mockSlice = this.mockSliceOfDeviceLogItems(10);
    final PageRequest pageRequest = PageRequest.of(0, 10, Sort.Direction.ASC, "id");
    Mockito.when(
            this.deviceLogItemSlicingRepository.findByModificationTimeBefore(this.now, pageRequest))
        .thenReturn(mockSlice);

    final List<DeviceLogItem> deviceLogItems =
        this.transactionalDeviceLogItemService.findDeviceLogItemsBeforeDate(this.now, 10);
    assertThat(deviceLogItems.size()).isEqualTo(10);
  }

  @Test
  public void serviceDeletesDeviceLogItem() {
    final List<DeviceLogItem> deviceLogItems = this.mockSliceOfDeviceLogItems(1).getContent();

    try {
      this.transactionalDeviceLogItemService.deleteDeviceLogItems(deviceLogItems);
    } catch (final Exception e) {
      fail("Unexpected exception! " + e.getMessage());
    }
  }

  private Slice<DeviceLogItem> mockSliceOfDeviceLogItems(final int numberOfDeviceLogItems) {

    final List<DeviceLogItem> deviceLogItems = new ArrayList<>();
    for (int i = 0; i < numberOfDeviceLogItems; i++) {
      final DeviceLogItem.Builder builder =
          new DeviceLogItem.Builder()
              .withIncoming(false)
              .withDeviceUid("deviceUID")
              .withEncodedMessage("0x48 0x49")
              .withDecodedMessage("H I")
              .withDeviceIdentification("test")
              .withOrganisationIdentification("organisation")
              .withValid(true)
              .withPayloadMessageSerializedSize(2);
      final DeviceLogItem deviceLogItem = new DeviceLogItem(builder);
      deviceLogItems.add(deviceLogItem);
    }

    return new SliceImpl<>(deviceLogItems);
  }
}
