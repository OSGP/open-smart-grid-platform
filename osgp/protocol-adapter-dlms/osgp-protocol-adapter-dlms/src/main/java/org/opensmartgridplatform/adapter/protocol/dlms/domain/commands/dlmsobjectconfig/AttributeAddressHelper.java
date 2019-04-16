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
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttributeAddressHelper {
    private final List<DlmsObjectConfig> configs = new ArrayList<>();

    private final DlmsHelper dlmsHelper;

    @Autowired
    public AttributeAddressHelper(final DlmsHelper dlmsHelper) {
        this.dlmsHelper = dlmsHelper;

        this.configs.add(new DlmsObjectConfigDsmr422(Collections.singletonList(Protocol.OTHER_PROTOCOL)));
        this.configs.add(new DlmsObjectConfigSmr50(Collections.singletonList(Protocol.SMR_5_1)));
    }

    public AttributeAddress getAttributeAddress(final Protocol protocol, final DlmsObjectType type,
            final Integer channel) {
        return this.getAttributeAddress(protocol, type, channel, null, null, null);
    }

    public AttributeAddress getAttributeAddress(final Protocol protocol, final DlmsObjectType type,
            final Integer channel, final DateTime from, final DateTime to, final Medium filterMedium) {
        final List<DlmsObject> objects = this.getObjects(protocol, type);

        if (objects != null && !objects.isEmpty()) {
            final DlmsObject object = objects.get(0);
            final int classId = object.getClassId();
            final ObisCode obisCode = this.replaceChannel(object.getObisCode(), channel);
            final int attributeId = object.getDefaultAttributeId();

            final SelectiveAccessDescription access = this
                    .getAccessDescription(object, from, to, channel, filterMedium, protocol);

            return new AttributeAddress(classId, obisCode, attributeId, access);
        } else {
            return null;
        }
    }

    public List<AttributeAddress> getAttributeAddressWithScalerUnitAddresses(final Protocol protocol,
            final DlmsObjectType type, final Integer channel, final DateTime from, final DateTime to,
            final Medium filterMedium) {

        final List<AttributeAddress> attributeAddressesWithScalerUnitAddresses = new ArrayList<>();

        attributeAddressesWithScalerUnitAddresses
                .add(this.getAttributeAddress(protocol, type, channel, from, to, filterMedium));

        final List<DlmsObject> objects = this.getObjects(protocol, type);

        if (objects != null && !objects.isEmpty()) {
            final DlmsObject object = objects.get(0);
            if (object instanceof DlmsProfile) {
                final DlmsProfile profile = (DlmsProfile) object;

                for (final DlmsCaptureObject captureObject : profile.getCaptureObjects()) {
                    final DlmsObject captureObjectObject = captureObject.getObject();

                    if (captureObjectObject instanceof DlmsRegister) {

                        if (!this.mediumMatches(filterMedium, captureObjectObject) || !this
                                .channelMatches(channel, captureObject)
                                || captureObject.getAttributeId() != captureObjectObject.getDefaultAttributeId()) {
                            continue;
                        }

                        final AttributeAddress attributeAddressForScalerUnit = new AttributeAddress(
                                captureObjectObject.getClassId(),
                                this.replaceChannel(captureObjectObject.getObisCode(), channel),
                                ((DlmsRegister) captureObjectObject).getScalerUnitAttributeId(), null);

                        attributeAddressesWithScalerUnitAddresses.add(attributeAddressForScalerUnit);
                    }
                }
            }
        }

        return attributeAddressesWithScalerUnitAddresses;
    }

    private List<DlmsObject> getObjects(final Protocol protocol, final DlmsObjectType type) {
        final List<DlmsObjectConfig> configsForProtocol = this.configs.stream()
                .filter(c -> c.protocols.contains(protocol)).collect(Collectors.toList());

        if (configsForProtocol.isEmpty()) {
            return Collections.emptyList();
        } else {
            final DlmsObjectConfig objectConfig = configsForProtocol.get(0);

            final List<DlmsObject> objects = objectConfig.getObjects();

            return objects.stream().filter(i -> i.getType().equals(type)).collect(Collectors.toList());
        }
    }

    private ObisCode replaceChannel(String obisCode, final Integer channel) {
        if (channel != null) {
            obisCode = obisCode.replace("<c>", channel.toString());
        }

        return new ObisCode(obisCode);
    }

    private SelectiveAccessDescription getAccessDescription(final DlmsObject object, final DateTime from,
            final DateTime to, final Integer channel, final Medium filterMedium, final Protocol protocol) {
        if (!(object instanceof DlmsProfile) || from == null || to == null) {
            return null;
        } else {
            final int accessSelector = 1;

            final DataObject selectedValues = this.getSelectedValues(object, channel, filterMedium, protocol);

            final DataObject accessParameter = this.dlmsHelper
                    .getAccessSelectionTimeRangeParameter(from, to, selectedValues);

            return new SelectiveAccessDescription(accessSelector, accessParameter);
        }
    }

    private DataObject getSelectedValues(final DlmsObject object, final Integer channel, final Medium filterMedium,
            final Protocol protocol) {
        final List<DataObject> objectDefinitions = new ArrayList<>();

        if (object instanceof DlmsProfile && ((DlmsProfile) object).getCaptureObjects() != null && protocol
                .isSelectValuesInSelectiveAccessSupported()) {

            final DlmsProfile profile = (DlmsProfile) object;

            for (final DlmsCaptureObject captureObject : profile.getCaptureObjects()) {
                final DlmsObject captureObjectObject = captureObject.getObject();

                if (!this.mediumMatches(filterMedium, captureObjectObject) || !this
                        .channelMatches(channel, captureObject)) {
                    continue;
                }

                // Create and add object definition for this capture object
                final ObisCode obisCode = this.replaceChannel(captureObjectObject.getObisCode(), channel);
                objectDefinitions.add(DataObject.newStructureData(
                        Arrays.asList(DataObject.newUInteger16Data(captureObjectObject.getClassId()),
                                DataObject.newOctetStringData(obisCode.bytes()),
                                DataObject.newInteger8Data((byte) captureObject.getAttributeId()),
                                DataObject.newUInteger16Data(0))));
            }

            if (profile.getCaptureObjects().size() == objectDefinitions.size()) {
                // If all capture objects are selected, then use an empty list (which means select all)
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
