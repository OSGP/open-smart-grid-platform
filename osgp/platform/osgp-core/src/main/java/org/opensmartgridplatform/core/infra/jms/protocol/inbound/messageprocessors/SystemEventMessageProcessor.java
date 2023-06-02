//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.core.infra.jms.protocol.inbound.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.core.domain.model.domain.DomainRequestService;
import org.opensmartgridplatform.core.infra.jms.protocol.inbound.AbstractProtocolRequestMessageProcessor;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.domain.core.repositories.DomainInfoRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SystemEventDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("dlmsSystemEventMessageProcessor")
@Transactional(value = "transactionManager")
public class SystemEventMessageProcessor extends AbstractProtocolRequestMessageProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(SystemEventMessageProcessor.class);

  @Autowired private DomainRequestService domainRequestService;

  @Autowired private DomainInfoRepository domainInfoRepository;

  protected SystemEventMessageProcessor() {
    super(MessageType.SYSTEM_EVENT);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {

    final MessageMetadata metadata = MessageMetadata.fromMessage(message);

    LOGGER.info(
        "Received message of messageType: {} organisationIdentification: {} deviceIdentification: {}",
        this.messageType,
        metadata.getOrganisationIdentification(),
        metadata.getDeviceIdentification());

    final RequestMessage requestMessage = (RequestMessage) message.getObject();
    final Object dataObject = requestMessage.getRequest();

    final SystemEventDto systemEvent = (SystemEventDto) dataObject;

    final RequestMessage request =
        new RequestMessage(
            requestMessage.getCorrelationUid(),
            metadata.getOrganisationIdentification(),
            requestMessage.getDeviceIdentification(),
            requestMessage.getIpAddress(),
            requestMessage.getBaseTransceiverStationId(),
            requestMessage.getCellId(),
            systemEvent);

    final DomainInfo domainInfo =
        this.domainInfoRepository.findByDomainAndDomainVersion(
            metadata.getDomain(), metadata.getDomainVersion());

    this.domainRequestService.send(request, DeviceFunction.SYSTEM_EVENT.name(), domainInfo);
  }
}
