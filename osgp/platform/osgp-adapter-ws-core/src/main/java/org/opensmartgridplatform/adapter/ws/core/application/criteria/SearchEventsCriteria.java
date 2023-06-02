//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.core.application.criteria;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.joda.time.DateTime;
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
  private final DateTime from;
  private final DateTime until;
  private final List<EventType> eventTypes;
  private final String description;
  private final String descriptionStartsWith;
}
