// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.BitString;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.GetConfigurationObjectService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.GetConfigurationObjectServiceDsmr4;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.SetConfigurationObjectService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.SetConfigurationObjectServiceDsmr4;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GprsOperationModeTypeDto;

@ExtendWith(MockitoExtension.class)
class SetConfigurationObjectCommandExecutorDsmr4IT
    extends SetConfigurationObjectCommandExecutorITBase {

  private static final int INDEX_OF_GPRS_OPERATION_MODE = 0;
  private static final int INDEX_OF_CONFIGURATION_FLAGS = 1;

  @Mock private ObjectConfigServiceHelper objectConfigServiceHelper;
  @Mock private DlmsDeviceRepository dlmsDeviceRepository;

  @BeforeEach
  void setUp() throws IOException {
    final DlmsHelper dlmsHelper = new DlmsHelper();
    final GetConfigurationObjectService getService =
        new GetConfigurationObjectServiceDsmr4(
            dlmsHelper, this.objectConfigServiceHelper, this.dlmsDeviceRepository);
    final SetConfigurationObjectService setService =
        new SetConfigurationObjectServiceDsmr4(
            dlmsHelper, this.objectConfigServiceHelper, this.dlmsDeviceRepository);
    super.setUp(getService, setService);
  }

  @Test
  void execute() throws IOException, ProtocolAdapterException {

    // SETUP
    // configurationToSet: 0111-----1------
    final GprsOperationModeTypeDto gprsModeToSet = GprsOperationModeTypeDto.TRIGGERED;
    final ConfigurationObjectDto configurationToSet =
        this.createConfigurationObjectDto(
            gprsModeToSet,
            this.createFlagDto(ConfigurationFlagTypeDto.DISCOVER_ON_OPEN_COVER, false),
            this.createFlagDto(ConfigurationFlagTypeDto.DISCOVER_ON_POWER_ON, true),
            this.createFlagDto(ConfigurationFlagTypeDto.DYNAMIC_MBUS_ADDRESS, true),
            this.createFlagDto(ConfigurationFlagTypeDto.PO_ENABLE, true),
            this.createFlagDto(ConfigurationFlagTypeDto.HLS_5_ON_P0_ENABLE, true));

    // flagsOnDevice: 1000101010000000
    final byte[] flagsOnDevice =
        this.createFlagBytes(
            ConfigurationFlagTypeDto.DISCOVER_ON_OPEN_COVER,
            ConfigurationFlagTypeDto.HLS_3_ON_P3_ENABLE,
            ConfigurationFlagTypeDto.HLS_5_ON_P3_ENABLE,
            ConfigurationFlagTypeDto.HLS_4_ON_P0_ENABLE);

    // result of merging configurationToSet and flagsOnDevice
    final byte firstExpectedByte = this.asByte("01111010");
    final byte secondExpectedByte = this.asByte("11000000");
    final String deviceIdentification = "device-1";
    final Protocol protocol = Protocol.DSMR_4_2_2;
    final AttributeAddress attributeAddress = new AttributeAddress(1, "0.1.94.31.3.255", 2);

    final GprsOperationModeTypeDto gprsModeOnDevice = GprsOperationModeTypeDto.ALWAYS_ON;
    final DataObject deviceData = this.createStructureData(flagsOnDevice, gprsModeOnDevice);
    when(this.getResult.getResultData()).thenReturn(deviceData);
    when(this.objectConfigServiceHelper.findOptionalDefaultAttributeAddress(
            protocol, DlmsObjectType.CONFIGURATION_OBJECT))
        .thenReturn(Optional.of(attributeAddress));

    final DlmsDevice device = new DlmsDevice();
    device.setDeviceIdentification(deviceIdentification);
    device.setProtocol(protocol);

    // CALL
    final AccessResultCode result =
        this.instance.execute(this.conn, device, configurationToSet, this.messageMetadata);

    // VERIFY
    assertThat(result).isEqualTo(AccessResultCode.SUCCESS);

    final List<DataObject> elements = this.captureSetParameterStructureData();
    final Number gprsOperationMode = elements.get(INDEX_OF_GPRS_OPERATION_MODE).getValue();
    assertThat(gprsOperationMode).isEqualTo(gprsModeToSet.getNumber());

    final BitString configurationFlags = elements.get(INDEX_OF_CONFIGURATION_FLAGS).getValue();
    final byte[] flagsSentToDevice = configurationFlags.getBitString();

    assertThat(flagsSentToDevice[0]).isEqualTo(firstExpectedByte);
    assertThat(flagsSentToDevice[1]).isEqualTo(secondExpectedByte);

    // after Getting current configuration object from device
    verify(this.dlmsDeviceRepository, times(1))
        .updateHlsActive(deviceIdentification, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE);
    // after Setting configuration object on device (nothing changes)
    verify(this.dlmsDeviceRepository, times(1))
        .updateHlsActive(deviceIdentification, null, null, null);
  }

  @Override
  Integer getBitPosition(final ConfigurationFlagTypeDto flag) {
    return flag.getBitPositionDsmr4().orElseThrow(RuntimeException::new);
  }

  private DataObject createStructureData(
      final byte[] flags, final GprsOperationModeTypeDto gprsMode) {
    final DataObject gprsModeData = DataObject.newEnumerateData(gprsMode.getNumber());
    final BitString bitString = new BitString(flags, 16);
    final DataObject flagsData = DataObject.newBitStringData(bitString);
    return DataObject.newStructureData(gprsModeData, flagsData);
  }

  private List<DataObject> captureSetParameterStructureData() throws IOException {
    verify(this.dlmsConnection).set(this.setParameterArgumentCaptor.capture());
    final SetParameter setParameter = this.setParameterArgumentCaptor.getValue();
    final DataObject setParameterData = setParameter.getData();
    return setParameterData.getValue();
  }
}
