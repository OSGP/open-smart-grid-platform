// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import static org.opensmartgridplatform.dlms.interfaceclass.attribute.MbusClientAttribute.DEVICE_TYPE;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.MbusClientAttribute.IDENTIFICATION_NUMBER;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.MbusClientAttribute.MANUFACTURER_ID;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.MbusClientAttribute.PRIMARY_ADDRESS;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.MbusClientAttribute.VERSION;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.MBUS_CLIENT_SETUP;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.CosemObjectAccessor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DataObjectAttrExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DataObjectAttrExecutors;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.FindMatchingChannelHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.NotSupportedByProtocolException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.MbusClientAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.method.MBusClientMethod;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MbusChannelElementsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component()
public class DeviceChannelsHelper {

  private static final DataObject UINT_8_ZERO = DataObject.newUInteger8Data((short) 0);
  private static final DataObject UINT_16_ZERO = DataObject.newUInteger16Data(0);
  private static final DataObject UINT_32_ZERO = DataObject.newUInteger32Data(0L);

  private static final int NUMBER_OF_ATTRIBUTES_MBUS_CLIENT = 5;
  private static final int INDEX_PRIMARY_ADDRESS = 0;
  private static final int INDEX_IDENTIFICATION_NUMBER = 1;
  private static final int INDEX_MANUFACTURER_ID = 2;
  private static final int INDEX_VERSION = 3;
  private static final int INDEX_DEVICE_TYPE = 4;

  private static final short FIRST_CHANNEL = 1;
  private static final short NR_OF_CHANNELS = 4;

  private final DlmsHelper dlmsHelper;
  private final ObjectConfigServiceHelper objectConfigServiceHelper;

  @Autowired
  public DeviceChannelsHelper(
      final DlmsHelper dlmsHelper, final ObjectConfigServiceHelper objectConfigServiceHelper) {
    this.dlmsHelper = dlmsHelper;
    this.objectConfigServiceHelper = objectConfigServiceHelper;
  }

  public List<ChannelElementValuesDto> findCandidateChannelsForDevice(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final MbusChannelElementsDto requestDto)
      throws ProtocolAdapterException {

    final List<ChannelElementValuesDto> channelElementValuesList = new ArrayList<>();
    for (short channel = FIRST_CHANNEL; channel < FIRST_CHANNEL + NR_OF_CHANNELS; channel++) {
      final ChannelElementValuesDto channelElementValues =
          this.getChannelElementValues(conn, device, channel);
      channelElementValuesList.add(channelElementValues);
      if (requestDto != null
          && FindMatchingChannelHelper.matches(requestDto, channelElementValues)) {
        /*
         * A complete match for all attributes from the request has been
         * found. Stop retrieving M-Bus Client Setup attributes for
         * other channels. Return a list returning the values retrieved
         * so far and don't retrieve any additional M-Bus Client Setup
         * data from the device.
         */
        return channelElementValuesList;
      }
    }
    /*
     * A complete match for all attributes from the request has not been
     * found. The best partial match that has no conflicting attribute
     * values, or the first free channel has to be picked from this list.
     */
    return channelElementValuesList;
  }

  private List<GetResult> getMBusClientAttributeValues(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final AttributeAddress[] attrAddresses)
      throws ProtocolAdapterException {
    conn.getDlmsMessageListener()
        .setDescription(
            "DeviceChannelsHelper, retrieve M-Bus client setup attributes: "
                + JdlmsObjectToStringUtil.describeAttributes(attrAddresses));
    return this.dlmsHelper.getWithList(conn, device, attrAddresses);
  }

