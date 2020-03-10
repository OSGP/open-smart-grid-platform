/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.glue.steps.database.core;

import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_CORRELATION_UID;
import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION;
import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION;
import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_SCHEDULED_TIME;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_CORRELATION_UID;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_DEVICE_IDENTIFICATION;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_SCHEDULED_TIME;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.opensmartgridplatform.cucumber.core.DateTimeHelper;
import org.opensmartgridplatform.cucumber.core.ReadSettingsHelper;
import org.opensmartgridplatform.domain.core.entities.ScheduledTask;
import org.opensmartgridplatform.domain.core.repositories.ScheduledTaskRepository;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleData;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareUpdateMessageDataContainer;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Given;

public class ScheduledTaskSteps extends BaseDeviceSteps {

    private static final Map<String, Function<Map<String, String>, ScheduledTask>> SCHEDULED_TASK_CREATOR_MAP = new HashMap<>();
    static {
        SCHEDULED_TASK_CREATOR_MAP.put("UPDATE_FIRMWARE", m -> createUpdateFirmwareScheduledTask(m));
    }

    @Autowired
    private ScheduledTaskRepository scheduledTaskRepository;

    @Given("a scheduled {string} task")
    public void givenAScheduledTask(final String messageType, final Map<String, String> settings) {
        final ScheduledTask scheduledTask = SCHEDULED_TASK_CREATOR_MAP.get(messageType).apply(settings);
        this.scheduledTaskRepository.save(scheduledTask);
    }

    private static ScheduledTask createUpdateFirmwareScheduledTask(final Map<String, String> settings) {
        final String deviceIdentification = ReadSettingsHelper.getString(settings, KEY_DEVICE_IDENTIFICATION,
                DEFAULT_DEVICE_IDENTIFICATION);
        final String organisationIdentification = ReadSettingsHelper.getString(settings,
                KEY_ORGANIZATION_IDENTIFICATION, DEFAULT_ORGANIZATION_IDENTIFICATION);
        final String correlationUid = ReadSettingsHelper.getString(settings, KEY_CORRELATION_UID,
                DEFAULT_CORRELATION_UID);
        final String messageType = MessageType.UPDATE_FIRMWARE.toString();
        final int messagePriority = 4;
        final Long scheduleTime = DateTimeHelper
                .getDateTime(ReadSettingsHelper.getString(settings, KEY_SCHEDULED_TIME, DEFAULT_SCHEDULED_TIME))
                .getMillis();
        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, messageType, messagePriority, scheduleTime);
        final FirmwareModuleData firmwareModuleData = new FirmwareModuleData(null, "FW-01", null, null, null, null);
        final String firmwareUrl = "firmware-url";
        final Serializable messageData = new FirmwareUpdateMessageDataContainer(firmwareModuleData, firmwareUrl);
        return new ScheduledTask(deviceMessageMetadata, "CORE", "1.0", messageData, new Timestamp(scheduleTime));
    }
}
