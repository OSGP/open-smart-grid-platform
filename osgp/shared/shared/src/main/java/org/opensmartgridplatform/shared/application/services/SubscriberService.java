//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.shared.application.services;

public interface SubscriberService<T> {
  void onNext(T t);
}
