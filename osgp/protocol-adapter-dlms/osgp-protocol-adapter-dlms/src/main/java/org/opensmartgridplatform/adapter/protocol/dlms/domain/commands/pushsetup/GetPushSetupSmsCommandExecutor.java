//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.pushsetup;

import java.util.List;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemObisCodeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupSmsDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class GetPushSetupSmsCommandExecutor
    extends GetPushSetupCommandExecutor<Void, PushSetupSmsDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetPushSetupSmsCommandExecutor.class);
  private static final ObisCode OBIS_CODE = new ObisCode("0.2.25.9.0.255");

  private static final AttributeAddress[] ATTRIBUTE_ADDRESSES = new AttributeAddress[6];

  static {
    ATTRIBUTE_ADDRESSES[0] =
        new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_PUSH_OBJECT_LIST);
    ATTRIBUTE_ADDRESSES[1] =
        new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_SEND_DESTINATION_AND_METHOD);
    ATTRIBUTE_ADDRESSES[2] =
        new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_COMMUNICATION_WINDOW);
    ATTRIBUTE_ADDRESSES[3] =
        new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_RANDOMISATION_START_INTERVAL);
    ATTRIBUTE_ADDRESSES[4] =
        new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_NUMBER_OF_RETRIES);
    ATTRIBUTE_ADDRESSES[5] =
        new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_REPETITION_DELAY);
  }

  @Autowired private DlmsHelper dlmsHelper;

  @Override
  public PushSetupSmsDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Void useless,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    conn.getDlmsMessageListener()
        .setDescription(
            "GetPushSetupSms, retrieve attributes: "
                + JdlmsObjectToStringUtil.describeAttributes(ATTRIBUTE_ADDRESSES));

    LOGGER.info("Retrieving Push Setup SMS");

    final List<GetResult> getResultList =
        this.dlmsHelper.getWithList(conn, device, ATTRIBUTE_ADDRESSES);

    GetPushSetupCommandExecutor.checkResultList(getResultList, ATTRIBUTE_ADDRESSES);

    final PushSetupSmsDto.Builder pushSetupSmsBuilder = new PushSetupSmsDto.Builder();
    pushSetupSmsBuilder.withLogicalName(new CosemObisCodeDto(OBIS_CODE.bytes()));

    pushSetupSmsBuilder.withPushObjectList(
        this.dlmsHelper.readListOfObjectDefinition(
            getResultList.get(INDEX_PUSH_OBJECT_LIST), "Push Object List"));

    pushSetupSmsBuilder.withSendDestinationAndMethod(
        this.dlmsHelper.readSendDestinationAndMethod(
            getResultList.get(INDEX_SEND_DESTINATION_AND_METHOD), "Send Destination And Method"));

    pushSetupSmsBuilder.withCommunicationWindow(
        this.dlmsHelper.readListOfWindowElement(
            getResultList.get(INDEX_COMMUNICATION_WINDOW), "Communication Window"));

    pushSetupSmsBuilder.withRandomisationStartInterval(
        this.dlmsHelper
            .readLongNotNull(
                getResultList.get(INDEX_RANDOMISATION_START_INTERVAL),
                "Randomisation Start Interval")
            .intValue());

    pushSetupSmsBuilder.withNumberOfRetries(
        this.dlmsHelper
            .readLongNotNull(getResultList.get(INDEX_NUMBER_OF_RETRIES), "Number of Retries")
            .intValue());

    pushSetupSmsBuilder.withRepetitionDelay(
        this.dlmsHelper
            .readLongNotNull(getResultList.get(INDEX_REPETITION_DELAY), "Repetition Delay")
            .intValue());

    return pushSetupSmsBuilder.build();
  }
}
