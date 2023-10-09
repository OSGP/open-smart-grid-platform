// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig.dlmsClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CaptureObject;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.MeterType;
import org.opensmartgridplatform.dlms.objectconfig.ObjectProperty;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;

@Getter
@NoArgsConstructor
public class ProfileGeneric extends CosemObject {

  public ProfileGeneric(
      final String tag,
      final String description,
      final int classId,
      final int version,
      final String obis,
      final String group,
      final String note,
      final List<MeterType> meterTypes,
      final Map<ObjectProperty, Object> properties,
      final List<Attribute> attributes) {
    super(
        tag, description, classId, version, obis, group, note, meterTypes, properties, attributes);
  }

  public List<CaptureObject> getCaptureObjects(
      final ObjectConfigService service,
      final String protocol,
      final String version,
      final Integer channel)
      throws ObjectConfigException {
    final List<CaptureObject> captureObjects = new ArrayList<>();

    final List<Integer> channels;
    if (this.hasWildcardChannel()) {
      // If the profile has an x for the channel in the obis code, then return all capture objects
      // with the specified channel.
      channels = List.of(channel);
    } else {
      // If the profile has no x for channel, then each capture objects with an x in the config
      // should be handled as 4 different capture objects, one for each channel 1..4.
      channels = List.of(1, 2, 3, 4);
    }

    final List<CaptureObject> captureObjectsWithWildcardChannel = new ArrayList<>();
    final List<CaptureObject> captureObjectsWithoutWildcardChannel = new ArrayList<>();

    final List<CaptureObject> captureObjectsFromConfig =
        service.getCaptureObjects(this, protocol, version);

    for (final CaptureObject captureObject : captureObjectsFromConfig) {
      if (captureObject.getCosemObject().hasWildcardChannel()) {
        captureObjectsWithWildcardChannel.add(captureObject);
      } else {
        captureObjectsWithoutWildcardChannel.add(captureObject);
      }
    }

    captureObjects.addAll(captureObjectsWithoutWildcardChannel);

    captureObjects.addAll(
        channels.stream()
            .map(c -> this.addChannel(captureObjectsWithWildcardChannel, c))
            .flatMap(List::stream)
            .toList());

    return captureObjects;
  }

  private List<CaptureObject> addChannel(
      final List<CaptureObject> captureObjects, final int channel) {
    final List<CaptureObject> captureObjectsWithChannel = new ArrayList<>();

    for (final CaptureObject captureObject : captureObjects) {
      final CosemObject cosemObject = captureObject.getCosemObject();
      captureObjectsWithChannel.add(
          new CaptureObject(
              this.updateCosemObjectWithChannel(cosemObject, channel),
              captureObject.getAttributeId()));
    }

    return captureObjectsWithChannel;
  }

  private CosemObject updateCosemObjectWithChannel(
      final CosemObject cosemObject, final int channel) {
    return cosemObject.copyWithNewObis(cosemObject.getObis().replace("x", String.valueOf(channel)));
  }
}
