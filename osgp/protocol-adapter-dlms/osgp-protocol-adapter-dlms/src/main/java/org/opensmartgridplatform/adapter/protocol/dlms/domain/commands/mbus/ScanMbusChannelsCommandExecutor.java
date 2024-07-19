// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.MBUS_CLIENT_SETUP;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.CosemObjectAccessor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.NotSupportedByProtocolException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.MbusClientAttribute;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MbusChannelShortEquipmentIdentifierDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MbusShortEquipmentIdentifierDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ScanMbusChannelsRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ScanMbusChannelsResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScanMbusChannelsCommandExecutor
    extends AbstractCommandExecutor<Void, ScanMbusChannelsResponseDto> {

  /** IDs of the attributes of the M-Bus Client Setup that make up the Short ID. */
  private static final MbusClientAttribute[] ATTRIBUTE_IDS_SHORT_ID =
      new MbusClientAttribute[] {
        MbusClientAttribute.IDENTIFICATION_NUMBER,
        MbusClientAttribute.MANUFACTURER_ID,
        MbusClientAttribute.VERSION,
        MbusClientAttribute.DEVICE_TYPE
      };

  private static final int NUMBER_OF_CHANNELS = 4;
  private static final int NUMBER_OF_SHORT_ID_ATTRIBUTES_PER_CHANNEL =
      ATTRIBUTE_IDS_SHORT_ID.length;

  private final DlmsHelper dlmsHelper;
  private final ObjectConfigServiceHelper objectConfigServiceHelper;

  public ScanMbusChannelsCommandExecutor(
      final DlmsHelper dlmsHelper, final ObjectConfigServiceHelper objectConfigServiceHelper) {
    super(ScanMbusChannelsRequestDataDto.class);
    this.dlmsHelper = dlmsHelper;
    this.objectConfigServiceHelper = objectConfigServiceHelper;
  }

  private AttributeAddress[] makeAttributeAddressesShortIds(
      final DlmsConnectionManager conn, final DlmsDevice device) throws ProtocolAdapterException {
    final AttributeAddress[] shortIdAddresses =
        new AttributeAddress[NUMBER_OF_CHANNELS * NUMBER_OF_SHORT_ID_ATTRIBUTES_PER_CHANNEL];
    int index = 0;
    for (int channel = 1; channel <= NUMBER_OF_CHANNELS; channel++) {

      final CosemObjectAccessor cosemObjectAccessor =
          this.createCosemObjectAccessor(conn, device, (short) channel);

      for (int i = 0; i < NUMBER_OF_SHORT_ID_ATTRIBUTES_PER_CHANNEL; i++) {
        shortIdAddresses[index++] =
            cosemObjectAccessor.createAttributeAddress(ATTRIBUTE_IDS_SHORT_ID[i]);
      }
    }
    return shortIdAddresses;
  }

  @Override
  public Void fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {
    /*
     * ScanMBusChannelsRequestDto does not contain any values to pass on,
     * and the ScanMBusChannelsCommandExecutor takes a Void as input that is
     * ignored.
     */
    return null;
  }

  @Override
  public ScanMbusChannelsResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Void mBusAttributesDto,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    log.debug("retrieving M-Bus info on e-meter");
    final List<GetResult> mBusShortIdResults =
        this.dlmsHelper.getAndCheck(
            conn,
            device,
            "Retrieve M-Bus Short ID attributes",
            this.makeAttributeAddressesShortIds(conn, device));

    final int mBusClientSetupVersion = this.getmBusClientSetupVersion(conn, device);
    final List<MbusChannelShortEquipmentIdentifierDto> channelShortIds =
        this.channelShortIdsFromGetResults(mBusShortIdResults, mBusClientSetupVersion);
    return new ScanMbusChannelsResponseDto(channelShortIds);
  }

  private List<MbusChannelShortEquipmentIdentifierDto> channelShortIdsFromGetResults(
      final List<GetResult> mBusShortIdResults, final int mBusClientSetupVersion)
      throws ProtocolAdapterException {

    /*
     * Process attributes in the same order as they were placed in the
     * attribute addresses used to retrieve the get results.
     */
    final List<MbusChannelShortEquipmentIdentifierDto> channelShortIds = new ArrayList<>();
    int index = 0;
    for (short channel = 1; channel <= NUMBER_OF_CHANNELS; channel++) {
      final String identificationNumber =
          this.determineIdentificationNumber(
              mBusShortIdResults.get(index++), channel, mBusClientSetupVersion);
      final String manufacturerIdentification =
          this.determineManufacturerIdentification(mBusShortIdResults.get(index++), channel);
      final Short versionIdentification =
          this.determineVersionIdentification(mBusShortIdResults.get(index++), channel);
      final Short deviceTypeIdentification =
          this.determineDeviceTypeIdentification(mBusShortIdResults.get(index++), channel);
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
      final GetResult getResult, final short channel, final int mBusClientSetupVersion)
      throws ProtocolAdapterException {

    final Long identification =
        this.dlmsHelper.readLong(getResult, "Identification number on channel " + channel);

    final IdentificationNumber identificationNumber;

    if (this.identificationNumberStoredAsBcdOnDevice(mBusClientSetupVersion)) {
      identificationNumber = IdentificationNumber.fromBcdRepresentationAsLong(identification);
    } else {
      identificationNumber = IdentificationNumber.fromNumericalRepresentation(identification);
    }

    return identificationNumber.getTextualRepresentation();
  }

  private boolean identificationNumberStoredAsBcdOnDevice(final int mBusClientSetupVersion) {
    return mBusClientSetupVersion == 0;
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

  private CosemObjectAccessor createCosemObjectAccessor(
      final DlmsConnectionManager conn, final DlmsDevice device, final short channel)
      throws NotSupportedByProtocolException {
    return new CosemObjectAccessor(
        conn,
        this.objectConfigServiceHelper,
        MBUS_CLIENT_SETUP,
        Protocol.forDevice(device),
        channel);
  }

  private int getmBusClientSetupVersion(final DlmsConnectionManager conn, final DlmsDevice device)
      throws NotSupportedByProtocolException {
    // version is equal for all channels since it comes from the same configuration
    final CosemObjectAccessor cosemObjectAccessor =
        this.createCosemObjectAccessor(conn, device, (short) 1);
    return cosemObjectAccessor.getVersion();
  }
}
