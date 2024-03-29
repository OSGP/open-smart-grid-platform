// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionManagerStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.valueobjects.CombinedDeviceModelCode.CombinedDeviceModelCodeBuilder;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualMeterReadsQueryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MeterReadsGasResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class GetActualMeterReadsGasCommandExecutorIntegrationTest {

  private GetActualMeterReadsGasCommandExecutor executor;

  private DlmsHelper dlmsHelper;
  private ObjectConfigService objectConfigService;

  private DlmsConnectionManagerStub connectionManagerStub;
  private DlmsConnectionStub connectionStub;

  private static final String OBIS_ACTUAL = "0.1.24.2.1.255";
  private static final int CLASS_ID_EXTENDED_REGISTER = 4;
  private static final byte ATTR_ID_VALUE = 2;
  private static final byte ATTR_ID_CAPTURE_TIME = 5;

  private static final int CHANNEL = 1;

  private static final DateTime DATE_TIME = DateTime.parse("2018-12-31T23:00:00Z");

  private final long VALUE = 1000L;

  private static final List<String> GMETER_TYPES = List.of("G4", "G6", "G10", "G16", "G25");
  private static final Map<String, Integer> SCALERS_FOR_METER_TYPES =
      Map.ofEntries(
          entry("G4", -3), entry("G6", -3), entry("G10", -2), entry("G16", -2), entry("G25", -2));

  @BeforeEach
  public void setUp() throws IOException, ObjectConfigException {
    this.dlmsHelper = new DlmsHelper();
    this.objectConfigService = new ObjectConfigService();

    this.executor =
        new GetActualMeterReadsGasCommandExecutor(this.objectConfigService, this.dlmsHelper);
    this.connectionStub = new DlmsConnectionStub();
    this.connectionManagerStub = new DlmsConnectionManagerStub(this.connectionStub);

    this.connectionStub.setDefaultReturnValue(DataObject.newArrayData(Collections.emptyList()));
  }

  private static Stream<Arguments> generateCombinations() {
    final List<Arguments> arguments = new ArrayList<>();

    for (final Protocol protocol : Protocol.values()) {
      for (final String gMeterType : GMETER_TYPES) {
        if (protocol != Protocol.OTHER_PROTOCOL) {
          arguments.add(Arguments.of(protocol, gMeterType));
        }
      }
    }

    return arguments.stream();
  }

  @ParameterizedTest
  @MethodSource("generateCombinations")
  void testExecuteDsmr(final Protocol protocol, final String mbusDeviceModelCode) throws Exception {

    // SETUP
    // set device model code in a comma seperated list per channel index 1-4 is channel 1-4 and
    // index 0 is device model code of master device
    final String combinedDeviceModelCodes =
        new CombinedDeviceModelCodeBuilder()
            .channelBasedDeviceModelCode(CHANNEL, mbusDeviceModelCode)
            .build()
            .toString();

    final MessageMetadata messageMetadata =
        MessageMetadata.newBuilder()
            .withCorrelationUid("123456")
            .withDeviceModelCode(combinedDeviceModelCodes)
            .build();

    // Reset stub
    this.connectionStub.clearRequestedAttributeAddresses();

    // Create device with requested protocol version
    final DlmsDevice device = this.createDlmsDevice(protocol);

    // Create request object
    final ActualMeterReadsQueryDto request =
        new ActualMeterReadsQueryDto(ChannelDto.fromNumber(CHANNEL));

    // Set expected addresses
    final AttributeAddress expectedAddressValue = this.createAttributeAddress(this.ATTR_ID_VALUE);
    final AttributeAddress expectedAddressTime =
        this.createAttributeAddress(this.ATTR_ID_CAPTURE_TIME);

    // Set response in stub
    this.setResponses(expectedAddressValue, expectedAddressTime);

    // CALL
    final MeterReadsGasResponseDto response =
        this.executor.execute(this.connectionManagerStub, device, request, messageMetadata);

    // VERIFY

    // The executor should have asked for 2 addresses: the value and the capture time
    final List<AttributeAddress> requestedAttributeAddresses =
        this.connectionStub.getRequestedAttributeAddresses();
    assertThat(requestedAttributeAddresses).hasSize(2);

    // Check response
    assertThat(response.getCaptureTime()).isEqualTo(this.DATE_TIME.toDate());
    this.checkValue(
        response.getConsumption(), this.SCALERS_FOR_METER_TYPES.get(mbusDeviceModelCode));
  }

  private DlmsDevice createDlmsDevice(final Protocol protocol) {
    final DlmsDevice device = new DlmsDevice();
    device.setProtocol(protocol);
    return device;
  }

  private void setResponses(
      final AttributeAddress addressValue, final AttributeAddress addressTime) {

    final DataObject value = DataObject.newInteger64Data(this.VALUE);
    final DataObject dateTime =
        DataObject.newDateTimeData(
            new CosemDateTime(
                this.DATE_TIME.getYear(),
                this.DATE_TIME.getMonthOfYear(),
                this.DATE_TIME.getDayOfMonth(),
                this.DATE_TIME.getHourOfDay(),
                this.DATE_TIME.getMinuteOfHour(),
                this.DATE_TIME.getSecondOfMinute(),
                0));

    this.connectionStub.addReturnValue(addressValue, value);
    this.connectionStub.addReturnValue(addressTime, dateTime);
  }

  private void checkValue(final DlmsMeterValueDto valueDto, final int scaler) {

    final BigDecimal multiplier = BigDecimal.valueOf(pow(10, abs(scaler)));
    assertThat(valueDto.getValue().multiply(multiplier).intValue()).isEqualTo(this.VALUE);
  }

  private AttributeAddress createAttributeAddress(final int attributeId) {
    return new AttributeAddress(
        this.CLASS_ID_EXTENDED_REGISTER, this.OBIS_ACTUAL, attributeId, null);
  }
}
