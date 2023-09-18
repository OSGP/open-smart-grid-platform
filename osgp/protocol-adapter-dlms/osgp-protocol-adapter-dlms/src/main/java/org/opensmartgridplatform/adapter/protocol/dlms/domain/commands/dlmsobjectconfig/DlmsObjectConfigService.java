// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.joda.time.DateTime;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.CommunicationMethod;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsProfile;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsRegister;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.Medium;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DlmsObjectConfigService {

  private final DlmsHelper dlmsHelper;
  private final List<DlmsObjectConfig> dlmsObjectConfigs;

  @Autowired
  public DlmsObjectConfigService(
      final DlmsHelper dlmsHelper, final List<DlmsObjectConfig> dlmsObjectConfigs) {
    this.dlmsHelper = dlmsHelper;
    this.dlmsObjectConfigs = dlmsObjectConfigs;
  }

  public AttributeAddress getAttributeAddress(
      final DlmsDevice device, final DlmsObjectType type, final Integer channel)
      throws ProtocolAdapterException {
    // Note: channel can be null.
    final Optional<AttributeAddress> optionalAttributeAddress =
        this.findAttributeAddressForProfile(device, type, channel)
            .map(AttributeAddressForProfile::getAttributeAddress);

    return optionalAttributeAddress.orElseThrow(
        () ->
            new ProtocolAdapterException(
                String.format(
                    "Did not find %s object for device %s for channel %s",
                    type.name(), device.getDeviceId(), channel)));
  }

  public Optional<AttributeAddress> findAttributeAddress(
      final DlmsDevice device, final DlmsObjectType type, final Integer channel) {
    // Note: channel can be null.
    return this.findAttributeAddressForProfile(device, type, channel)
        .map(AttributeAddressForProfile::getAttributeAddress);
  }

  public Optional<AttributeAddressForProfile> findAttributeAddressForProfile(
      final DlmsDevice device,
      final DlmsObjectType type,
      final Integer channel,
      final DateTime from,
      final DateTime to,
      final Medium filterMedium,
      final boolean selectiveAccessSupported) {
    return this.findDlmsObject(Protocol.forDevice(device), type, filterMedium)
        .map(
            dlmsObject ->
                this.getAttributeAddressForProfile(
                    new AddressRequest(device, dlmsObject, channel, from, to, filterMedium),
                    selectiveAccessSupported));
  }

  public Optional<AttributeAddressForProfile> findAttributeAddressForProfile(
      final DlmsDevice device, final DlmsObjectType type, final Integer channel) {
    return this.findDlmsObject(Protocol.forDevice(device), type, null)
        .map(
            dlmsObject ->
                this.getAttributeAddressForProfile(
                    new AddressRequest(device, dlmsObject, channel, null, null, null), true));
  }

  public Optional<DlmsObject> findDlmsObject(
      final Protocol protocol, final DlmsObjectType type, final Medium filterMedium) {
    return this.dlmsObjectConfigs.stream()
        .filter(config -> config.contains(protocol))
        .findAny()
        .flatMap(dlmsObjectConfig -> dlmsObjectConfig.findObject(type, filterMedium));
  }

  public DlmsObject getDlmsObject(final DlmsDevice device, final DlmsObjectType type)
      throws ProtocolAdapterException {
    final Protocol protocol = Protocol.forDevice(device);

    return this.dlmsObjectConfigs.stream()
        .filter(config -> config.contains(protocol))
        .findAny()
        .flatMap(dlmsObjectConfig -> dlmsObjectConfig.findObject(type, null))
        .orElseThrow(
            () ->
                new ProtocolAdapterException(
                    "Did not find " + type.name() + " object for device " + device.getDeviceId()));
  }

  public DlmsObject getDlmsObjectForCommunicationMethod(
      final DlmsDevice device, final DlmsObjectType type) throws ProtocolAdapterException {
    final Protocol protocol = Protocol.forDevice(device);
    final CommunicationMethod method =
        CommunicationMethod.getCommunicationMethod(device.getCommunicationMethod());

    return this.dlmsObjectConfigs.stream()
        .filter(config -> config.contains(protocol))
        .findAny()
        .flatMap(
            dlmsObjectConfig -> dlmsObjectConfig.findObjectForCommunicationMethod(type, method))
        .orElseThrow(
            () ->
                new ProtocolAdapterException(
                    "Did not find "
                        + type.name()
                        + " object with communication method "
                        + method.getMethodName()
                        + " for device "
                        + device.getDeviceId()));
  }

  public List<AttributeAddress> getAttributeAddressesForScalerUnit(
      final AttributeAddressForProfile attributeAddressForProfile, final Integer channel) {
    final List<AttributeAddress> attributeAddresses = new ArrayList<>();

    // Get all Registers from the list of selected objects for which the default attribute is
    // captured.
    final List<DlmsRegister> dlmsRegisters =
        attributeAddressForProfile.getCaptureObjects(DlmsRegister.class, true);

    for (final DlmsRegister register : dlmsRegisters) {
      attributeAddresses.add(
          new AttributeAddress(
              register.getClassId(),
              this.replaceChannel(register.getObisCodeAsString(), channel),
              register.getScalerUnitAttributeId()));
    }

    return attributeAddresses;
  }

  private AttributeAddressForProfile getAttributeAddressForProfile(
      final AddressRequest addressRequest, final boolean selectedValuesSupported) {
    final List<DlmsCaptureObject> selectedObjects =
        this.getSelectedCaptureObjects(addressRequest, selectedValuesSupported);

    final SelectiveAccessDescription access =
        this.getAccessDescription(addressRequest, selectedObjects, selectedValuesSupported);

    final DlmsObject dlmsObject = addressRequest.getDlmsObject();

    final ObisCode obisCode =
        this.replaceChannel(dlmsObject.getObisCodeAsString(), addressRequest.getChannel());

    return new AttributeAddressForProfile(
        new AttributeAddress(
            dlmsObject.getClassId(), obisCode, dlmsObject.getDefaultAttributeId(), access),
        selectedObjects);
  }

  private ObisCode replaceChannel(String obisCode, final Integer channel) {
    if (channel != null) {
      obisCode = obisCode.replace("<c>", channel.toString());
    }

    return new ObisCode(obisCode);
  }

  private SelectiveAccessDescription getAccessDescription(
      final AddressRequest addressRequest,
      final List<DlmsCaptureObject> selectedObjects,
      final boolean selectiveAccessSupported) {

    final DlmsObject object = addressRequest.getDlmsObject();
    final DateTime from = addressRequest.getFrom();
    final DateTime to = addressRequest.getTo();

    if (!(object instanceof DlmsProfile) || from == null || to == null) {
      return null;
    } else {
      final int accessSelector = 1;

      final DataObject selectedValues =
          this.getSelectedValuesObject(addressRequest, selectedObjects);

      final DataObject accessParameter =
          this.dlmsHelper.getAccessSelectionTimeRangeParameter(
              from,
              to,
              selectiveAccessSupported
                  ? selectedValues
                  : DataObject.newArrayData(Collections.emptyList()));

      return new SelectiveAccessDescription(accessSelector, accessParameter);
    }
  }

  private List<DlmsCaptureObject> getSelectedCaptureObjects(
      final AddressRequest addressRequest, final boolean selectedValuesSupported) {
    final DlmsObject object = addressRequest.getDlmsObject();

    if (object instanceof final DlmsProfile profile && profile.getCaptureObjects() != null) {
      if (selectedValuesSupported) {
        return profile.getCaptureObjects().stream()
            .filter(
                o ->
                    o.getRelatedObject().mediumMatches(addressRequest.getFilterMedium())
                        && o.channelMatches(addressRequest.getChannel()))
            .toList();
      } else {
        return profile.getCaptureObjects();
      }
    } else {
      return List.of();
    }
  }

  private DataObject getSelectedValuesObject(
      final AddressRequest addressRequest, final List<DlmsCaptureObject> selectedObjects) {
    if (selectedObjects.size()
        == ((DlmsProfile) addressRequest.getDlmsObject()).getCaptureObjects().size()) {
      // If all capture objects are selected then return an empty list (which means select all)
      return DataObject.newArrayData(List.of());
    } else {
      final List<DataObject> objectDefinitions =
          this.getObjectDefinitions(addressRequest.getChannel(), selectedObjects);

      return DataObject.newArrayData(objectDefinitions);
    }
  }

  private List<DataObject> getObjectDefinitions(
      final Integer channel, final List<DlmsCaptureObject> selectedObjects) {
    final List<DataObject> objectDefinitions = new ArrayList<>();

    for (final DlmsCaptureObject captureObject : selectedObjects) {
      final DlmsObject relatedObject = captureObject.getRelatedObject();

      // Create and add object definition for this capture object
      final ObisCode obisCode = this.replaceChannel(relatedObject.getObisCodeAsString(), channel);
      objectDefinitions.add(
          DataObject.newStructureData(
              Arrays.asList(
                  DataObject.newUInteger16Data(relatedObject.getClassId()),
                  DataObject.newOctetStringData(obisCode.bytes()),
                  DataObject.newInteger8Data((byte) captureObject.getAttributeId()),
                  DataObject.newUInteger16Data(0))));
    }

    return objectDefinitions;
  }
}
