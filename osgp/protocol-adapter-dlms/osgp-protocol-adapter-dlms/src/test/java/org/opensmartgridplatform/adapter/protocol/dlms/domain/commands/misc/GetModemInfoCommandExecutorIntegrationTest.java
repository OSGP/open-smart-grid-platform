/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigConfiguration;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.CommunicationMethod;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionManagerStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.BitErrorRateDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CircuitSwitchedStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetModemInfoRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetModemInfoResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ModemInfoDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ModemRegistrationStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PacketSwitchedStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SignalQualityDto;

@ExtendWith(MockitoExtension.class)
public class GetModemInfoCommandExecutorIntegrationTest {

    private GetModemInfoCommandExecutor executor;

    private DlmsConnectionManagerStub connectionManagerStub;
    private DlmsConnectionStub connectionStub;


    @BeforeEach
    public void setUp() {

        final TimeZone defaultTimeZone = TimeZone.getDefault();
        final DateTimeZone defaultDateTimeZone = DateTimeZone.getDefault();

        // all time based tests must use UTC time.
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        DateTimeZone.setDefault(DateTimeZone.UTC);

        final DlmsHelper dlmsHelper = new DlmsHelper();
        final DlmsObjectConfigConfiguration dlmsObjectConfigConfiguration = new DlmsObjectConfigConfiguration();
        final DlmsObjectConfigService dlmsObjectConfigService = new DlmsObjectConfigService(dlmsHelper,
                dlmsObjectConfigConfiguration.getDlmsObjectConfigs());

        this.executor = new GetModemInfoCommandExecutor(dlmsHelper, dlmsObjectConfigService);
        this.connectionStub = new DlmsConnectionStub();
        this.connectionManagerStub = new DlmsConnectionManagerStub(this.connectionStub);

        this.connectionStub.setDefaultReturnValue(DataObject.newArrayData(Collections.emptyList()));

        // reset to original TimeZone
        TimeZone.setDefault(defaultTimeZone);
        DateTimeZone.setDefault(defaultDateTimeZone);
    }

    @Test
    public void testExecuteDsmr4() throws Exception {
        for (final CommunicationMethod method : CommunicationMethod.values()) {
            this.testExecute(Protocol.DSMR_4_2_2, method, true);
        }
    }

    @Test
    public void testExecuteSmr5_0() throws Exception {
        for (final CommunicationMethod method : CommunicationMethod.values()) {
            this.testExecute(Protocol.SMR_5_0_0, method, true);
        }
    }

    @Test
    public void testExecuteSmr5_1() throws Exception {
        for (final CommunicationMethod method : CommunicationMethod.values()) {
            this.testExecute(Protocol.SMR_5_1, method, false);
        }
    }

