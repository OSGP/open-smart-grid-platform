// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.pushsetup;

import static org.opensmartgridplatform.dlms.interfaceclass.attribute.PushSetupAttribute.COMMUNICATION_WINDOW;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.PushSetupAttribute.NUMBER_OF_RETRIES;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.PushSetupAttribute.PUSH_OBJECT_LIST;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.PushSetupAttribute.RANDOMISATION_START_INTERVAL;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.PushSetupAttribute.REPETITION_DELAY;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.PushSetupAttribute.SEND_DESTINATION_AND_METHOD;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.NotSupportedByProtocolException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.PushSetupAttribute;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemObisCodeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupSmsDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Component;

@Slf4j
@Component()
public class GetPushSetupSmsCommandExecutor
    extends GetPushSetupCommandExecutor<Void, PushSetupSmsDto> {

  private final DlmsHelper dlmsHelper;
  private final ObjectConfigServiceHelper objectConfigServiceHelper;

  PushSetupAttribute[] attributes = {
    PUSH_OBJECT_LIST,
    SEND_DESTINATION_AND_METHOD,
    COMMUNICATION_WINDOW,
    RANDOMISATION_START_INTERVAL,
    NUMBER_OF_RETRIES,
    REPETITION_DELAY
  };

  public GetPushSetupSmsCommandExecutor(
      final DlmsHelper dlmsHelper, final ObjectConfigServiceHelper objectConfigServiceHelper) {
    this.dlmsHelper = dlmsHelper;
    this.objectConfigServiceHelper = objectConfigServiceHelper;
  }

  @Override
  public PushSetupSmsDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Void useless,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    final AttributeAddress[] attributeAddresses =
        this.getAttributeAddresses(Protocol.forDevice(device));
    conn.getDlmsMessageListener()
        .setDescription(
            "GetPushSetupSms, retrieve attributes: "
                + JdlmsObjectToStringUtil.describeAttributes(attributeAddresses));

    log.info("Retrieving Push Setup SMS");

    final List<GetResult> getResultList =
        this.dlmsHelper.getWithList(conn, device, attributeAddresses);

    GetPushSetupCommandExecutor.checkResultList(getResultList, attributeAddresses);

    final PushSetupSmsDto.Builder pushSetupSmsBuilder = new PushSetupSmsDto.Builder();
    pushSetupSmsBuilder.withLogicalName(
        new CosemObisCodeDto(attributeAddresses[0].getInstanceId().bytes()));

    pushSetupSmsBuilder.withPushObjectList(
        this.dlmsHelper.readListOfObjectDefinition(
            getResultList.get(this.idx(PUSH_OBJECT_LIST)), "Push Object List"));

    pushSetupSmsBuilder.withSendDestinationAndMethod(
        this.dlmsHelper.readSendDestinationAndMethod(
            getResultList.get(this.idx(SEND_DESTINATION_AND_METHOD)),
            "Send Destination And Method"));

    pushSetupSmsBuilder.withCommunicationWindow(
        this.dlmsHelper.readListOfWindowElement(
            getResultList.get(this.idx(COMMUNICATION_WINDOW)), "Communication Window"));

    pushSetupSmsBuilder.withRandomisationStartInterval(
        this.dlmsHelper
            .readLongNotNull(
                getResultList.get(this.idx(RANDOMISATION_START_INTERVAL)),
                "Randomisation Start Interval")
            .intValue());

    pushSetupSmsBuilder.withNumberOfRetries(
        this.dlmsHelper
            .readLongNotNull(getResultList.get(this.idx(NUMBER_OF_RETRIES)), "Number of Retries")
            .intValue());

    pushSetupSmsBuilder.withRepetitionDelay(
        this.dlmsHelper
            .readLongNotNull(getResultList.get(this.idx(REPETITION_DELAY)), "Repetition Delay")
            .intValue());

    return pushSetupSmsBuilder.build();
  }

  private AttributeAddress[] getAttributeAddresses(final Protocol protocol)
      throws NotSupportedByProtocolException {
    final AttributeAddress[] attributeAddresses = new AttributeAddress[this.attributes.length];
    for (int i = 0; i < this.attributes.length; i++) {
      attributeAddresses[i] = this.getAttributeAddress(protocol, this.attributes[i].attributeId());
    }
    return attributeAddresses;
  }

  private int idx(final PushSetupAttribute pushSetupAttribute) {
    return ArrayUtils.indexOf(this.attributes, pushSetupAttribute);
  }

  private AttributeAddress getAttributeAddress(final Protocol protocol, final int attributeId)
      throws NotSupportedByProtocolException {
    final Optional<AttributeAddress> optionalAttributeAddress =
        this.objectConfigServiceHelper.findOptionalAttributeAddress(
            protocol, DlmsObjectType.PUSH_SETUP_SMS, null, attributeId);
    return optionalAttributeAddress.orElseThrow(
        () ->
            new NotSupportedByProtocolException(
                String.format(
                    "No address found for %s in protocol %s %s",
                    DlmsObjectType.PUSH_SETUP_SMS.name(),
                    protocol.getName(),
                    protocol.getVersion())));
  }
}
