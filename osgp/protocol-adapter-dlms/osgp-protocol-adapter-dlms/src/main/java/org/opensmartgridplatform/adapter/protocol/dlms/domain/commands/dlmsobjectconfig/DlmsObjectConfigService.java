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

import org.joda.time.DateTime;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsProfile;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsRegister;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.Medium;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DlmsObjectConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DlmsObjectConfigService.class);

    private final DlmsHelper dlmsHelper;
    private final List<DlmsObjectConfig> dlmsObjectConfigs;

    @Autowired
    public DlmsObjectConfigService(final DlmsHelper dlmsHelper, final List<DlmsObjectConfig> dlmsObjectConfigs) {
        this.dlmsHelper = dlmsHelper;
        this.dlmsObjectConfigs = dlmsObjectConfigs;
    }

    public Optional<AttributeAddress> findAttributeAddress(final DlmsDevice device, final DlmsObjectType type,
            final Integer channel) {
        // Note: channel can be null.
        return this.findAttributeAddressForProfile(device, type, channel, null, null, null)
                .map(AttributeAddressForProfile::getAttributeAddress);
    }

    public Optional<AttributeAddressForProfile> findAttributeAddressForProfile(final DlmsDevice device,
            final DlmsObjectType type, final Integer channel, final DateTime from, final DateTime to,
            final Medium filterMedium) {
        return this.findDlmsObject(Protocol.forDevice(device), type,
                filterMedium)
                .map(dlmsObject -> this.getAttributeAddressForProfile(new AddressRequest(device, dlmsObject, channel,
                        from, to, filterMedium)));
    }

    public Optional<DlmsObject> findDlmsObject(final Protocol protocol, final DlmsObjectType type,
            final Medium filterMedium) {
        return this.dlmsObjectConfigs.stream()
                .filter(config -> config.contains(protocol))
                .findAny()
                .flatMap(dlmsObjectConfig -> dlmsObjectConfig.findObject(type, filterMedium));
    }

    public List<AttributeAddress> getAttributeAddressesForScalerUnit(final AttributeAddressForProfile attributeAddressForProfile,
            final Integer channel) {
        final List<AttributeAddress> attributeAddresses = new ArrayList<>();

        // Get all Registers from the list of selected objects for which the default attribute is captured.
        final List<DlmsRegister> dlmsRegisters = attributeAddressForProfile.getCaptureObjects(DlmsRegister.class, true);

        for (final DlmsRegister register : dlmsRegisters) {
            attributeAddresses.add(
                    new AttributeAddress(register.getClassId(), this.replaceChannel(register.getObisCodeAsString(), channel),
                            register.getScalerUnitAttributeId()));
        }

        return attributeAddresses;
    }

    private AttributeAddressForProfile getAttributeAddressForProfile(final AddressRequest addressRequest) {
        final List<DlmsCaptureObject> selectedObjects = new ArrayList<>();

        final SelectiveAccessDescription access = this.getAccessDescription(addressRequest, selectedObjects);

        final DlmsObject dlmsObject = addressRequest.getDlmsObject();

        final ObisCode obisCode = this.replaceChannel(dlmsObject.getObisCodeAsString(), addressRequest.getChannel());

        return new AttributeAddressForProfile(new AttributeAddress(dlmsObject.getClassId(), obisCode,
                dlmsObject.getDefaultAttributeId(), access), selectedObjects);
    }

    private ObisCode replaceChannel(String obisCode, final Integer channel) {
        if (channel != null) {
            obisCode = obisCode.replace("<c>", channel.toString());
        }

        return new ObisCode(obisCode);
    }

    private SelectiveAccessDescription getAccessDescription(final AddressRequest addressRequest,
            final List<DlmsCaptureObject> selectedObjects) {

        final DlmsObject object = addressRequest.getDlmsObject();
        final DateTime from = addressRequest.getFrom();
        final DateTime to = addressRequest.getTo();
        final DlmsDevice device = addressRequest.getDevice();

        if (!(object instanceof DlmsProfile) || from == null || to == null) {
            return null;
        } else if (!device.isSelectiveAccessSupported()) {
            LOGGER.info("Device does not support selective access, returning all captureobjects as selected objects");
            selectedObjects.addAll(((DlmsProfile) object).getCaptureObjects());
            return null;
        } else {
            final int accessSelector = 1;

            final DataObject selectedValues = this.getSelectedValues(addressRequest, selectedObjects);

            final DataObject accessParameter = this.dlmsHelper.getAccessSelectionTimeRangeParameter(from, to,
                    selectedValues);

            return new SelectiveAccessDescription(accessSelector, accessParameter);
        }
    }

    private DataObject getSelectedValues(final AddressRequest addressRequest, final List<DlmsCaptureObject> selectedObjects) {
        List<DataObject> objectDefinitions = new ArrayList<>();

        final DlmsObject object = addressRequest.getDlmsObject();
        final Protocol protocol = Protocol.forDevice(addressRequest.getDevice());

        if (object instanceof DlmsProfile && ((DlmsProfile) object).getCaptureObjects() != null) {

            final DlmsProfile profile = (DlmsProfile) object;

            if (!protocol.isSelectValuesInSelectiveAccessSupported()) {
                // If selecting values is not supported, then all values are selected (and the objectDefinitions list
                // should be empty)
                selectedObjects.addAll(profile.getCaptureObjects());
            } else {
                objectDefinitions = this.getObjectDefinitions(addressRequest.getChannel(),
                        addressRequest.getFilterMedium(), protocol, profile, selectedObjects);
            }
        }

        return DataObject.newArrayData(objectDefinitions);
    }

    private List<DataObject> getObjectDefinitions(final Integer channel, final Medium filterMedium,
            final Protocol protocol, final DlmsProfile profile, final List<DlmsCaptureObject> selectedObjects) {
        final List<DataObject> objectDefinitions = new ArrayList<>();

        for (final DlmsCaptureObject captureObject : profile.getCaptureObjects()) {
            final DlmsObject relatedObject = captureObject.getRelatedObject();

            if (!relatedObject.mediumMatches(filterMedium) || !captureObject.channelMatches(channel)) {
                continue;
            }

            // Create and add object definition for this capture object
            final ObisCode obisCode = this.replaceChannel(relatedObject.getObisCodeAsString(), channel);
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
}
