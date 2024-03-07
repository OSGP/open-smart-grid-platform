// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.BitString;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;

@ExtendWith(MockitoExtension.class)
class GetConfigurationObjectServiceDsmr4Test {

  private GetConfigurationObjectServiceDsmr4 instance;

  @Mock private GetResult getResult;
  @Mock private DlmsHelper dlmsHelper;

  @Mock private ObjectConfigServiceHelper objectConfigServiceHelper;
  @Mock private DlmsDeviceRepository dlmsDeviceRepository;

  @BeforeEach
  void setUp() {
    this.instance =
        new GetConfigurationObjectServiceDsmr4(
            this.dlmsHelper, this.objectConfigServiceHelper, this.dlmsDeviceRepository);
  }

  @ParameterizedTest
  @EnumSource(Protocol.class)
  @NullSource
  void handles(final Protocol protocol) {
    assertThat(this.instance.handles(protocol)).isEqualTo(protocol != null && protocol.isDsmr42());
  }

  @Test
  void getFlagType() {
    for (final ConfigurationFlagTypeDto flagTypeDto : ConfigurationFlagTypeDto.values()) {
      flagTypeDto
          .getBitPositionDsmr4()
          .ifPresent(
              bitPosition ->
                  assertThat(
                          this.instance
                              .getFlagType(bitPosition)
                              .orElseThrow(IllegalArgumentException::new))
                      .isEqualTo(flagTypeDto));
    }
  }

  @Test
  void getConfigurationObjectResultDataNull() throws ProtocolAdapterException {
    // SETUP
    when(this.getResult.getResultData()).thenReturn(null);

    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(
            () -> {
              // CALL
              this.instance.getConfigurationObject(this.getResult);
            });
  }

  @Test
  void getConfigurationObjectResultDataNotComplex() throws ProtocolAdapterException {

    // SETUP
    final DataObject nonComplex = mock(DataObject.class);
    when(nonComplex.isComplex()).thenReturn(false);
    when(this.getResult.getResultData()).thenReturn(nonComplex);

    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(
            () -> {
              // CALL
              this.instance.getConfigurationObject(this.getResult);
            });
  }

  @Test
  void getConfigurationObjectElementsNull() throws ProtocolAdapterException {

    // SETUP
    final DataObject structure = mock(DataObject.class);
    when(structure.isComplex()).thenReturn(true);
    when(this.getResult.getResultData()).thenReturn(structure);
    final List<DataObject> elements = null;
    when(structure.getValue()).thenReturn(elements);

    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(
            () -> {
              // CALL
              this.instance.getConfigurationObject(this.getResult);
            });
  }

  @Test
  void getConfigurationObjectElementsSizeNotTwo() throws ProtocolAdapterException {

    // SETUP
    final DataObject structure = mock(DataObject.class);
    when(structure.isComplex()).thenReturn(true);
    when(this.getResult.getResultData()).thenReturn(structure);
    final List<DataObject> elements = new ArrayList<>();
    // elements is empty so size is not two
    when(structure.getValue()).thenReturn(elements);

    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(
            () -> {
              // CALL
              this.instance.getConfigurationObject(this.getResult);
            });
  }

  @Test
  void getConfigurationObjectGprsModeNull() throws ProtocolAdapterException {

    // SETUP
    final DataObject structure = mock(DataObject.class);
    when(structure.isComplex()).thenReturn(true);
    when(this.getResult.getResultData()).thenReturn(structure);
    final List<DataObject> elements = new ArrayList<>();
    // gprs mode is null
    elements.add(null);
    elements.add(null);
    when(structure.getValue()).thenReturn(elements);

    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(
            () -> {
              // CALL
              this.instance.getConfigurationObject(this.getResult);
            });
  }

  @Test
  void getConfigurationObjectGprsModeNotNumber() throws ProtocolAdapterException {

    // SETUP
    final DataObject structure = mock(DataObject.class);
    when(structure.isComplex()).thenReturn(true);
    when(this.getResult.getResultData()).thenReturn(structure);
    final List<DataObject> elements = new ArrayList<>();
    final DataObject gprsMode = mock(DataObject.class);
    // gprs mode is not a number
    when(gprsMode.isNumber()).thenReturn(false);
    elements.add(gprsMode);
    elements.add(null);
    when(structure.getValue()).thenReturn(elements);

    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(
            () -> {
              // CALL
              this.instance.getConfigurationObject(this.getResult);
            });
  }

