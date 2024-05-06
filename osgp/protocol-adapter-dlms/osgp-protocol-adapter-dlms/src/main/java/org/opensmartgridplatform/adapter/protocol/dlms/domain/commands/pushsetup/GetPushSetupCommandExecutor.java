// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.pushsetup;

import static org.opensmartgridplatform.dlms.interfaceclass.attribute.PushSetupAttribute.COMMUNICATION_WINDOW;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.PushSetupAttribute.NUMBER_OF_RETRIES;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.PushSetupAttribute.PUSH_OBJECT_LIST;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.PushSetupAttribute.RANDOMISATION_START_INTERVAL;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.PushSetupAttribute.REPETITION_DELAY;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.PushSetupAttribute.SEND_DESTINATION_AND_METHOD;

import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.NotSupportedByProtocolException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.PushSetupAttribute;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;

public abstract class GetPushSetupCommandExecutor<T, R> extends AbstractCommandExecutor<T, R> {

  PushSetupAttribute[] attributes = {
    PUSH_OBJECT_LIST,
    SEND_DESTINATION_AND_METHOD,
    COMMUNICATION_WINDOW,
    RANDOMISATION_START_INTERVAL,
    NUMBER_OF_RETRIES,
    REPETITION_DELAY
  };
  private final ObjectConfigServiceHelper objectConfigServiceHelper;

  protected GetPushSetupCommandExecutor(final ObjectConfigServiceHelper objectConfigServiceHelper) {
    this.objectConfigServiceHelper = objectConfigServiceHelper;
  }

  protected static void checkResultList(
      final List<GetResult> getResultList, final AttributeAddress[] attributeAddresses)
      throws ProtocolAdapterException {
    if (getResultList.isEmpty()) {
      throw new ProtocolAdapterException(
          "No GetResult received while retrieving Push Setup table.");
    }

    if (getResultList.size() != attributeAddresses.length) {
      throw new ProtocolAdapterException(
          "Expected "
              + attributeAddresses.length
              + " GetResults while retrieving Push Setup table, got "
              + getResultList.size());
    }
  }

  protected AttributeAddress[] getAttributeAddresses(
      final Protocol protocol, final DlmsObjectType dlmsObjectType)
      throws NotSupportedByProtocolException {
    final AttributeAddress[] attributeAddresses = new AttributeAddress[this.attributes.length];
    for (int i = 0; i < this.attributes.length; i++) {
      attributeAddresses[i] =
          this.getAttributeAddress(protocol, dlmsObjectType, this.attributes[i].attributeId());
    }
    return attributeAddresses;
  }

  protected AttributeAddress getAttributeAddress(
      final Protocol protocol, final DlmsObjectType dlmsObjectType, final int attributeId)
      throws NotSupportedByProtocolException {
    return this.objectConfigServiceHelper
        .findOptionalAttributeAddress(protocol, dlmsObjectType, null, attributeId)
        .orElseThrow(
            () ->
                new NotSupportedByProtocolException(
                    String.format(
                        "No address found for %s in protocol %s %s",
                        DlmsObjectType.PUSH_SETUP_SMS.name(),
                        protocol.getName(),
                        protocol.getVersion())));
  }

  protected int idx(final PushSetupAttribute pushSetupAttribute) {
    return ArrayUtils.indexOf(this.attributes, pushSetupAttribute);
  }
}
