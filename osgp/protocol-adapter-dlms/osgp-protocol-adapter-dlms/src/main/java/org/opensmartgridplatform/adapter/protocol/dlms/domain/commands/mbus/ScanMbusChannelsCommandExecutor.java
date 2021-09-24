/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import java.util.ArrayList;
import java.util.List;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsClassVersion;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.MbusClientAttribute;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MbusChannelShortEquipmentIdentifierDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MbusShortEquipmentIdentifierDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ScanMbusChannelsRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ScanMbusChannelsResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScanMbusChannelsCommandExecutor
    extends AbstractCommandExecutor<Void, ScanMbusChannelsResponseDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ScanMbusChannelsCommandExecutor.class);

  private static final int CLASS_ID = InterfaceClass.MBUS_CLIENT.id();
  /**
   * IDs of the attributes of the M-Bus Client Setup that make up the Short ID.
   *
   * <p>The order of the IDs should match the way attributes are used in {@link
   * #makeAttributeAddressesShortIds()} and {@link #channelShortIdsFromGetResults(List, Protocol)}.
   */
  private static final int[] ATTRIBUTE_IDS_SHORT_ID =
      new int[] {
        MbusClientAttribute.IDENTIFICATION_NUMBER.attributeId(),
            MbusClientAttribute.MANUFACTURER_ID.attributeId(),
        MbusClientAttribute.VERSION.attributeId(), MbusClientAttribute.DEVICE_TYPE.attributeId()
      };

  private static final int OBIS_BYTE_A_MBUS_CLIENT_SETUP = 0;
  private static final int OBIS_BYTE_C_MBUS_CLIENT_SETUP = 24;
  private static final int OBIS_BYTE_D_MBUS_CLIENT_SETUP = 1;
  private static final int OBIS_BYTE_E_MBUS_CLIENT_SETUP = 0;
  private static final int OBIS_BYTE_F_MBUS_CLIENT_SETUP = 255;

  private static final int NUMBER_OF_CHANNELS = 4;
  private static final int NUMBER_OF_SHORT_ID_ATTRIBUTES_PER_CHANNEL =
      ATTRIBUTE_IDS_SHORT_ID.length;

  private static final AttributeAddress[] SHORT_ID_ATTRIBUTE_ADDRESSES =
      makeAttributeAddressesShortIds();

  @Autowired private DlmsHelper dlmsHelper;

  @Autowired private DlmsObjectConfigService dlmsObjectConfigService;

  public ScanMbusChannelsCommandExecutor() {
    super(ScanMbusChannelsRequestDataDto.class);
  }

  private static ObisCode getObisCodeMbusClientSetup(final int channel) {
    return new ObisCode(
        OBIS_BYTE_A_MBUS_CLIENT_SETUP,
        channel,
        OBIS_BYTE_C_MBUS_CLIENT_SETUP,
        OBIS_BYTE_D_MBUS_CLIENT_SETUP,
        OBIS_BYTE_E_MBUS_CLIENT_SETUP,
        OBIS_BYTE_F_MBUS_CLIENT_SETUP);
  }

  /**
   * @see #ATTRIBUTE_IDS_SHORT_ID
   * @see #channelShortIdsFromGetResults(List, Protocol)
   */
  private static AttributeAddress[] makeAttributeAddressesShortIds() {
    final AttributeAddress[] shortIdAddresses =
        new AttributeAddress[NUMBER_OF_CHANNELS * NUMBER_OF_SHORT_ID_ATTRIBUTES_PER_CHANNEL];
    int index = 0;
    for (int channel = 1; channel <= NUMBER_OF_CHANNELS; channel++) {
      final ObisCode obisCode = getObisCodeMbusClientSetup(channel);
      for (int i = 0; i < NUMBER_OF_SHORT_ID_ATTRIBUTES_PER_CHANNEL; i++) {
        shortIdAddresses[index++] =
            new AttributeAddress(CLASS_ID, obisCode, ATTRIBUTE_IDS_SHORT_ID[i]);
      }
    }
    return shortIdAddresses;
  }

  @Override
  public Void fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {
    /*
     * ScanMbusChannelsRequestDto does not contain any values to pass on,
     * and the ScanMbusChannelsCommandExecutor takes a Void as input that is
     * ignored.
     */
    return null;
  }

  @Override
  public ScanMbusChannelsResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Void mbusAttributesDto,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    LOGGER.debug("retrieving mbus info on e-meter");
    final List<GetResult> mbusShortIdResults =
        this.dlmsHelper.getAndCheck(
            conn, device, "Retrieve M-Bus Short ID attributes", SHORT_ID_ATTRIBUTE_ADDRESSES);
    final List<MbusChannelShortEquipmentIdentifierDto> channelShortIds =
        this.channelShortIdsFromGetResults(mbusShortIdResults, device);
    return new ScanMbusChannelsResponseDto(channelShortIds);
  }

  /**
   * @see #ATTRIBUTE_IDS_SHORT_ID
   * @see #makeAttributeAddressesShortIds()
   */
  private List<MbusChannelShortEquipmentIdentifierDto> channelShortIdsFromGetResults(
      final List<GetResult> mbusShortIdResults, final DlmsDevice device)
      throws ProtocolAdapterException {

    /*
     * Process attributes in the same order as they were placed in the
     * attribute addresses used to retrieve the get results.
     */
    final List<MbusChannelShortEquipmentIdentifierDto> channelShortIds = new ArrayList<>();
    int index = 0;
    for (short channel = 1; channel <= NUMBER_OF_CHANNELS; channel++) {
      final String identificationNumber =
          this.determineIdentificationNumber(mbusShortIdResults.get(index++), channel, device);
      final String manufacturerIdentification =
          this.determineManufacturerIdentification(mbusShortIdResults.get(index++), channel);
      final Short versionIdentification =
          this.determineVersionIdentification(mbusShortIdResults.get(index++), channel);
      final Short deviceTypeIdentification =
          this.determineDeviceTypeIdentification(mbusShortIdResults.get(index++), channel);
      final MbusShortEquipmentIdentifierDto shortId =
          new MbusShortEquipmentIdentifierDto(
              identificationNumber,
              manufacturerIdentification,
              versionIdentification,
              deviceTypeIdentification);
      channelShortIds.add(new MbusChannelShortEquipmentIdentifierDto(channel, shortId));
    }
    return channelShortIds;
  }

  private String determineIdentificationNumber(
      final GetResult getResult, final short channel, final DlmsDevice device)
      throws ProtocolAdapterException {

    final Long identification =
        this.dlmsHelper.readLong(getResult, "Identification number on channel " + channel);

    final IdentificationNumber identificationNumber;

    final DlmsObject mbusClientSetupObject =
        this.dlmsObjectConfigService.getDlmsObject(device, DlmsObjectType.MBUS_CLIENT_SETUP);

    if (mbusClientSetupObject.getVersion().equals(DlmsClassVersion.VERSION_0)) {
      identificationNumber = IdentificationNumber.fromBcdRepresentationAsLong(identification);
    } else {
      identificationNumber = IdentificationNumber.fromNumericalRepresentation(identification);
    }

    return identificationNumber.getTextualRepresentation();
  }

  private String determineManufacturerIdentification(final GetResult getResult, final short channel)
      throws ProtocolAdapterException {

    final Integer manufacturerId =
        this.dlmsHelper.readInteger(getResult, "Manufacturer identification on channel " + channel);
    if (manufacturerId == null) {
      return null;
    }
    return ManufacturerId.fromId(manufacturerId).getIdentification();
  }

  private Short determineVersionIdentification(final GetResult getResult, final short channel)
      throws ProtocolAdapterException {

    return this.dlmsHelper.readShort(getResult, "Version identification on channel " + channel);
  }

  private Short determineDeviceTypeIdentification(final GetResult getResult, final short channel)
      throws ProtocolAdapterException {

    return this.dlmsHelper.readShort(getResult, "Device type identification on channel " + channel);
  }
}
