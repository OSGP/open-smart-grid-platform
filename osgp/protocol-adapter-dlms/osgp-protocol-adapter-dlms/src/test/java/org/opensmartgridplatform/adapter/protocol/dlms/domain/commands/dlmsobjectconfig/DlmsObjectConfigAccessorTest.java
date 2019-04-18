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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.AttributeAddressAssert;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;

@RunWith(MockitoJUnitRunner.class)
public class DlmsObjectConfigAccessorTest {

    private DlmsObjectConfigAccessor accessor;
    private DlmsHelper dlmsHelper;

    final DateTime from = DateTime.now().minusDays(1);
    final DateTime to = DateTime.now();

    private final DlmsDevice device51 = new DlmsDevice();
    private final DlmsDevice device51_2 = new DlmsDevice();
    private final DlmsDevice device422 = new DlmsDevice();

    private final DlmsClock clock1 = new DlmsClock(DlmsObjectType.CLOCK, "0.0.1.0.0.255");
    private final DlmsClock clock2 = new DlmsClock(DlmsObjectType.CLOCK, "0.0.2.0.0.255");
    private final DlmsRegister register = new DlmsRegister(DlmsObjectType.ACTIVE_ENERGY_IMPORT, "1.0.1.8.1.255", 0,
            RegisterUnit.WH, Medium.ELECTRICITY);
    private final DlmsRegister registerWithChannel = new DlmsRegister(DlmsObjectType.MBUS_MASTER_VALUE,
            "0.<c>.24.1.255.4", 0, RegisterUnit.WH, Medium.GAS);

    private final List<DlmsCaptureObject> captureObjectsE = Arrays
            .asList(new DlmsCaptureObject(this.clock1, 2), new DlmsCaptureObject(this.register, 2));
    private final DlmsProfile profileE = new DlmsProfile(DlmsObjectType.INTERVAL_VALUES, "1.0.99.1.1.255",
            this.captureObjectsE, ProfileCaptureTime.HOUR, Medium.ELECTRICITY);

    private final List<DlmsCaptureObject> captureObjectsCombined = Arrays
            .asList(new DlmsCaptureObject(this.clock1, 2), new DlmsCaptureObject(this.register, 2),
                    new DlmsCaptureObjectWithChannel(this.registerWithChannel, 1, 2));
    private final DlmsProfile profileCombined = new DlmsProfile(DlmsObjectType.DAILY_LOAD_PROFILE, "1.0.98.1.1.255",
            this.captureObjectsCombined, ProfileCaptureTime.HOUR, Medium.COMBINED);

    @Before
    public void setUp() {
        this.dlmsHelper = new DlmsHelper();
        this.accessor = new DlmsObjectConfigAccessor(this.dlmsHelper);

        this.device51.setProtocol("SMR", "5.1");
        this.device51.setSelectiveAccessSupported(true);
        this.device51_2.setProtocol("SMR", "5.1");
        this.device51_2.setSelectiveAccessSupported(false);
        this.device422.setProtocol("DSMR", "4.2.2");

        final List<Protocol> protocols1 = Collections.singletonList(Protocol.SMR_5_1);
        final List<DlmsObject> objects1 = Arrays
                .asList(this.clock1, this.clock2, this.register, this.registerWithChannel, this.profileE,
                        this.profileCombined);
        final DlmsObjectConfig config1 = new DlmsObjectConfig(protocols1, objects1);

        final List<Protocol> protocols2 = Collections.singletonList(Protocol.SMR_5_1);
        final List<DlmsObject> objects2 = Collections.singletonList(this.clock1);
        final DlmsObjectConfig config2 = new DlmsObjectConfig(protocols2, objects2);

        this.accessor.setConfigs(Arrays.asList(config1, config2));
    }

    @Test
    public void testNoMatchingObject() throws Exception {
        // CALL
        final AttributeAddress attributeAddress = this.accessor
                .getAttributeAddress(this.device51, DlmsObjectType.AMR_STATUS, null);

        // VERIFY
        assertThat(attributeAddress).isEqualTo(null);
    }

    @Test
    public void testNoMatchingObjectForProtocol() throws Exception {
        // CALL
        final AttributeAddress attributeAddress = this.accessor
                .getAttributeAddress(this.device422, DlmsObjectType.ACTIVE_ENERGY_IMPORT, null);

        // VERIFY
        assertThat(attributeAddress).isEqualTo(null);
    }

    @Test
    public void testOneMatchingObject() throws Exception {
        // SETUP
        final AttributeAddress expectedAddress = new AttributeAddress(this.register.getClassId(),
                this.register.getObisCode(), this.register.getDefaultAttributeId(), null);

        // CALL
        final AttributeAddress attributeAddress = this.accessor
                .getAttributeAddress(this.device51, DlmsObjectType.ACTIVE_ENERGY_IMPORT, null);

        // VERIFY
        AttributeAddressAssert.is(attributeAddress, expectedAddress);
    }

    @Test
    public void testOneMatchingObjectWithChannel() throws Exception {
        // SETUP
        final Integer channel = 1;
        final AttributeAddress expectedAddress = new AttributeAddress(this.registerWithChannel.getClassId(),
                this.getObisCodeWithChannel(this.registerWithChannel.getObisCode(), channel),
                this.registerWithChannel.getDefaultAttributeId(), null);

        // CALL
        final AttributeAddress attributeAddress = this.accessor
                .getAttributeAddress(this.device51, DlmsObjectType.MBUS_MASTER_VALUE, channel);

        // VERIFY
        AttributeAddressAssert.is(attributeAddress, expectedAddress);
    }

