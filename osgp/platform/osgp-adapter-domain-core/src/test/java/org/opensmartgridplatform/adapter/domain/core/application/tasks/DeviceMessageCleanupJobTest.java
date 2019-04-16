/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.application.tasks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opensmartgridplatform.adapter.domain.core.application.services.TransactionalDeviceLogItemService;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.quartz.JobExecutionException;
import org.springframework.test.util.ReflectionTestUtils;

public class DeviceMessageCleanupJobTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @InjectMocks
    private DeviceMessageCleanupJob deviceMessageCleanupJob;

    @Mock
    private TransactionalDeviceLogItemService transactionalDeviceLogItemService;

    private final String filePrefix = "junit-mocked-csv-file-";

    @Before
    public void initMocksAndSetProperties() {
        MockitoAnnotations.initMocks(this);

        ReflectionTestUtils.setField(this.deviceMessageCleanupJob, "deviceMessageRetentionPeriodInMonths", 1);
        ReflectionTestUtils.setField(this.deviceMessageCleanupJob, "csvFileLocation",
                this.folder.getRoot().getAbsolutePath());
        ReflectionTestUtils.setField(this.deviceMessageCleanupJob, "csvFilePrefix", this.filePrefix);
        ReflectionTestUtils.setField(this.deviceMessageCleanupJob, "csvFileCompressionEnabled", true);
        ReflectionTestUtils.setField(this.deviceMessageCleanupJob, "deviceMessagePageSize", 10);
    }

    @Test
    public void test() throws JobExecutionException {
        // Arrange
        final DeviceLogItem.Builder builder = new DeviceLogItem.Builder().withIncoming(false).withDeviceUid("deviceUID")
                .withEncodedMessage("0x4F 0x53 0x4C 0x50 ").withDecodedMessage("O S L P")
                .withDeviceIdentification("test").withOrganisationIdentification("organisation").withValid(true)
                .withPayloadMessageSerializedSize(4);
        final DeviceLogItem deviceLogItem = new DeviceLogItem(builder);
        final List<DeviceLogItem> deviceLogItems = Arrays.asList(deviceLogItem);
        Mockito.when(this.transactionalDeviceLogItemService.findDeviceLogItemsBeforeDate(any(Date.class),
                any(Integer.class))).thenReturn(deviceLogItems);

        // Act
        this.deviceMessageCleanupJob.execute(null);

        // Assert

        // Example path:
        // /tmp/junit7318456469288690301/junit-mocked-csv-file-20190416-112237.csv.zip
        final String path = this.folder.getRoot().getAbsolutePath();
        final File folder = new File(path);
        final File[] files = folder.listFiles();
        File outputFile = null;
        for (final File file : files) {
            if (file.getName().startsWith(this.filePrefix)) {
                outputFile = file;
                break;
            }
        }
        assertThat(outputFile.exists()).isTrue();
        assertThat(outputFile).hasExtension("zip");
        assertThat(outputFile.length()).isNotZero();
    }
}