  protected void resetMBusClientAttributeValues(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final short channel,
      final String executorName)
      throws ProtocolAdapterException {

    final CosemObjectAccessor cosemObjectAccessor =
        new CosemObjectAccessor(
            conn,
            this.objectConfigServiceHelper,
            MBUS_CLIENT_SETUP,
            Protocol.forDevice(device),
            channel);

    final DataObjectAttrExecutors dataObjectExecutors =
        new DataObjectAttrExecutors(executorName)
            .addExecutor(
                this.getMbusAttributeExecutor(
                    cosemObjectAccessor, IDENTIFICATION_NUMBER, UINT_32_ZERO))
            .addExecutor(
                this.getMbusAttributeExecutor(cosemObjectAccessor, MANUFACTURER_ID, UINT_16_ZERO))
            .addExecutor(this.getMbusAttributeExecutor(cosemObjectAccessor, VERSION, UINT_8_ZERO))
            .addExecutor(
                this.getMbusAttributeExecutor(cosemObjectAccessor, DEVICE_TYPE, UINT_8_ZERO))
            .addExecutor(
                this.getMbusAttributeExecutor(cosemObjectAccessor, PRIMARY_ADDRESS, UINT_8_ZERO));

    conn.getDlmsMessageListener()
        .setDescription(String.format("Reset MBus attributes to channel %d", channel));

    dataObjectExecutors.execute(conn);
  }

  protected ChannelElementValuesDto getChannelElementValues(
      final DlmsConnectionManager conn, final DlmsDevice device, final short channel)
      throws ProtocolAdapterException {
    final AttributeAddress[] attrAddresses = this.makeAttributeAddresses(device, channel);
    final List<GetResult> resultList =
        this.getMBusClientAttributeValues(conn, device, attrAddresses);

    final CosemObject cosemObject = this.getCosemObject(Protocol.forDevice(device));
    return this.makeChannelElementValues(channel, resultList, cosemObject);
  }

  private ChannelElementValuesDto makeChannelElementValues(
      final short channel, final List<GetResult> resultList, final CosemObject mbusClientSetup)
      throws ProtocolAdapterException {
    final short primaryAddress =
        this.readShort(resultList, INDEX_PRIMARY_ADDRESS, "primaryAddress");
    final String manufacturerIdentification = this.readManufacturerIdentification(resultList);
    final short version = this.readShort(resultList, INDEX_VERSION, "version");
    final short deviceTypeIdentification =
        this.readShort(resultList, INDEX_DEVICE_TYPE, "deviceTypeIdentification");
    try {
      final String identificationNumber =
          this.readIdentificationNumber(resultList, mbusClientSetup);
      return new ChannelElementValuesDto(
          channel,
          primaryAddress,
          identificationNumber,
          manufacturerIdentification,
          version,
          deviceTypeIdentification);
    } catch (final IllegalArgumentException e) {
      throw new InvalidIdentificationNumberException(
          String.format("Invalid Channel information in channel %d", channel),
          new ChannelElementValuesDto(
              channel,
              primaryAddress,
              resultList.get(INDEX_IDENTIFICATION_NUMBER).getResultData().toString(),
              manufacturerIdentification,
              version,
              deviceTypeIdentification));
    }
  }

  private String readIdentificationNumber(
      final List<GetResult> resultList, final CosemObject mbusClientSetup)
      throws ProtocolAdapterException {

    final GetResult getResult = resultList.get(INDEX_IDENTIFICATION_NUMBER);
    final DataObject resultData = getResult.getResultData();

    if (resultData == null) {
      return null;
    } else {
      final Long identification = this.dlmsHelper.readLong(resultData, "identificationNumber");
      final IdentificationNumber identificationNumber;
      if (this.identificationNumberStoredAsBcdOnDevice(mbusClientSetup)) {
        identificationNumber = IdentificationNumber.fromBcdRepresentationAsLong(identification);
      } else {
        identificationNumber = IdentificationNumber.fromNumericalRepresentation(identification);
      }
      return identificationNumber.getTextualRepresentation();
    }
  }

  private boolean identificationNumberStoredAsBcdOnDevice(final CosemObject mbusClientSetup) {
    return mbusClientSetup.getVersion() == 0;
  }

  private String readManufacturerIdentification(final List<GetResult> resultList)
      throws ProtocolAdapterException {

    final int manufacturerId =
        this.readInt(resultList, INDEX_MANUFACTURER_ID, "manufacturerIdentification");
    return ManufacturerId.fromId(manufacturerId).getIdentification();
  }