    private void testExecute(
            final Protocol protocol,
            final CommunicationMethod method,
            final boolean expectObjectNotFound
    ) throws Exception {

        // SETUP

        // Reset stub
        this.connectionStub.clearRequestedAttributeAddresses();

        // Create device with requested protocol version and communication method
        final DlmsDevice device = this.createDlmsDevice(protocol, method);

        // Create request object
        final GetModemInfoRequestDto request = new GetModemInfoRequestDto();

        // Get expected addresses
        final AttributeAddress expectedAddressOperator = this.createAttributeAddress(method, 2);
        final AttributeAddress expectedAddressRegistrationStatus = this.createAttributeAddress(method, 3);
        final AttributeAddress expectedAddressCsStatus = this.createAttributeAddress(method, 4);
        final AttributeAddress expectedAddressPsStatus = this.createAttributeAddress(method, 5);
        final AttributeAddress expectedAddressCellInfo = this.createAttributeAddress(method, 6);
        final AttributeAddress expectedAddressAdjacentCells = this.createAttributeAddress(method, 7);
        final AttributeAddress expectedAddressCaptureTime = this.createAttributeAddress(method, 8);
        final int expectedTotalNumberOfAttributeAddresses = 7;

        // Set responses in stub
        this.setResponseForOperator(expectedAddressOperator, protocol, method);
        this.setResponseForRegistrationStatus(expectedAddressRegistrationStatus, protocol, method);
        this.setResponseForCsStatus(expectedAddressCsStatus, protocol, method);
        this.setResponseForPsStatus(expectedAddressPsStatus, protocol, method);
        this.setResponseForCellInfo(expectedAddressCellInfo, protocol, method);
        this.setResponseForAdjacentCells(expectedAddressAdjacentCells, protocol, method);
        this.setResponseForCaptureTime(expectedAddressCaptureTime, protocol, method);

        // CALL
        GetModemInfoResponseDto response = null;
        try {
            response = this.executor.execute(this.connectionManagerStub, device, request);
        } catch (final ProtocolAdapterException e) {
            if (expectObjectNotFound) {
                assertThat(e.getMessage()).isEqualTo("Did not find MODEM_INFO object for device 6789012");
                return;
            } else {
                fail("Unexpected ProtocolAdapterException: " + e.getMessage());
            }
        }

        // VERIFY

        // Get resulting requests from connection stub
        final List<AttributeAddress> requestedAttributeAddresses = this.connectionStub.getRequestedAttributeAddresses();
        assertThat(requestedAttributeAddresses.size()).isEqualTo(expectedTotalNumberOfAttributeAddresses);

        // Check response
        assertThat(response).isNotNull();
        final ModemInfoDto modemInfoDto = response.getModemInfoDto();
        assertThat(modemInfoDto).isNotNull();
        assertThat(modemInfoDto.getOperator()).isEqualTo("Utility Connect");
        assertThat(modemInfoDto.getModemRegistrationStatus()).isEqualTo(ModemRegistrationStatusDto.REGISTERED_ROAMING);
        assertThat(modemInfoDto.getCircuitSwitchedStatus()).isEqualTo(CircuitSwitchedStatusDto.INACTIVE);
        assertThat(modemInfoDto.getPacketSwitchedStatus()).isEqualTo(PacketSwitchedStatusDto.CDMA);
        assertThat(modemInfoDto.getCellId()).isEqualTo(new byte[] {93, 0, 0, 0});
        assertThat(modemInfoDto.getLocationId()).isEqualTo(new byte[] {-72, 8});
        assertThat(modemInfoDto.getSignalQuality()).isEqualTo(SignalQualityDto.MINUS_87_DBM);
        assertThat(modemInfoDto.getBitErrorRate()).isEqualTo(BitErrorRateDto.RXQUAL_6);
        assertThat(modemInfoDto.getMobileCountryCode()).isEqualTo(204);
        assertThat(modemInfoDto.getMobileNetworkCode()).isEqualTo(66);
        assertThat(modemInfoDto.getChannelNumber()).isEqualTo(107);
        assertThat(modemInfoDto.getNumberOfAdjacentCells()).isEqualTo(3);
        assertThat(modemInfoDto.getAdjacentCellId()).isEqualTo(new byte[] {85, 0, 0, 0});
        assertThat(modemInfoDto.getAdjacentCellSignalQuality()).isEqualTo(SignalQualityDto.MINUS_65_DBM);
        assertThat(modemInfoDto.getCaptureTime()).isEqualTo(new DateTime(2021, 4, 1, 9, 28, DateTimeZone.UTC));
    }

    private DlmsDevice createDlmsDevice(final Protocol protocol, final CommunicationMethod method) {
        final DlmsDevice device = new DlmsDevice();
        device.setDeviceIdentification("123456789012");
        device.setProtocol(protocol);
        device.setCommunicationMethod(method.name());
        return device;
    }

    private AttributeAddress createAttributeAddress(final CommunicationMethod method, final int attributeId)
            throws Exception {

        if (method == CommunicationMethod.GPRS) {
            return new AttributeAddress(47, new ObisCode(0, 0, 25, 6, 0, 255), attributeId);
        } else if (method == CommunicationMethod.CDMA) {
            return new AttributeAddress(47, new ObisCode(0, 1, 25, 6, 0, 255), attributeId);
        } else if (method == CommunicationMethod.LTE_M) {
            return new AttributeAddress(47, new ObisCode(0, 2, 25, 6, 0, 255), attributeId);
        }

        throw new Exception("Invalid communication method " + method.name());
    }

