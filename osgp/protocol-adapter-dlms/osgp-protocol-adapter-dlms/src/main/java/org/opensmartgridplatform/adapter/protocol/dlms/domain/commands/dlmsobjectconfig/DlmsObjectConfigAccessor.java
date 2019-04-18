/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.GetPeriodicMeterReadsGasCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DlmsObjectConfigAccessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPeriodicMeterReadsGasCommandExecutor.class);

    private final List<DlmsObjectConfig> configs = new ArrayList<>();

    private final DlmsHelper dlmsHelper;

    @Autowired
    public DlmsObjectConfigAccessor(final DlmsHelper dlmsHelper) {
        this.dlmsHelper = dlmsHelper;

        this.configs.add(new DlmsObjectConfigDsmr422(Collections.singletonList(Protocol.OTHER_PROTOCOL)));
        this.configs.add(new DlmsObjectConfigSmr50(Collections.singletonList(Protocol.SMR_5_1)));
    }

    // SetConfigs is only to be used for testing
    void setConfigs(final List<DlmsObjectConfig> configs) {
        this.configs.clear();
        this.configs.addAll(configs);
    }

    public AttributeAddress getAttributeAddress(final DlmsDevice device, final DlmsObjectType type,
            final Integer channel) {
        return this.getAttributeAddress(device, type, channel, null, null, null, null);
    }

    public AttributeAddress getAttributeAddress(final DlmsDevice device, final DlmsObjectType type,
            final Integer channel, final DateTime from, final DateTime to, final Medium filterMedium,
            final List<DlmsCaptureObject> selectedObjects) {
        final List<DlmsObject> objects = this
                .getObjects(Protocol.withNameAndVersion(device.getProtocol(), device.getProtocolVersion()), type,
                        filterMedium);

        if (objects != null && !objects.isEmpty()) {
            final DlmsObject object = objects.get(0);
            final int classId = object.getClassId();
            final ObisCode obisCode = this.replaceChannel(object.getObisCode(), channel);
            final int attributeId = object.getDefaultAttributeId();

            final SelectiveAccessDescription access = this
                    .getAccessDescription(object, from, to, channel, filterMedium, device, selectedObjects);

            return new AttributeAddress(classId, obisCode, attributeId, access);
        } else {
            return null;
        }
    }

    public List<AttributeAddress> getAttributeAddressesForScalerUnit(final List<DlmsCaptureObject> selectedObjects,
            final Integer channel) {
        final List<AttributeAddress> attributeAddresses = new ArrayList<>();

        // Get all Registers from the list of selected objects for which the default attribute is captured.
        final List<DlmsRegister> dlmsRegisters = selectedObjects.stream()
                .filter(c -> c.getAttributeId() == c.getRelatedObject().getDefaultAttributeId())
                .map(DlmsCaptureObject::getRelatedObject).filter(r -> r instanceof DlmsRegister)
                .map(r -> (DlmsRegister) r).collect(Collectors.toList());

        for (final DlmsRegister register : dlmsRegisters) {
            attributeAddresses.add(new AttributeAddress(register.getClassId(),
                    this.replaceChannel(register.getObisCode(), channel), register.getScalerUnitAttributeId()));
        }

        return attributeAddresses;
    }

    private List<DlmsObject> getObjects(final Protocol protocol, final DlmsObjectType type, final Medium filterMedium) {
        final List<DlmsObjectConfig> configsForProtocol = this.configs.stream()
                .filter(c -> c.protocols.contains(protocol)).collect(Collectors.toList());

        if (configsForProtocol.isEmpty()) {
            return Collections.emptyList();
        } else {
            final DlmsObjectConfig objectConfig = configsForProtocol.get(0);

            final List<DlmsObject> objects = objectConfig.getObjects();

            // @formatter:off
           return objects.stream()
                    .filter(i -> i.getType().equals(type))
                    .filter(i -> !(i instanceof DlmsProfile) ||
                            ((DlmsProfile)i).getMedium() == Medium.COMBINED ||
                            ((DlmsProfile)i).getMedium() == filterMedium)
                    .collect(Collectors.toList());
            // @formatter:on
        }
    }

    private ObisCode replaceChannel(String obisCode, final Integer channel) {
        if (channel != null) {
            obisCode = obisCode.replace("<c>", channel.toString());
        }

        return new ObisCode(obisCode);
    }

    private SelectiveAccessDescription getAccessDescription(final DlmsObject object, final DateTime from,
            final DateTime to, final Integer channel, final Medium filterMedium, final DlmsDevice device,
            final List<DlmsCaptureObject> selectedObjects) {
        if (!(object instanceof DlmsProfile) || from == null || to == null) {
            return null;
        } else if (!device.isSelectiveAccessSupported()) {
            LOGGER.info("Device does not support selective access, returning all captureobjects as selected objects");
            selectedObjects.addAll(((DlmsProfile) object).getCaptureObjects());
            return null;
        } else {
            final int accessSelector = 1;

            final DataObject selectedValues = this.getSelectedValues(object, channel, filterMedium,
                    Protocol.withNameAndVersion(device.getProtocol(), device.getProtocolVersion()), selectedObjects);

            final DataObject accessParameter = this.dlmsHelper
                    .getAccessSelectionTimeRangeParameter(from, to, selectedValues);

            return new SelectiveAccessDescription(accessSelector, accessParameter);
        }
    }

    private DataObject getSelectedValues(final DlmsObject object, final Integer channel, final Medium filterMedium,
            final Protocol protocol, final List<DlmsCaptureObject> selectedObjects) {
        final List<DataObject> objectDefinitions = new ArrayList<>();

        if (object instanceof DlmsProfile && ((DlmsProfile) object).getCaptureObjects() != null) {

            final DlmsProfile profile = (DlmsProfile) object;

            for (final DlmsCaptureObject captureObject : profile.getCaptureObjects()) {
                final DlmsObject relatedObject = captureObject.getRelatedObject();

                if (!this.mediumMatches(filterMedium, relatedObject) || !this.channelMatches(channel, captureObject)) {
                    continue;
                }

                // Create and add object definition for this capture object
                final ObisCode obisCode = this.replaceChannel(relatedObject.getObisCode(), channel);
                objectDefinitions.add(DataObject.newStructureData(
                        Arrays.asList(DataObject.newUInteger16Data(relatedObject.getClassId()),
                                DataObject.newOctetStringData(obisCode.bytes()),
                                DataObject.newInteger8Data((byte) captureObject.getAttributeId()),
                                DataObject.newUInteger16Data(0))));

                // Add object to selected object list
                if (selectedObjects != null) {
                    selectedObjects.add(captureObject);
                }
            }

            if (profile.getCaptureObjects().size() == objectDefinitions.size() || !protocol
                    .isSelectValuesInSelectiveAccessSupported()) {
                // If all capture objects are selected or selecting values is not supported, then use an empty list
                // (which means select all)
                objectDefinitions.clear();
            }
        }

        return DataObject.newArrayData(objectDefinitions);
    }

    private boolean mediumMatches(final Medium filterMedium, final DlmsObject object) {
        return filterMedium == null || !(object instanceof DlmsRegister)
                || ((DlmsRegister) object).getMedium() == filterMedium;
    }

    private boolean channelMatches(final Integer channel, final DlmsCaptureObject captureObject) {
        return !(captureObject instanceof DlmsCaptureObjectWithChannel)
                || ((DlmsCaptureObjectWithChannel) captureObject).getChannel() == channel;
    }
}
