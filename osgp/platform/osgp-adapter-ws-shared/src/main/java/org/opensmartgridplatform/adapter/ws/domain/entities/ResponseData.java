// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.io.Serializable;
import org.hibernate.annotations.Type;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;

@Entity
@Table(name = "response_data")
public class ResponseData extends AbstractEntity {

  /** Serial Version UID. */
  private static final long serialVersionUID = 493031146792643786L;

  @Column(length = 255)
  private String organisationIdentification;

  @Column(length = 255)
  private String messageType;

  @Column(length = 255)
  private String deviceIdentification;

  @Column(length = 255)
  private String correlationUid;

  @Column private Short numberOfNotificationsSent;

  @Enumerated(EnumType.STRING)
  private ResponseMessageResultType resultType;

  @Type(type = "java.io.Serializable")
  private Serializable messageData;

  @SuppressWarnings("unused")
  private ResponseData() {}

  public ResponseData(
      final CorrelationIds ids,
      final String messageType,
      final ResponseMessageResultType resultType,
      final Serializable messageData,
      final Short numberOfNotificationsSent) {
    this.organisationIdentification = ids.getOrganisationIdentification();
    this.messageType = messageType;
    this.deviceIdentification = ids.getDeviceIdentification();
    this.correlationUid = ids.getCorrelationUid();
    this.resultType = resultType;
    this.messageData = messageData;
    this.numberOfNotificationsSent = numberOfNotificationsSent;
  }

  public String getCorrelationUid() {
    return this.correlationUid;
  }

  public String getOrganisationIdentification() {
    return this.organisationIdentification;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public String getMessageType() {
    return this.messageType;
  }

  public Serializable getMessageData() {
    return this.messageData;
  }

  public ResponseMessageResultType getResultType() {
    return this.resultType;
  }

  public Short getNumberOfNotificationsSent() {
    return this.numberOfNotificationsSent;
  }

  public void setNumberOfNotificationsSent(final Short numberOfNotificationsSent) {
    this.numberOfNotificationsSent = numberOfNotificationsSent;
  }
}
