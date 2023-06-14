// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.infra.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

public class MessageListenerContainerRegistry extends Registry<DefaultMessageListenerContainer>
    implements DisposableBean {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(MessageListenerContainerRegistry.class);

  @Override
  protected void preUnregisterAll() {
    this.getValues().forEach(MessageListenerContainerRegistry::stopAndDestroy);
  }

  @Override
  protected void preUnregister(final String key) {
    final DefaultMessageListenerContainer messageListenerContainer = this.getValue(key);
    if (messageListenerContainer != null) {
      LOGGER.info("Stopping and destroying MessageListenerContainer {}", key);
      stopAndDestroy(messageListenerContainer);
    }
  }

  @Override
  public void destroy() {
    this.unregisterAll();
  }

  private static void stopAndDestroy(
      final DefaultMessageListenerContainer messageListenerContainer) {
    messageListenerContainer.stop();
    messageListenerContainer.destroy();
  }
}
