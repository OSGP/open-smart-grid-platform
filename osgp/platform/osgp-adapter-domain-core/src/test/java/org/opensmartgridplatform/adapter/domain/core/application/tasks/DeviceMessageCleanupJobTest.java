// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.core.application.tasks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opensmartgridplatform.adapter.domain.core.application.services.TransactionalDeviceLogItemService;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.opensmartgridplatform.shared.utils.FileUtils;
import org.springframework.test.util.ReflectionTestUtils;

public class DeviceMessageCleanupJobTest {

  @TempDir Path folder;

  @InjectMocks private DeviceMessageCleanupJob deviceMessageCleanupJob;

  @Mock private TransactionalDeviceLogItemService transactionalDeviceLogItemService;

  private final String filePrefix = "junit-mocked-csv-file-";
  private static final String JUST_A_FILE = "file.csv";

  @BeforeEach
  public void initMocksAndSetProperties() {
    MockitoAnnotations.initMocks(this);

    ReflectionTestUtils.setField(this.deviceMessageCleanupJob, "deviceMessageCleanupEnabled", true);
    ReflectionTestUtils.setField(
        this.deviceMessageCleanupJob, "deviceMessageRetentionPeriodInMonths", 1);
    ReflectionTestUtils.setField(
        this.deviceMessageCleanupJob,
        "csvFileLocation",
        this.folder.resolve(JUST_A_FILE).getParent().toString());
    ReflectionTestUtils.setField(this.deviceMessageCleanupJob, "csvFilePrefix", this.filePrefix);
    ReflectionTestUtils.setField(this.deviceMessageCleanupJob, "csvFileCompressionEnabled", true);
    ReflectionTestUtils.setField(this.deviceMessageCleanupJob, "deviceMessagePageSize", 10);
  }

  @Test
  public void test() throws IOException {
    // Arrange
    final List<DeviceLogItem> deviceLogItems = this.createDeviceLogItems();
    Mockito.when(
            this.transactionalDeviceLogItemService.findDeviceLogItemsBeforeDate(
                any(Date.class), any(Integer.class)))
        .thenReturn(deviceLogItems);

    // Act
    this.deviceMessageCleanupJob.execute(null);

    // Assert

    // Example path:
    // /tmp/junit7318456469288690301/junit-mocked-csv-file-20190416-112237.csv.zip
    final String path = this.folder.resolve(JUST_A_FILE).getParent().toString();

    final File zipFile = FileUtils.findFileInFolderUsingFilePrefix(path, this.filePrefix);
    assertThat(zipFile.exists()).isTrue();
    assertThat(zipFile).hasExtension("zip");
    assertThat(zipFile.length()).isNotZero();

    ZipFileReader.extractZipFile(zipFile.getAbsolutePath(), path);
    zipFile.delete();

    final File csvFile = FileUtils.findFileInFolderUsingFilePrefix(path, this.filePrefix);
    assertThat(csvFile.exists()).isTrue();
    assertThat(csvFile).hasExtension("csv");
    assertThat(csvFile.length()).isNotZero();

    final long numberOfLines = FileUtils.countNumberOfLinesInFile(csvFile);
    assertThat(numberOfLines).isEqualTo(2l);
  }

  private List<DeviceLogItem> createDeviceLogItems() {
    final DeviceLogItem.Builder builder =
        new DeviceLogItem.Builder()
            .withIncoming(false)
            .withDeviceUid("deviceUID")
            .withEncodedMessage("0x4F 0x53 0x4C 0x50 ")
            .withDecodedMessage("O S L P")
            .withDeviceIdentification("test")
            .withOrganisationIdentification("organisation")
            .withValid(true)
            .withPayloadMessageSerializedSize(4);
    final DeviceLogItem deviceLogItem = new DeviceLogItem(builder);
    return Arrays.asList(deviceLogItem);
  }
}
