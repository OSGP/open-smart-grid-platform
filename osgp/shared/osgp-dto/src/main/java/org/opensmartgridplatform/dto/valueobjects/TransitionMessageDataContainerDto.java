//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;
import org.joda.time.DateTime;

public class TransitionMessageDataContainerDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 5491018613060059335L;

  private TransitionTypeDto transitionType;
  private DateTime dateTime;

  public TransitionMessageDataContainerDto(
      final TransitionTypeDto transitionType, final DateTime dateTime) {
    this.transitionType = transitionType;
    this.dateTime = dateTime;
  }

  public TransitionTypeDto getTransitionType() {
    return this.transitionType;
  }

  public DateTime getDateTime() {
    return this.dateTime;
  }
}