    @Test
    public void testProfileWithOneMedium() throws Exception {
        // SETUP
        final Integer channel = null;
        final Medium filterMedium = Medium.ELECTRICITY;
        final List<DlmsCaptureObject> selectedObjects = new ArrayList<>();

        final DataObject selectedValues = DataObject.newArrayData(Collections.emptyList());

        final DataObject accessParams = DataObject.newStructureData(
                Arrays.asList(this.getDataObject(this.clock1), this.getDataObject(this.from),
                        this.getDataObject(this.to), selectedValues));

        final SelectiveAccessDescription access = new SelectiveAccessDescription(1, accessParams);

        final AttributeAddress expectedAddress = new AttributeAddress(this.profileE.getClassId(),
                this.profileE.getObisCode(), this.profileE.getDefaultAttributeId(), access);

        // CALL
        final AttributeAddress attributeAddress = this.accessor
                .getAttributeAddress(this.device51, DlmsObjectType.INTERVAL_VALUES, channel, this.from, this.to,
                        filterMedium, selectedObjects);

        // VERIFY
        AttributeAddressAssert.is(attributeAddress, expectedAddress);
        assertThat(selectedObjects).isEqualTo(this.captureObjectsE);
    }

    @Test
    public void testProfileWithMediumCombinedAndFilterMedium() throws Exception {
        // SETUP
        final Integer channel = null;
        final Medium filterMedium = Medium.ELECTRICITY;
        final List<DlmsCaptureObject> selectedObjects = new ArrayList<>();

        final DataObject selectedValues = DataObject.newArrayData(Collections.emptyList());

        final DataObject accessParams = DataObject.newStructureData(
                Arrays.asList(this.getDataObject(this.clock1), this.getDataObject(this.from),
                        this.getDataObject(this.to), selectedValues));

        final SelectiveAccessDescription access = new SelectiveAccessDescription(1, accessParams);

        final AttributeAddress expectedAddress = new AttributeAddress(this.profileCombined.getClassId(),
                this.profileCombined.getObisCode(), this.profileCombined.getDefaultAttributeId(), access);

        final List<DlmsCaptureObject> expectedSelectedObjects = this.captureObjectsCombined.stream()
                .filter(c -> !(c.getRelatedObject() instanceof DlmsRegister)
                        || ((DlmsRegister) c.getRelatedObject()).getMedium() == filterMedium)
                .collect(Collectors.toList());

        // CALL
        final AttributeAddress attributeAddress = this.accessor
                .getAttributeAddress(this.device51, DlmsObjectType.DAILY_LOAD_PROFILE, channel, this.from, this.to,
                        filterMedium, selectedObjects);

        // VERIFY
        AttributeAddressAssert.is(attributeAddress, expectedAddress);
        assertThat(selectedObjects).isEqualTo(expectedSelectedObjects);
    }

    @Test
    public void testProfileWithMediumCombinedAndNoFilterMedium() throws Exception {
        // SETUP
        final Integer channel = 1;
        final Medium filterMedium = null;
        final List<DlmsCaptureObject> selectedObjects = new ArrayList<>();

        final DataObject selectedValues = DataObject.newArrayData(Collections.emptyList());

        final DataObject accessParams = DataObject.newStructureData(
                Arrays.asList(this.getDataObject(this.clock1), this.getDataObject(this.from),
                        this.getDataObject(this.to), selectedValues));

        final SelectiveAccessDescription access = new SelectiveAccessDescription(1, accessParams);

        final AttributeAddress expectedAddress = new AttributeAddress(this.profileCombined.getClassId(),
                this.profileCombined.getObisCode(), this.profileCombined.getDefaultAttributeId(), access);

        // CALL
        final AttributeAddress attributeAddress = this.accessor
                .getAttributeAddress(this.device51, DlmsObjectType.DAILY_LOAD_PROFILE, channel, this.from, this.to,
                        filterMedium, selectedObjects);

        // VERIFY
        AttributeAddressAssert.is(attributeAddress, expectedAddress);
        assertThat(selectedObjects).isEqualTo(this.captureObjectsCombined);
    }

    @Test
    public void testProfileWithSelectiveAccessNotSupported() throws Exception {

    }

    @Test
    public void testProfileWithSelectingValuesNotSupported() throws Exception {

    }

    private ObisCode getObisCodeWithChannel(final String obisAsString, final Integer channel) {
        String obisWithChannel = obisAsString;

        if (channel != null) {
            obisWithChannel = obisAsString.replace("<c>", channel.toString());
        }

        return new ObisCode(obisWithChannel);
    }

    private DataObject getDataObject(final DlmsObject dlmsObject) {
        return DataObject.newStructureData(Arrays.asList(DataObject.newUInteger16Data(dlmsObject.getClassId()),
                DataObject.newOctetStringData(new ObisCode(dlmsObject.getObisCode()).bytes()),
                DataObject.newInteger8Data((byte) dlmsObject.getDefaultAttributeId()),
                DataObject.newUInteger16Data(0)));
    }

    private DataObject getDataObject(final DateTime dateTime) {
        return this.dlmsHelper.asDataObject(dateTime);
    }
}

