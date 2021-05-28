/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.CommunicationMethod;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObjectDependingOnCommunicationMethod;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsProfile;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.Medium;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

public abstract class DlmsObjectConfig {

  private final List<Protocol> protocols;
  private final List<DlmsObject> objects;

  DlmsObjectConfig() {
    this.protocols = this.initProtocols();
    this.objects = this.initObjects();
  }

  public Stream<DlmsObject> getObjects() {
    return this.objects.stream();
  }

  boolean contains(final Protocol protocol) {
    return this.protocols.contains(protocol);
  }

  abstract List<Protocol> initProtocols();

  abstract List<DlmsObject> initObjects();

  /**
   * Searches in the object config for the requested object type matching the specified medium and
   * returns it.
   *
   * @param type the object type to search for
   * @param filterMedium if specified, an object only matches if it has the correct medium
   * @return the requested object or an empty Optional if it was not found.
   */
  public Optional<DlmsObject> findObject(final DlmsObjectType type, final Medium filterMedium) {
    return this.objects.stream()
        .filter(o1 -> o1.getType().equals(type))
        .filter(
            o2 ->
                !(o2 instanceof DlmsProfile)
                    || ((DlmsProfile) o2).getMedium() == Medium.COMBINED
                    || ((DlmsProfile) o2).getMedium() == filterMedium)
        .findAny();
  }

  /**
   * Searches in the object config for the requested object type matching the specified
   * communication method and returns it.
   *
   * @param type the object type to search for
   * @param communicationMethod an object only matches if it has the correct method
   * @return the requested object or an empty Optional if it was not found.
   */
  public Optional<DlmsObject> findObjectForCommunicationMethod(
      final DlmsObjectType type, final CommunicationMethod communicationMethod) {
    return this.objects.stream()
        .filter(o1 -> o1.getType().equals(type))
        .filter(
            o2 ->
                (o2 instanceof DlmsObjectDependingOnCommunicationMethod)
                    && ((DlmsObjectDependingOnCommunicationMethod) o2).getCommunicationMethod()
                        == communicationMethod)
        .findAny();
  }

  /**
   * Searches in the object config for the requested object type and returns its obis code.
   *
   * @param type the object type to search for
   * @return the obiscode of the requested object
   * @throws ProtocolAdapterException when no matching object is found
   */
  public ObisCode getObisForObject(final DlmsObjectType type) throws ProtocolAdapterException {
    return this.getObisForObject(type, null);
  }

  /**
   * Searches in the object config for the requested object type matching the specified medium and
   * returns its obis code.
   *
   * @param type the object type to search for
   * @param filterMedium if specified, an object only matches if it has the correct medium
   * @return the obiscode of the requested object
   * @throws ProtocolAdapterException when no matching object is found
   */
  public ObisCode getObisForObject(final DlmsObjectType type, final Medium filterMedium)
      throws ProtocolAdapterException {
    final Optional<DlmsObject> dlmsObject = this.findObject(type, filterMedium);

    if (dlmsObject.isPresent()) {
      return dlmsObject.get().getObisCode();
    } else {
      throw new ProtocolAdapterException(
          "Dlms object not found in config, type: " + type + ", medium: " + filterMedium);
    }
  }
}