    private void setResponseForOperator(
            final AttributeAddress address,
            final Protocol protocol,
            final CommunicationMethod method
    ) {
        final DataObject responseDataObject = DataObject.newVisibleStringData(
                new byte[] { 85, 116, 105, 108, 105, 116, 121, 32, 67, 111, 110, 110, 101, 99, 116 });
        this.connectionStub.addReturnValue(address, responseDataObject);
    }

    private void setResponseForRegistrationStatus(
            final AttributeAddress address,
            final Protocol protocol,
            final CommunicationMethod method
    ) {
        final DataObject responseDataObject = DataObject.newEnumerateData(5);
        this.connectionStub.addReturnValue(address, responseDataObject);
    }

    private void setResponseForCsStatus(
            final AttributeAddress address,
            final Protocol protocol,
            final CommunicationMethod method
    ) {
        final DataObject responseDataObject = DataObject.newEnumerateData(0);
        this.connectionStub.addReturnValue(address, responseDataObject);
    }

    private void setResponseForPsStatus(
            final AttributeAddress address,
            final Protocol protocol,
            final CommunicationMethod method
    ) {
        final DataObject responseDataObject = DataObject.newEnumerateData(6);
        this.connectionStub.addReturnValue(address, responseDataObject);
    }

    private void setResponseForCellInfo(
            final AttributeAddress address,
            final Protocol protocol,
            final CommunicationMethod method
    ) {
        final DataObject cellId = DataObject.newUInteger32Data(93);
        final DataObject locationId = DataObject.newUInteger16Data(2232);
        final DataObject signalQuality = DataObject.newUInteger8Data((short) 13);
        final DataObject ber = DataObject.newUInteger8Data((short) 6);
        final DataObject mcc = DataObject.newUInteger16Data(204);
        final DataObject mnc = DataObject.newUInteger16Data(66);
        final DataObject channelNumber = DataObject.newUInteger32Data(107);

        final DataObject responseDataObject = DataObject.newStructureData(
                cellId,
                locationId,
                signalQuality,
                ber,
                mcc,
                mnc,
                channelNumber
        );
        this.connectionStub.addReturnValue(address, responseDataObject);
    }

    private void setResponseForAdjacentCells(
            final AttributeAddress address,
            final Protocol protocol,
            final CommunicationMethod method
    ) {
        final DataObject cellId1 = DataObject.newUInteger32Data(85);
        final DataObject signalQuality1 = DataObject.newUInteger8Data((short) 24);
        final DataObject adjacentCells1 = DataObject.newStructureData(cellId1, signalQuality1);
        final DataObject cellId2 = DataObject.newUInteger32Data(0);
        final DataObject signalQuality2 = DataObject.newUInteger8Data((short) 0);
        final DataObject adjacentCells2 = DataObject.newStructureData(cellId2, signalQuality2);
        final DataObject cellId3 = DataObject.newUInteger32Data(303);
        final DataObject signalQuality3 = DataObject.newUInteger8Data((short) 31);
        final DataObject adjacentCells3 = DataObject.newStructureData(cellId3, signalQuality3);

        final DataObject responseDataObject = DataObject
                .newArrayData(Arrays.asList(adjacentCells1, adjacentCells2, adjacentCells3));
        this.connectionStub.addReturnValue(address, responseDataObject);
    }

    private void setResponseForCaptureTime(
            final AttributeAddress address,
            final Protocol protocol,
            final CommunicationMethod method
    ) {
        final DataObject responseDataObject = DataObject.newDateTimeData(new CosemDateTime(2021, 4, 1, 9, 28, 0, 0));
        this.connectionStub.addReturnValue(address, responseDataObject);
    }

    private DataObject getDateAsOctetString(final int year, final int month, final int day) {
        final CosemDateTime dateTime = new CosemDateTime(year, month, day, 0, 0, 0, 0);

        return DataObject.newOctetStringData(dateTime.encode());
    }
}
