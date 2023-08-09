// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.core.application.criteria;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.opensmartgridplatform.domain.core.valueobjects.EventType;
import org.opensmartgridplatform.shared.application.config.PageSpecifier;

/** a class to hold the parameters used to search events. */
@Getter
@AllArgsConstructor
@Builder
public class SearchEventsCriteria {
  private final String organisationIdentification;
  private final String deviceIdentification;
  private final PageSpecifier pageSpecifier;
  private final ZonedDateTime from;
  private final ZonedDateTime until;
  private final List<EventType> eventTypes;
  private final String description;
  private final String descriptionStartsWith;
}
