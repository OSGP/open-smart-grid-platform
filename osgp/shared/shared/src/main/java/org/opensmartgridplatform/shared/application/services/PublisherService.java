//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.shared.application.services;

public interface PublisherService<T> {
  public void subscribe(SubscriberService<T> s);

  public void publish(T t);
}
