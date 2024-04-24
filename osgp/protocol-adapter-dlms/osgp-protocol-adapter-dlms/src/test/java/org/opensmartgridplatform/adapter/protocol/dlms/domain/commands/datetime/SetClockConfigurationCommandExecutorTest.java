// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.datetime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import ma.glasnost.orika.MapperFacade;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ClockAttribute;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dlms.services.Protocol;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetClockConfigurationRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class SetClockConfigurationCommandExecutorTest {

  protected static final int CLASS_ID = 8;
  private static final String OBIS = "0.0.1.0.0.255";
  private static final ObisCode OBIS_CODE = new ObisCode(OBIS);

  @Mock private DlmsDevice dlmsDevice;

  @Mock private DlmsConnectionManager conn;
  @Mock private DlmsMessageListener dlmsMessageListener;
  @Mock private DlmsConnection dlmsConnection;
  @Mock private MessageMetadata messageMetadata;

  final MapperFacade configurationMapper = new ConfigurationMapper();

  private SetClockConfigurationCommandExecutor executor;

  @Captor ArgumentCaptor<SetParameter> setParameterArgumentCaptor;

  @BeforeEach
  public void setUp() throws IOException, ObjectConfigException {
    final ObjectConfigService objectConfigService = new ObjectConfigService();

    this.executor =
        new SetClockConfigurationCommandExecutor(this.configurationMapper, objectConfigService);
  }

  @ParameterizedTest
  @EnumSource(Protocol.class)
  void testExecute(final Protocol protocol) throws ProtocolAdapterException, IOException {

    when(this.dlmsDevice.getProtocolName()).thenReturn(protocol.getName());
    when(this.dlmsDevice.getProtocolVersion()).thenReturn(protocol.getVersion());

    // SETUP
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(any(SetParameter.class))).thenReturn(AccessResultCode.SUCCESS);

    final short timeZoneOffset = 60;
    final DateTime daylightSavingsBegin = new DateTime(2023, 3, 26, 1, 0, 0, DateTimeZone.UTC);
    final DateTime daylightSavingsEnd = new DateTime(2023, 3, 26, 1, 0, 0, DateTimeZone.UTC);
    final boolean daylightSavingsEnabled = true;

    final SetClockConfigurationRequestDto requestDto =
        new SetClockConfigurationRequestDto(
            timeZoneOffset,
            new CosemDateTimeDto(daylightSavingsBegin),
            new CosemDateTimeDto(daylightSavingsEnd),
            daylightSavingsEnabled);

    // CALL
    this.executor.execute(this.conn, this.dlmsDevice, requestDto, this.messageMetadata);

    // VERIFY
    verify(this.dlmsConnection, times(4)).set(this.setParameterArgumentCaptor.capture());
    final List<SetParameter> setParameters = this.setParameterArgumentCaptor.getAllValues();
    this.verifySetParameter(
        setParameters.get(0),
        ClockAttribute.TIME_ZONE,
        DataObject.newInteger16Data(requestDto.getTimeZoneOffset()));
    this.verifySetParameter(
        setParameters.get(1),
        ClockAttribute.DAYLIGHT_SAVINGS_BEGIN,
        DataObject.newOctetStringData(
            this.newCosemDateTime(requestDto.getDaylightSavingsBegin()).encode()));
    this.verifySetParameter(
        setParameters.get(2),
        ClockAttribute.DAYLIGHT_SAVINGS_END,
        DataObject.newOctetStringData(
            this.newCosemDateTime(requestDto.getDaylightSavingsEnd()).encode()));
    this.verifySetParameter(
        setParameters.get(3),
        ClockAttribute.DAYLIGHT_SAVINGS_ENABLED,
        DataObject.newBoolData(requestDto.isDaylightSavingsEnabled()));
  }

  private CosemDateTime newCosemDateTime(final CosemDateTimeDto dateTime) {
    return this.configurationMapper.map(dateTime, CosemDateTime.class);
  }

  private void verifySetParameter(
      final SetParameter setParameter,
      final ClockAttribute clockAttribute,
      final DataObject expectedDataObject) {
    final AttributeAddress attributeAddress = setParameter.getAttributeAddress();
    assertThat(attributeAddress.getClassId()).isEqualTo(CLASS_ID);
    assertThat(attributeAddress.getInstanceId()).isEqualTo(OBIS_CODE);
    assertThat(attributeAddress.getId()).isEqualTo(clockAttribute.attributeId());

    final DataObject dataObject = setParameter.getData();
    assertThat(dataObject.getType()).isEqualTo(expectedDataObject.getType());
    assertThat(dataObject.getRawValue()).isEqualTo(expectedDataObject.getRawValue());
  }
}
