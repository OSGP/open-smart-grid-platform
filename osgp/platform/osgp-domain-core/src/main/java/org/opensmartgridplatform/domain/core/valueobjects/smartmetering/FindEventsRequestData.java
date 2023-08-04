// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.time.ZonedDateTime;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;

public class FindEventsRequestData implements Serializable, ActionRequest {

  /** Serial Version UID. */
  private static final long serialVersionUID = 150978792120024431L;

  private final EventLogCategory eventLogCategory;
  private final ZonedDateTime from;
  private final ZonedDateTime until;

  public FindEventsRequestData(
      final EventLogCategory eventLogCategory,
      final ZonedDateTime from,
      final ZonedDateTime until) {
    this.eventLogCategory = eventLogCategory;
    this.from = from;
    this.until = until;
  }

  public EventLogCategory getEventLogCategory() {
    return this.eventLogCategory;
  }

  public ZonedDateTime getFrom() {
    return this.from;
  }

  public ZonedDateTime getUntil() {
    return this.until;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActionValueObject
   * #validate()
   */
  @Override
  public void validate() throws FunctionalException {

    if (!this.from.isBefore(this.until)) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_SMART_METERING,
          new Exception("The 'from' timestamp designates a time after 'until' timestamp."));
    }
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.FIND_EVENTS;
  }
}
