// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupAlarmDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class GetPushSetupAlarmCommandExecutor
    extends GetPushSetupCommandExecutor<Void, PushSetupAlarmDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetPushSetupAlarmCommandExecutor.class);
  private static final ObisCode OBIS_CODE = new ObisCode("0.1.25.9.0.255");

  private static final AttributeAddress[] ATTRIBUTE_ADDRESSES = {
    new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_PUSH_OBJECT_LIST),
    new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_SEND_DESTINATION_AND_METHOD),
    new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_COMMUNICATION_WINDOW),
    new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_RANDOMISATION_START_INTERVAL),
    new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_NUMBER_OF_RETRIES),
    new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_REPETITION_DELAY)
  };

  @Autowired private DlmsHelper dlmsHelper;

  @Override
  public PushSetupAlarmDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Void useless,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    conn.getDlmsMessageListener()
        .setDescription(
            "GetPushSetupAlarm, retrieve attributes: "
                + JdlmsObjectToStringUtil.describeAttributes(ATTRIBUTE_ADDRESSES));

    LOGGER.info("Retrieving Push Setup Alarm");

    final List<GetResult> getResultList =
        this.dlmsHelper.getWithList(conn, device, ATTRIBUTE_ADDRESSES);

    GetPushSetupCommandExecutor.checkResultList(getResultList, ATTRIBUTE_ADDRESSES);

    final PushSetupAlarmDto.Builder pushSetupAlarmBuilder = new PushSetupAlarmDto.Builder();
    pushSetupAlarmBuilder.withLogicalName(new CosemObisCodeDto(OBIS_CODE.bytes()));

    pushSetupAlarmBuilder.withPushObjectList(
        this.dlmsHelper.readListOfObjectDefinition(
            getResultList.get(INDEX_PUSH_OBJECT_LIST), "Push Object List"));

    pushSetupAlarmBuilder.withSendDestinationAndMethod(
        this.dlmsHelper.readSendDestinationAndMethod(
            getResultList.get(INDEX_SEND_DESTINATION_AND_METHOD), "Send Destination And Method"));

    pushSetupAlarmBuilder.withCommunicationWindow(
        this.dlmsHelper.readListOfWindowElement(
            getResultList.get(INDEX_COMMUNICATION_WINDOW), "Communication Window"));

    pushSetupAlarmBuilder.withRandomisationStartInterval(
        this.dlmsHelper
            .readLongNotNull(
                getResultList.get(INDEX_RANDOMISATION_START_INTERVAL),
                "Randomisation Start Interval")
            .intValue());

    pushSetupAlarmBuilder.withNumberOfRetries(
        this.dlmsHelper
            .readLongNotNull(getResultList.get(INDEX_NUMBER_OF_RETRIES), "Number of Retries")
            .intValue());

    pushSetupAlarmBuilder.withRepetitionDelay(
        this.dlmsHelper
            .readLongNotNull(getResultList.get(INDEX_REPETITION_DELAY), "Repetition Delay")
            .intValue());

    return pushSetupAlarmBuilder.build();
  }
}
