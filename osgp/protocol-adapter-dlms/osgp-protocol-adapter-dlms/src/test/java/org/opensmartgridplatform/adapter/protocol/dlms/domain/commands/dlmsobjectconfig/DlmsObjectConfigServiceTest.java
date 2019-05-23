/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsClock;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsProfile;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsRegister;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.Medium;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.ProfileCaptureTime;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.RegisterUnit;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.AttributeAddressAssert;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;

@RunWith(MockitoJUnitRunner.class)
public class DlmsObjectConfigServiceTest {

    private DlmsObjectConfigService service;

    private final DateTime from = DateTime.now().minusDays(1);
    private final DateTime to = DateTime.now();

    private final DlmsDevice device422 = new DlmsDevice();
    private final DlmsDevice device422_noSelectiveAccess = new DlmsDevice();
    private final DlmsDevice device51 = new DlmsDevice();

    private final DlmsClock clock1 = new DlmsClock("0.0.1.0.0.255");
    private final DlmsClock clock2 = new DlmsClock("0.0.2.0.0.255");
    private final DlmsRegister register = new DlmsRegister(DlmsObjectType.ACTIVE_ENERGY_IMPORT, "1.0.1.8.1.255", 0,
            RegisterUnit.WH, Medium.ELECTRICITY);
    private final DlmsRegister registerWithChannel = new DlmsRegister(DlmsObjectType.MBUS_MASTER_VALUE,
            "0.<c>.24.1.255.4", 0, RegisterUnit.WH, Medium.GAS);

    private final List<DlmsCaptureObject> captureObjectsE = Arrays.asList(new DlmsCaptureObject(this.clock1, 2),
            new DlmsCaptureObject(this.register, 2));
    private final DlmsProfile profileE = new DlmsProfile(DlmsObjectType.INTERVAL_VALUES, "1.0.99.1.1.255",
            this.captureObjectsE, ProfileCaptureTime.HOUR, Medium.ELECTRICITY);

    private final List<DlmsCaptureObject> captureObjectsCombined = Arrays.asList(new DlmsCaptureObject(this.clock1, 2),
            new DlmsCaptureObject(this.register, 2), DlmsCaptureObject.createWithChannel(this.registerWithChannel, 1, 2));
    private final DlmsProfile profileCombined = new DlmsProfile(DlmsObjectType.DAILY_LOAD_PROFILE, "1.0.98.1.1.255",
            this.captureObjectsCombined, ProfileCaptureTime.HOUR, Medium.COMBINED);

    private final DlmsHelper dlmsHelper = new DlmsHelper();

    @Mock
    private DlmsObjectConfig config422;

    @Mock
    private DlmsObjectConfig config50;

    @Before
    public void setUp() {
        when(this.config422.contains(Protocol.DSMR_4_2_2)).thenReturn(true);
        when(this.config422.findObject(DlmsObjectType.CLOCK, null)).thenReturn(Optional.of(clock1));
        when(this.config422.findObject(DlmsObjectType.ACTIVE_ENERGY_IMPORT, null)).thenReturn(Optional.of(register));
        when(this.config422.findObject(DlmsObjectType.MBUS_MASTER_VALUE, null)).thenReturn(Optional.of(registerWithChannel));
        when(this.config422.findObject(DlmsObjectType.INTERVAL_VALUES, Medium.ELECTRICITY)).thenReturn(Optional.of(profileE));
        when(this.config422.findObject(DlmsObjectType.DAILY_LOAD_PROFILE, null)).thenReturn(Optional.of(profileCombined));
        when(this.config422.findObject(DlmsObjectType.DAILY_LOAD_PROFILE, Medium.GAS)).thenReturn(Optional.of(profileCombined));
        when(this.config422.findObject(DlmsObjectType.DAILY_LOAD_PROFILE, Medium.ELECTRICITY)).thenReturn(Optional.of(profileCombined));

        when(this.config50.contains(Protocol.SMR_5_1)).thenReturn(true);
        when(this.config50.findObject(DlmsObjectType.AMR_STATUS, null)).thenReturn(Optional.empty());
        when(this.config50.findObject(DlmsObjectType.INTERVAL_VALUES, null)).thenReturn(Optional.empty());
        when(this.config50.findObject(DlmsObjectType.DAILY_LOAD_PROFILE, Medium.GAS)).thenReturn(Optional.of(profileCombined));

        final List<DlmsObjectConfig> configs = Arrays.asList(this.config422, this.config50);

        this.service = new DlmsObjectConfigService(this.dlmsHelper, configs);

        this.device422.setProtocol("DSMR", "4.2.2");
        this.device422.setSelectiveAccessSupported(true);
        this.device422_noSelectiveAccess.setProtocol("DSMR", "4.2.2");
        this.device422_noSelectiveAccess.setSelectiveAccessSupported(false);
        this.device51.setProtocol("SMR", "5.1");
        this.device51.setSelectiveAccessSupported(true);
    }

