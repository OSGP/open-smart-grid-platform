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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.TimeZone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.CosemDateFormat.Field;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ClockAttribute;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SynchronizeTimeRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class SynchronizeTimeCommandExecutorTest {

  private static final ObisCode LOGICAL_NAME = new ObisCode("0.0.1.0.0.255");

  @Captor ArgumentCaptor<SetParameter> setParameterArgumentCaptor;

  @Captor ArgumentCaptor<DlmsDevice> dlmsDeviceArgumentCaptor;

  @Mock private DlmsConnectionManager conn;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Mock private DlmsConnection dlmsConnection;

  @Mock private MessageMetadata messageMetadata;

  @Mock DlmsDeviceRepository dlmsDeviceRepository;

  private SynchronizeTimeCommandExecutor executor;

  @BeforeEach
  void setUp() throws IOException, ObjectConfigException {
    final ObjectConfigService objectConfigService = new ObjectConfigService();
    final ObjectConfigServiceHelper objectConfigServiceHelper =
        new ObjectConfigServiceHelper(objectConfigService);
    this.executor =
        new SynchronizeTimeCommandExecutor(
            new DlmsHelper(), this.dlmsDeviceRepository, objectConfigServiceHelper);
  }

  @Test
  void testSynchronizeTime() throws ProtocolAdapterException, IOException {
    final String timeZone = "Europe/Amsterdam";
    final ZonedDateTime expectedTime = ZonedDateTime.now(TimeZone.getTimeZone(timeZone).toZoneId());
    final DlmsDevice device = new DlmsDevice();
    device.setTimezone(timeZone);
    device.setProtocol(Protocol.SMR_5_0_0);

    // SETUP
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(any(SetParameter.class))).thenReturn(AccessResultCode.SUCCESS);

    final SynchronizeTimeRequestDto synchronizeTimeRequest =
        new SynchronizeTimeRequestDto(timeZone);

    // CALL
    final AccessResultCode resultCode =
        this.executor.execute(this.conn, device, synchronizeTimeRequest, this.messageMetadata);

    // VERIFY
    assertThat(resultCode).isEqualTo(AccessResultCode.SUCCESS);
    verify(this.dlmsConnection, times(1)).set(this.setParameterArgumentCaptor.capture());

    // save timezone
    verify(this.dlmsDeviceRepository, times(1)).save(this.dlmsDeviceArgumentCaptor.capture());
    assertThat(this.dlmsDeviceArgumentCaptor.getValue().getTimezone()).isEqualTo(timeZone);

    final List<SetParameter> setParameters = this.setParameterArgumentCaptor.getAllValues();
    assertThat(setParameters).hasSize(1);

    final SetParameter setParameter = setParameters.get(0);
    final AttributeAddress attributeAddress = setParameter.getAttributeAddress();
    assertThat(attributeAddress.getClassId()).isEqualTo(InterfaceClass.CLOCK.id());
    assertThat(attributeAddress.getInstanceId()).isEqualTo(LOGICAL_NAME);
    assertThat(attributeAddress.getId()).isEqualTo(ClockAttribute.TIME.attributeId());

    assertThat(setParameter.getData().getType().name()).isEqualTo("DATE_TIME");
    final CosemDateTime cosemDateTime = setParameter.getData().getValue();

    // Explicit check hours and deviation because these are important in UTC transformation
    assertThat(cosemDateTime.get(Field.HOUR)).isEqualTo(expectedTime.getHour());
    assertThat(cosemDateTime.get(Field.DEVIATION))
        .isEqualTo(expectedTime.getOffset().getTotalSeconds() / -60);

    final ZonedDateTime dateTime =
        ZonedDateTime.of(
            cosemDateTime.get(Field.YEAR),
            cosemDateTime.get(Field.MONTH),
            cosemDateTime.get(Field.DAY_OF_MONTH),
            cosemDateTime.get(Field.HOUR),
            cosemDateTime.get(Field.MINUTE),
            cosemDateTime.get(Field.SECOND),
            cosemDateTime.get(Field.HUNDREDTHS) * 10 * 1000,
            ZoneOffset.ofTotalSeconds(cosemDateTime.get(Field.DEVIATION) * -60));
    assertThat(ChronoUnit.SECONDS.between(expectedTime, dateTime)).isZero();
  }
}
