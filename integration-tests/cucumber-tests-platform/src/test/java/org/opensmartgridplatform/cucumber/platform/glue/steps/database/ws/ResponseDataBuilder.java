// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.glue.steps.database.ws;

import java.io.Serializable;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;

public class ResponseDataBuilder {

  private String organisationIdentification = "test-org";
  private String deviceIdentification = "test-rtu";
  private String correlationUid = "test-org|||test-rtu|||20170101000000000";
  private String messageType = "GET_DATA";
  private Serializable messageData = null;
  private ResponseMessageResultType resultType = ResponseMessageResultType.OK;
  private Short numberOfNotificationsSent = 0;

  public ResponseData build() {
    final CorrelationIds ids =
        new CorrelationIds(
            this.organisationIdentification, this.deviceIdentification, this.correlationUid);
    return new ResponseData(
        ids, this.messageType, this.resultType, this.messageData, this.numberOfNotificationsSent);
  }

  public ResponseDataBuilder fromSettings(final Map<String, String> settings) {
    if (settings.containsKey(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION)) {
      this.withOrganisationIdentification(
          settings.get(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION));
    }
    if (settings.containsKey(PlatformKeys.KEY_DEVICE_IDENTIFICATION)) {
      this.withDeviceIdentification(settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
    }
    if (settings.containsKey(PlatformKeys.KEY_CORRELATION_UID)) {
      this.withCorrelationUid(settings.get(PlatformKeys.KEY_CORRELATION_UID));
    }
    if (settings.containsKey(PlatformKeys.KEY_MESSAGE_TYPE)) {
      this.withMessageType(settings.get(PlatformKeys.KEY_MESSAGE_TYPE));
    }
    if (settings.containsKey(PlatformKeys.KEY_MESSAGE_DATA)) {
      this.withMessageData(settings.get(PlatformKeys.KEY_MESSAGE_DATA));
    }
    if (settings.containsKey(PlatformKeys.KEY_RESULT_TYPE)) {
      this.withResultType(
          ResponseMessageResultType.valueOf(settings.get(PlatformKeys.KEY_RESULT_TYPE)));
    }
    if (settings.containsKey(PlatformKeys.KEY_NUMBER_OF_NOTIFICATIONS_SENT)) {
      this.withNumberOfNotificationsSent(
          Short.parseShort(settings.get(PlatformKeys.KEY_NUMBER_OF_NOTIFICATIONS_SENT)));
    }
    return this;
  }

  public ResponseDataBuilder withOrganisationIdentification(
      final String organisationIdentification) {
    this.organisationIdentification = organisationIdentification;
    return this;
  }

  public ResponseDataBuilder withDeviceIdentification(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
    return this;
  }

  public ResponseDataBuilder withCorrelationUid(final String correlationUid) {
    this.correlationUid = correlationUid;
    return this;
  }

  public ResponseDataBuilder withMessageType(final String messageType) {
    this.messageType = messageType;
    return this;
  }

  public ResponseDataBuilder withMessageData(final Serializable messageData) {
    this.messageData = messageData;
    return this;
  }

  public ResponseDataBuilder withResultType(final ResponseMessageResultType resultType) {
    this.resultType = resultType;
    return this;
  }

  public ResponseDataBuilder withNumberOfNotificationsSent(final Short numberOfNotificationsSent) {
    this.numberOfNotificationsSent = numberOfNotificationsSent;
    return this;
  }
}