    @Test
    public void testNoMatchingObject() throws Exception {
        // CALL
        final Optional<AttributeAddress> attributeAddress = this.service.findAttributeAddress(this.device51,
                DlmsObjectType.AMR_STATUS, null);

        // VERIFY
        assertThat(attributeAddress.isPresent()).isFalse();
    }

    @Test
    public void testNoMatchingObjectForProtocol() throws Exception {
        // CALL
        final Optional<AttributeAddress> attributeAddress = this.service.findAttributeAddress(this.device51,
                DlmsObjectType.INTERVAL_VALUES, null);

        // VERIFY
        assertThat(attributeAddress.isPresent()).isFalse();
    }

    @Test
    public void testOneMatchingObject() throws Exception {
        // SETUP
        final AttributeAddress expectedAddress = new AttributeAddress(this.register.getClassId(),
                this.register.getObisCode(), this.register.getDefaultAttributeId(), null);

        // CALL
        final Optional<AttributeAddress> attributeAddress = this.service.findAttributeAddress(this.device422,
                DlmsObjectType.ACTIVE_ENERGY_IMPORT, null);

        // VERIFY
        AttributeAddressAssert.is(attributeAddress.get(), expectedAddress);
    }

    @Test
    public void testOneMatchingObjectWithChannel() throws Exception {
        // SETUP
        final Integer channel = 1;
        final AttributeAddress expectedAddress = new AttributeAddress(this.registerWithChannel.getClassId(),
                this.getObisCodeWithChannel(this.registerWithChannel.getObisCode(), channel),
                this.registerWithChannel.getDefaultAttributeId(), null);

        // CALL
        final Optional<AttributeAddress> attributeAddress = this.service.findAttributeAddress(this.device422,
                DlmsObjectType.MBUS_MASTER_VALUE, channel);

        // VERIFY
        AttributeAddressAssert.is(attributeAddress.get(), expectedAddress);
    }

    @Test
    public void testProfileWithOneMedium() throws Exception {
        // SETUP
        final Integer channel = null;
        final Medium filterMedium = Medium.ELECTRICITY;

        final DataObject selectedValues = DataObject.newArrayData(Collections.emptyList());

        final DataObject accessParams = this.getAccessParams(selectedValues);

        final SelectiveAccessDescription access = new SelectiveAccessDescription(1, accessParams);

        final AttributeAddress expectedAddress = new AttributeAddress(this.profileE.getClassId(),
                this.profileE.getObisCode(), this.profileE.getDefaultAttributeId(), access);

        // CALL
        final Optional<AttributeAddressForProfile> attributeAddressForProfile =
                this.service.findAttributeAddressForProfile(
                this.device422, DlmsObjectType.INTERVAL_VALUES, channel, this.from, this.to, filterMedium);

        // VERIFY
        AttributeAddressAssert.is(attributeAddressForProfile.get().getAttributeAddress(), expectedAddress);
        assertThat(attributeAddressForProfile.get().getSelectedObjects()).isEqualTo(this.captureObjectsE);
    }

    @Test
    public void testProfileWithMediumCombinedAndFilterMedium() throws Exception {
        // SETUP
        final Integer channel = null;
        final Medium filterMedium = Medium.ELECTRICITY;

        final List<DlmsCaptureObject> expectedSelectedObjects = this.captureObjectsCombined.stream()
                .filter(c -> !(c.getRelatedObject() instanceof DlmsRegister)
                        || ((DlmsRegister) c.getRelatedObject()).getMedium() == filterMedium)
                .collect(Collectors.toList());

        final DataObject selectedValues = DataObject.newArrayData(expectedSelectedObjects.stream()
                .map(o -> this.getDataObject(o.getRelatedObject()))
                .collect(Collectors.toList()));

        final DataObject accessParams = this.getAccessParams(selectedValues);

        final SelectiveAccessDescription access = new SelectiveAccessDescription(1, accessParams);

        final AttributeAddress expectedAddress = new AttributeAddress(this.profileCombined.getClassId(),
                this.profileCombined.getObisCode(), this.profileCombined.getDefaultAttributeId(), access);

        // CALL
        final Optional<AttributeAddressForProfile> attributeAddressForProfile =
                this.service.findAttributeAddressForProfile(
                this.device422, DlmsObjectType.DAILY_LOAD_PROFILE, channel, this.from, this.to, filterMedium);

        // VERIFY
        AttributeAddressAssert.is(attributeAddressForProfile.get().getAttributeAddress(), expectedAddress);
        assertThat(attributeAddressForProfile.get().getSelectedObjects()).isEqualTo(expectedSelectedObjects);
    }