  @Test
  void getConfigurationObjectFlagsNull() throws ProtocolAdapterException {

    // SETUP
    final DataObject structure = mock(DataObject.class);
    when(structure.isComplex()).thenReturn(true);
    when(this.getResult.getResultData()).thenReturn(structure);
    final List<DataObject> elements = new ArrayList<>();
    final DataObject gprsMode = mock(DataObject.class);
    when(gprsMode.isNumber()).thenReturn(true);
    when(gprsMode.getValue()).thenReturn(42);
    elements.add(gprsMode);
    // flags is null
    elements.add(null);
    when(structure.getValue()).thenReturn(elements);

    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(
            () -> {
              // CALL
              this.instance.getConfigurationObject(this.getResult);
            });
  }

  @Test
  void getConfigurationObjectFlagsNotBitString() throws ProtocolAdapterException {

    // SETUP
    final DataObject structure = mock(DataObject.class);
    when(structure.isComplex()).thenReturn(true);
    when(this.getResult.getResultData()).thenReturn(structure);
    final List<DataObject> elements = new ArrayList<>();
    final DataObject gprsMode = mock(DataObject.class);
    when(gprsMode.isNumber()).thenReturn(true);
    when(gprsMode.getValue()).thenReturn(42);
    elements.add(gprsMode);
    final DataObject flags = mock(DataObject.class);
    // flags is not a BitString
    when(flags.isBitString()).thenReturn(false);
    elements.add(flags);
    when(structure.getValue()).thenReturn(elements);

    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(
            () -> {
              // CALL
              this.instance.getConfigurationObject(this.getResult);
            });
  }

  @Test
  void getConfigurationObjectFlagsIncludeHighAndLowFlags() throws ProtocolAdapterException {

    whenParseGetResult(this.getResult, new byte[] {82, 0});

    final ConfigurationObjectDto configurationObject =
        this.instance.getConfigurationObject(this.getResult);

    final Predicate<ConfigurationFlagTypeDto> protocolVersionPredicate =
        fl -> fl.getBitPositionDsmr4().isPresent();

    assertAllProtocolSpecificFlags(configurationObject, protocolVersionPredicate);
  }

  static void whenParseGetResult(final GetResult getResult, final byte[] flagBytes) {
    final DataObject resultData = mock(DataObject.class);
    when(resultData.isComplex()).thenReturn(true);
    when(getResult.getResultData()).thenReturn(resultData);
    final List<DataObject> listOfDataObject = mock(List.class);
    when(resultData.getValue()).thenReturn(listOfDataObject);
    final DataObject dataObject = mock(DataObject.class);
    final DataObject gprsMode = mock(DataObject.class);
    when(gprsMode.isNumber()).thenReturn(true);
    when(gprsMode.getValue()).thenReturn(Integer.valueOf(1));
    when(listOfDataObject.size()).thenReturn(2);
    when(listOfDataObject.get(0)).thenReturn(gprsMode);
    when(listOfDataObject.get(1)).thenReturn(dataObject);
    when(dataObject.isBitString()).thenReturn(true);
    final BitString bitString = mock(BitString.class);
    when(dataObject.getValue()).thenReturn(bitString);
    when(bitString.getBitString()).thenReturn(flagBytes);
  }

  static void assertAllProtocolSpecificFlags(
      final ConfigurationObjectDto configurationObject,
      final Predicate<ConfigurationFlagTypeDto> protocolVersionPredicate) {
    final List<ConfigurationFlagTypeDto> protocolVersionSpecificFlags =
        Arrays.stream(ConfigurationFlagTypeDto.values()).filter(protocolVersionPredicate).toList();
    final List<ConfigurationFlagTypeDto> flagsFromDevice =
        configurationObject.getConfigurationFlags().getFlags().stream()
            .map(ConfigurationFlagDto::getConfigurationFlagType)
            .toList();
    assertThat(flagsFromDevice).hasSameSizeAs(protocolVersionSpecificFlags);
    assertThat(flagsFromDevice).containsAll(protocolVersionSpecificFlags);
  }

  // happy flows covered in IT's
}