  private int readInt(final List<GetResult> resultList, final int index, final String description)
      throws ProtocolAdapterException {
    final Integer value = this.dlmsHelper.readInteger(resultList.get(index), description);
    return value == null ? 0 : value;
  }

  private short readShort(
      final List<GetResult> resultList, final int index, final String description)
      throws ProtocolAdapterException {
    final Short value = this.dlmsHelper.readShort(resultList.get(index), description);
    return value == null ? 0 : value;
  }

  private AttributeAddress[] makeAttributeAddresses(final DlmsDevice device, final int channel)
      throws ProtocolAdapterException {
    final EnumMap<MbusClientAttribute, Integer> map = new EnumMap<>(MbusClientAttribute.class);
    map.put(PRIMARY_ADDRESS, INDEX_PRIMARY_ADDRESS);
    map.put(IDENTIFICATION_NUMBER, INDEX_IDENTIFICATION_NUMBER);
    map.put(MANUFACTURER_ID, INDEX_MANUFACTURER_ID);
    map.put(VERSION, INDEX_VERSION);
    map.put(DEVICE_TYPE, INDEX_DEVICE_TYPE);

    final AttributeAddress[] attrAddresses = new AttributeAddress[NUMBER_OF_ATTRIBUTES_MBUS_CLIENT];

    for (final Entry<MbusClientAttribute, Integer> entry : map.entrySet()) {
      attrAddresses[entry.getValue()] =
          this.objectConfigServiceHelper.findAttributeAddress(
              device,
              Protocol.forDevice(device),
              MBUS_CLIENT_SETUP,
              channel,
              entry.getKey().attributeId());
    }
    return attrAddresses;
  }

  protected MethodResultCode deinstallSlave(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final short channel,
      final CosemObjectAccessor mBusSetup)
      throws ProtocolAdapterException {
    // in blue book version 10, the parameter is of type integer
    DataObject parameter = DataObject.newInteger8Data((byte) 0);
    conn.getDlmsMessageListener().setDescription("Call slave deinstall method");
    MethodResultCode slaveDeinstallResultCode =
        mBusSetup.callMethod(MBusClientMethod.SLAVE_DEINSTALL, parameter);
    if (slaveDeinstallResultCode == MethodResultCode.TYPE_UNMATCHED) {
      // in blue book version 12, the parameter is of type unsigned, we
      // will try again with that type
      parameter = DataObject.newUInteger8Data((byte) 0);
      slaveDeinstallResultCode = mBusSetup.callMethod(MBusClientMethod.SLAVE_DEINSTALL, parameter);
    }
    if (slaveDeinstallResultCode != MethodResultCode.SUCCESS) {
      log.warn(
          "Slave deinstall was not successfull on device {} on channel {}",
          device.getDeviceIdentification(),
          channel);
    }
    return slaveDeinstallResultCode;
  }

