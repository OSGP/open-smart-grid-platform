// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.infra.jms;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

@FunctionalInterface
public interface MessageProcessor {

  void processMessage(ObjectMessage message) throws JMSException;
}
