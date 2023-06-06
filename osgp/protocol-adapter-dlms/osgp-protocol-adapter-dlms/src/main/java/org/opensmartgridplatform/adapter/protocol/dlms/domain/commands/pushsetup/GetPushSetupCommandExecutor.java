// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.pushsetup;

import java.util.List;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;

public abstract class GetPushSetupCommandExecutor<T, R> extends AbstractCommandExecutor<T, R> {

  protected static final int CLASS_ID = 40;
  protected static final int ATTRIBUTE_ID_PUSH_OBJECT_LIST = 2;
  protected static final int ATTRIBUTE_ID_SEND_DESTINATION_AND_METHOD = 3;
  protected static final int ATTRIBUTE_ID_COMMUNICATION_WINDOW = 4;
  protected static final int ATTRIBUTE_ID_RANDOMISATION_START_INTERVAL = 5;
  protected static final int ATTRIBUTE_ID_NUMBER_OF_RETRIES = 6;
  protected static final int ATTRIBUTE_ID_REPETITION_DELAY = 7;

  protected static final int INDEX_PUSH_OBJECT_LIST = 0;
  protected static final int INDEX_SEND_DESTINATION_AND_METHOD = 1;
  protected static final int INDEX_COMMUNICATION_WINDOW = 2;
  protected static final int INDEX_RANDOMISATION_START_INTERVAL = 3;
  protected static final int INDEX_NUMBER_OF_RETRIES = 4;
  protected static final int INDEX_REPETITION_DELAY = 5;

  protected GetPushSetupCommandExecutor() {
    // hide public constructor, but keep this accessible by subclasses
  }

  protected GetPushSetupCommandExecutor(final Class<? extends ActionRequestDto> clazz) {
    super(clazz);
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
}