  protected ChannelElementValuesDto writeUpdatedMbus(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final MbusChannelElementsDto requestDto,
      final short channel,
      final String executorName)
      throws ProtocolAdapterException {

    final Protocol protocol = Protocol.forDevice(device);
    final CosemObject mbusClientSetupObject = this.getCosemObject(protocol);

    final DataObject identificationNumber;

    if (this.identificationNumberStoredAsBcdOnDevice(mbusClientSetupObject)) {
      identificationNumber =
          IdentificationNumber.fromTextualRepresentation(requestDto.getMbusIdentificationNumber())
              .asDataObjectInBcdRepresentation();
    } else {
      identificationNumber =
          IdentificationNumber.fromTextualRepresentation(requestDto.getMbusIdentificationNumber())
              .asDataObject();
    }

    final CosemObjectAccessor cosemObjectAccessor =
        new CosemObjectAccessor(
            conn,
            this.objectConfigServiceHelper,
            MBUS_CLIENT_SETUP,
            Protocol.forDevice(device),
            channel);

    final DataObject manufacturerId =
        ManufacturerId.fromIdentification(requestDto.getMbusManufacturerIdentification())
            .asDataObject();
    final DataObject mbusVersion = DataObject.newUInteger8Data(requestDto.getMbusVersion());
    final DataObject deviceType =
        DataObject.newUInteger8Data(requestDto.getMbusDeviceTypeIdentification());

    final DataObjectAttrExecutors dataObjectExecutors =
        new DataObjectAttrExecutors(executorName)
            .addExecutor(
                this.getMbusAttributeExecutor(
                    cosemObjectAccessor, IDENTIFICATION_NUMBER, identificationNumber))
            .addExecutor(
                this.getMbusAttributeExecutor(cosemObjectAccessor, MANUFACTURER_ID, manufacturerId))
            .addExecutor(this.getMbusAttributeExecutor(cosemObjectAccessor, VERSION, mbusVersion))
            .addExecutor(
                this.getMbusAttributeExecutor(cosemObjectAccessor, DEVICE_TYPE, deviceType));

    if (requestDto.getPrimaryAddress() != null) {
      dataObjectExecutors.addExecutor(
          this.getMbusAttributeExecutor(
              cosemObjectAccessor,
              PRIMARY_ADDRESS,
              DataObject.newUInteger8Data(requestDto.getPrimaryAddress())));
    }
    conn.getDlmsMessageListener()
        .setDescription(
            String.format(
                "Write updated MBus attributes to channel %d, set attributes: %s",
                channel, dataObjectExecutors.describeAttributes()));

    dataObjectExecutors.execute(conn);

    log.info("Finished coupling the mbus device to the gateway device");

    return new ChannelElementValuesDto(
        channel,
        requestDto.getPrimaryAddress(),
        requestDto.getMbusIdentificationNumber(),
        requestDto.getMbusManufacturerIdentification(),
        requestDto.getMbusVersion(),
        requestDto.getMbusDeviceTypeIdentification());
  }

  private CosemObject getCosemObject(final Protocol protocol)
      throws NotSupportedByProtocolException {
    final Optional<CosemObject> mbusClientSetup =
        this.objectConfigServiceHelper.getOptionalCosemObject(
            protocol.getName(), protocol.getVersion(), MBUS_CLIENT_SETUP);
    if (mbusClientSetup.isEmpty()) {
      throw new NotSupportedByProtocolException(
          String.format(
              "%s object not supported for protocol %s %s",
              MBUS_CLIENT_SETUP.name(), protocol.getName(), protocol.getVersion()));
    }
    return mbusClientSetup.get();
  }

  private DataObjectAttrExecutor getMbusAttributeExecutor(
      final CosemObjectAccessor cosemObjectAccessor,
      final MbusClientAttribute attribute,
      final DataObject value)
      throws NotSupportedByProtocolException {

    final AttributeAddress attributeAddress = cosemObjectAccessor.createAttributeAddress(attribute);

    return new DataObjectAttrExecutor(
        attribute.attributeName(),
        attributeAddress,
        value,
        attributeAddress.getClassId(),
        attributeAddress.getInstanceId(),
        attribute.attributeId());
  }

  protected ChannelElementValuesDto findEmptyChannel(
      final List<ChannelElementValuesDto> channelElementValuesList) {
    for (final ChannelElementValuesDto channelElementValues : channelElementValuesList) {

      if (this.checkChannelIdentificationValues(channelElementValues)
          && this.checkChannelConfigurationValues(channelElementValues)) {
        return channelElementValues;
      }
    }
    return null;
  }

  /**
   * @return true if all channel elements are 0 or null
   */
  private boolean checkChannelConfigurationValues(
      final ChannelElementValuesDto channelElementValues) {
    return (channelElementValues.getVersion() == 0)
        && (channelElementValues.getDeviceTypeIdentification() == 0)
        && channelElementValues.getManufacturerIdentification() == null;
  }

  /**
   * @return true if all channel elements are 0 or null
   */
  private boolean checkChannelIdentificationValues(
      final ChannelElementValuesDto channelElementValues) {
    return (channelElementValues.getIdentificationNumber() == null
        || "00000000".equals(channelElementValues.getIdentificationNumber())
            && (channelElementValues.getPrimaryAddress() == 0));
  }
}
