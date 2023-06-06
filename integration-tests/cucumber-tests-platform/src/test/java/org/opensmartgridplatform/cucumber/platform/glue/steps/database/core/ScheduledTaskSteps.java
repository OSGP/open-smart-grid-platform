// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.glue.steps.database.core;

import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_CORRELATION_UID;
import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION;
import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION;
import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_SCHEDULED_TIME;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_CORRELATION_UID;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_DEVICE_IDENTIFICATION;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_SCHEDULED_TIME;

import io.cucumber.java.en.Given;
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
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;

public class ScheduledTaskSteps extends BaseDeviceSteps {

  private static final Map<String, Function<Map<String, String>, ScheduledTask>>
      SCHEDULED_TASK_CREATOR_MAP = new HashMap<>();

  static {
    SCHEDULED_TASK_CREATOR_MAP.put("UPDATE_FIRMWARE", m -> createUpdateFirmwareScheduledTask(m));
  }

  @Autowired private ScheduledTaskRepository scheduledTaskRepository;

  private static ScheduledTask createUpdateFirmwareScheduledTask(
      final Map<String, String> settings) {
    final String deviceIdentification =
        ReadSettingsHelper.getString(
            settings, KEY_DEVICE_IDENTIFICATION, DEFAULT_DEVICE_IDENTIFICATION);
    final String organisationIdentification =
        ReadSettingsHelper.getString(
            settings, KEY_ORGANIZATION_IDENTIFICATION, DEFAULT_ORGANIZATION_IDENTIFICATION);
    final String correlationUid =
        ReadSettingsHelper.getString(settings, KEY_CORRELATION_UID, DEFAULT_CORRELATION_UID);
    final String messageType = MessageType.UPDATE_FIRMWARE.toString();
    final int messagePriority = 4;
    final Long scheduleTime =
        DateTimeHelper.getDateTime(
                ReadSettingsHelper.getString(settings, KEY_SCHEDULED_TIME, DEFAULT_SCHEDULED_TIME))
            .getMillis();
    final MessageMetadata messageMetadata =
        new MessageMetadata.Builder()
            .withDeviceIdentification(deviceIdentification)
            .withOrganisationIdentification(organisationIdentification)
            .withCorrelationUid(correlationUid)
            .withMessageType(messageType)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .build();
    final FirmwareModuleData firmwareModuleData =
        new FirmwareModuleData(null, "FW-01", null, null, null, null, null);
    final String firmwareUrl = "firmware-url";
    final Serializable messageData =
        new FirmwareUpdateMessageDataContainer(firmwareModuleData, firmwareUrl);
    return new ScheduledTask(
        messageMetadata, "CORE", "1.0", messageData, new Timestamp(scheduleTime));
  }

  @Given("a scheduled {string} task")
  public void givenAScheduledTask(final String messageType, final Map<String, String> settings) {
    final ScheduledTask scheduledTask = SCHEDULED_TASK_CREATOR_MAP.get(messageType).apply(settings);
    this.scheduledTaskRepository.save(scheduledTask);
  }
}
