// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import org.openmuc.jdlms.RawMessageListener;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

public interface DlmsMessageListener extends RawMessageListener {

  void setMessageMetadata(MessageMetadata messageMetadata);

  void setDescription(String description);
}
