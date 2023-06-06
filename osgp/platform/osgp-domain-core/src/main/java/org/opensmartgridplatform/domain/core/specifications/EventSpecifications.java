// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.specifications;

import java.util.Date;
import java.util.List;
import org.opensmartgridplatform.domain.core.entities.Event;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.valueobjects.EventType;
import org.springframework.data.jpa.domain.Specification;

public interface EventSpecifications {
  Specification<Event> isCreatedAfter(final Date dateFrom);

  Specification<Event> isCreatedBefore(final Date dateUntil);

  Specification<Event> isFromDevice(final String deviceIdentification);

  Specification<Event> isAuthorized(final Organisation organisation);

  Specification<Event> hasEventTypes(final List<EventType> eventTypes);

  Specification<Event> withDescription(final String description);

  Specification<Event> startsWithDescription(final String descriptionStartsWith);
}
