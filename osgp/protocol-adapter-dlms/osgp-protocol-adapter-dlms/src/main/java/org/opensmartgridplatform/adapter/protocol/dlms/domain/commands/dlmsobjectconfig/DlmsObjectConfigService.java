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
import java.util.List;
import java.util.Optional;
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
public class DlmsObjectConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPeriodicMeterReadsGasCommandExecutor.class);

    private final DlmsHelper dlmsHelper;
    private final List<DlmsObjectConfig> dlmsObjectConfigs;

    @Autowired
    public DlmsObjectConfigService(final DlmsHelper dlmsHelper, final List<DlmsObjectConfig> dlmsObjectConfigs) {
        this.dlmsHelper = dlmsHelper;
        this.dlmsObjectConfigs = dlmsObjectConfigs;
    }

    public Optional<AttributeAddress> findAttributeAddress(final DlmsDevice device, final DlmsObjectType type,
            final Integer channel) {
        return this.findAttributeAddressForProfile(device, type, channel, null, null, null)
                .map(addressForProfile -> (AttributeAddress) addressForProfile);
    }

    public Optional<AttributeAddressForProfile> findAttributeAddressForProfile(final DlmsDevice device,
            final DlmsObjectType type, final Integer channel, final DateTime from, final DateTime to,
            final Medium filterMedium) {
        return this.findDlmsObject(Protocol.withNameAndVersion(device.getProtocol(), device.getProtocolVersion()), type,
                filterMedium)
                .map(dlmsObject -> this.getAttributeAddressForProfile(dlmsObject, channel, from, to, filterMedium,
                        device));
    }

    private Optional<DlmsObject> findDlmsObject(final Protocol protocol, final DlmsObjectType type,
            final Medium filterMedium) {
        return this.dlmsObjectConfigs.stream()
                .filter(config -> config.contains(protocol))
                .findAny()
                .flatMap(dlmsObjectConfig -> this.findDlmsObject(dlmsObjectConfig, type, filterMedium));
    }

    private Optional<DlmsObject> findDlmsObject(final DlmsObjectConfig dlmsObjectConfig, final DlmsObjectType type,
            final Medium filterMedium) {
        // @formatter:off
        return dlmsObjectConfig.getObjects()
                .filter(o1 -> o1.getType().equals(type))
                .filter(o2 -> !(o2 instanceof DlmsProfile)
                        || ((DlmsProfile) o2).getMedium() == Medium.COMBINED
                        || ((DlmsProfile) o2).getMedium() == filterMedium)
                .findAny();
        // @formatter:on
    }

    public List<AttributeAddress> getAttributeAddressesForScalerUnit(final List<DlmsCaptureObject> selectedObjects,
            final Integer channel) {
        final List<AttributeAddress> attributeAddresses = new ArrayList<>();

        // Get all Registers from the list of selected objects for which the default attribute is captured.
        final List<DlmsRegister> dlmsRegisters = selectedObjects.stream()
                .filter(c -> c.getAttributeId() == c.getRelatedObject().getDefaultAttributeId())
                .map(DlmsCaptureObject::getRelatedObject)
                .filter(r -> r instanceof DlmsRegister)
                .map(r -> (DlmsRegister) r)
                .collect(Collectors.toList());

        for (final DlmsRegister register : dlmsRegisters) {
            attributeAddresses.add(
                    new AttributeAddress(register.getClassId(), this.replaceChannel(register.getObisCode(), channel),
                            register.getScalerUnitAttributeId()));
        }

        return attributeAddresses;
    }

    private AttributeAddressForProfile getAttributeAddressForProfile(final DlmsObject dlmsObject, final Integer channel,
            final DateTime from, final DateTime to, final Medium filterMedium, final DlmsDevice device) {
        final List<DlmsCaptureObject> selectedObjects = new ArrayList<>();

        final SelectiveAccessDescription access = this.getAccessDescription(dlmsObject, from, to, channel, filterMedium,
                device, selectedObjects);

        final ObisCode obisCode = this.replaceChannel(dlmsObject.getObisCode(), channel);

        return new AttributeAddressForProfile(dlmsObject.getClassId(), obisCode, dlmsObject.getDefaultAttributeId(),
                access, selectedObjects);
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

            final DataObject accessParameter = this.dlmsHelper.getAccessSelectionTimeRangeParameter(from, to,
                    selectedValues);

            return new SelectiveAccessDescription(accessSelector, accessParameter);
        }
    }

    private DataObject getSelectedValues(final DlmsObject object, final Integer channel, final Medium filterMedium,
            final Protocol protocol, final List<DlmsCaptureObject> selectedObjects) {
        List<DataObject> objectDefinitions = new ArrayList<>();

        if (object instanceof DlmsProfile && ((DlmsProfile) object).getCaptureObjects() != null) {

            final DlmsProfile profile = (DlmsProfile) object;

            if (!protocol.isSelectValuesInSelectiveAccessSupported()) {
                // If all selecting values is not supported, then all values are selected (and the objectDefinitions
                // list should be empty)
                selectedObjects.addAll(profile.getCaptureObjects());
            } else {
                objectDefinitions = this.getObjectDefinitions(channel, filterMedium, protocol, profile,
                        selectedObjects);
            }
        }

        return DataObject.newArrayData(objectDefinitions);
    }

    private List<DataObject> getObjectDefinitions(final Integer channel, final Medium filterMedium,
            final Protocol protocol, final DlmsProfile profile, final List<DlmsCaptureObject> selectedObjects) {
        final List<DataObject> objectDefinitions = new ArrayList<>();

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

        if (profile.getCaptureObjects().size() == objectDefinitions.size()
                || !protocol.isSelectValuesInSelectiveAccessSupported()) {
            // If all capture objects are selected then return an empty list (which means select all)
            objectDefinitions.clear();
        }

        return objectDefinitions;
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