    @Test
    public void testProfileWithMediumCombinedAndNoFilterMedium() throws Exception {
        // SETUP
        final Integer channel = 1;
        final Medium filterMedium = null;

        final DataObject selectedValues = DataObject.newArrayData(Collections.emptyList());

        final DataObject accessParams = this.getAccessParams(selectedValues);

        final SelectiveAccessDescription access = new SelectiveAccessDescription(1, accessParams);

        final AttributeAddress expectedAddress = new AttributeAddress(this.profileCombined.getClassId(),
                this.profileCombined.getObisCode(), this.profileCombined.getDefaultAttributeId(), access);

        // CALL
        final Optional<AttributeAddressForProfile> attributeAddressForProfile =
                this.service.findAttributeAddressForProfile(
                this.device422, DlmsObjectType.DAILY_LOAD_PROFILE, channel, this.from, this.to, filterMedium);

        // VERIFY
        AttributeAddressAssert.is(attributeAddressForProfile.get().getAttributeAddress(), expectedAddress);
        assertThat(attributeAddressForProfile.get().getSelectedObjects()).isEqualTo(this.captureObjectsCombined);
    }

    @Test
    public void testProfileWithSelectiveAccessNotSupported() throws Exception {
        // Filtering is not possible when selective access not supported, so all values will be returned.

        // SETUP
        final Integer channel = 1;
        final Medium filterMedium = Medium.GAS;

        // Selective access not supported
        final SelectiveAccessDescription access = null;

        final AttributeAddress expectedAddress = new AttributeAddress(this.profileCombined.getClassId(),
                this.profileCombined.getObisCode(), this.profileCombined.getDefaultAttributeId(), access);

        // CALL
        final Optional<AttributeAddressForProfile> attributeAddressForProfile =
                this.service.findAttributeAddressForProfile(
                this.device422_noSelectiveAccess, DlmsObjectType.DAILY_LOAD_PROFILE, channel, this.from, this.to,
                filterMedium);

        // VERIFY
        AttributeAddressAssert.is(attributeAddressForProfile.get().getAttributeAddress(), expectedAddress);

        // All values should be returned.
        assertThat(attributeAddressForProfile.get().getSelectedObjects()).isEqualTo(this.captureObjectsCombined);
    }

    @Test
    public void testProfileWithSelectingValuesNotSupported() throws Exception {
        // Filtering is not possible when selecting values is not supported, so all values will be returned.

        // SETUP
        final Integer channel = 1;
        final Medium filterMedium = Medium.GAS;

        // Selecting values is not supported
        final DataObject selectedValues = DataObject.newArrayData(Collections.emptyList());

        final DataObject accessParams = this.getAccessParams(selectedValues);

        final SelectiveAccessDescription access = new SelectiveAccessDescription(1, accessParams);

        final AttributeAddress expectedAddress = new AttributeAddress(this.profileCombined.getClassId(),
                this.profileCombined.getObisCode(), this.profileCombined.getDefaultAttributeId(), access);

        // CALL
        final Optional<AttributeAddressForProfile> attributeAddressForProfile =
                this.service.findAttributeAddressForProfile(
                this.device51, DlmsObjectType.DAILY_LOAD_PROFILE, channel, this.from, this.to, filterMedium);

        // VERIFY
        AttributeAddressAssert.is(attributeAddressForProfile.get().getAttributeAddress(), expectedAddress);

        // All values should be returned.
        assertThat(attributeAddressForProfile.get().getSelectedObjects()).isEqualTo(this.captureObjectsCombined);
    }

    private ObisCode getObisCodeWithChannel(final String obisAsString, final Integer channel) {
        String obisWithChannel = obisAsString;

        if (channel != null) {
            obisWithChannel = obisAsString.replace("<c>", channel.toString());
        }

        return new ObisCode(obisWithChannel);
    }

    private DataObject getDataObject(final DlmsObject dlmsObject) {
        return this.getDataObject(dlmsObject, null);
    }

    private DataObject getDataObject(final DlmsObject dlmsObject, final Integer channel) {
        return DataObject.newStructureData(Arrays.asList(DataObject.newUInteger16Data(dlmsObject.getClassId()),
                DataObject.newOctetStringData(this.getObisCodeWithChannel(dlmsObject.getObisCode(), channel).bytes()),
                DataObject.newInteger8Data((byte) dlmsObject.getDefaultAttributeId()),
                DataObject.newUInteger16Data(0)));
    }

    private DataObject getDataObject(final DateTime dateTime) {
        return this.dlmsHelper.asDataObject(dateTime);
    }

    private DataObject getAccessParams(final DataObject selectedValues) {
        return DataObject.newStructureData(Arrays.asList(this.getDataObject(this.clock1), this.getDataObject(this.from),
                this.getDataObject(this.to), selectedValues));
    }
}

