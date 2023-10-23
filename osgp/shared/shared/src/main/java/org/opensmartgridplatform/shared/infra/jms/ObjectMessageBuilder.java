// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.infra.jms;

import java.io.Serializable;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;

public class ObjectMessageBuilder {

  private String correlationUid = "test-corr-uid";
  private String deviceIdentification = "test-dvc";
  private final String domain = "domain";
  private final String domainVersion = "domain-version";
  private String ipAddress = "localhost";
  private String organisationIdentification = "test-org";
  private String messageType;
  private Serializable object = null;
  private final int retryCount = 0;
  private final int messagePriority = 0;
  private final boolean bypassRetry = false;
  private final boolean scheduled = false;

  public ObjectMessageBuilder withCorrelationUid(final String correlationUid) {
    this.correlationUid = correlationUid;
    return this;
  }

  public ObjectMessageBuilder withDeviceIdentification(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
    return this;
  }

  public ObjectMessageBuilder withOrganisationIdentification(
      final String organisationIdentification) {
    this.organisationIdentification = organisationIdentification;
    return this;
  }

  public ObjectMessageBuilder withObject(final Serializable object) {
    this.object = object;
    return this;
  }

  public ObjectMessageBuilder withMessageType(final String messageType) {
    this.messageType = messageType;
    return this;
  }

  public ObjectMessageBuilder withIpAddress(final String ipAddress) {
    this.ipAddress = ipAddress;
    return this;
  }

  public ObjectMessage build() throws JMSException {
    final ObjectMessage message = new ActiveMQObjectMessage();
    message.setJMSCorrelationID(this.correlationUid);
    message.setStringProperty(Constants.DEVICE_IDENTIFICATION, this.deviceIdentification);
    message.setStringProperty(
        Constants.ORGANISATION_IDENTIFICATION, this.organisationIdentification);
    message.setJMSType(this.messageType);
    message.setJMSPriority(this.messagePriority);
    message.setIntProperty(Constants.RETRY_COUNT, this.retryCount);
    message.setBooleanProperty(Constants.BYPASS_RETRY, this.bypassRetry);
    message.setStringProperty(Constants.DOMAIN, this.domain);
    message.setStringProperty(Constants.DOMAIN_VERSION, this.domainVersion);
    message.setStringProperty(Constants.NETWORK_ADDRESS, this.ipAddress);
    message.setBooleanProperty(Constants.IS_SCHEDULED, this.scheduled);

    message.setObject(this.object);
    return message;
  }
}
