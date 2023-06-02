//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.mqtt.application.services;

public interface MessageHandler {

  void handlePublishedMessage(String topic, byte[] payload);
}
