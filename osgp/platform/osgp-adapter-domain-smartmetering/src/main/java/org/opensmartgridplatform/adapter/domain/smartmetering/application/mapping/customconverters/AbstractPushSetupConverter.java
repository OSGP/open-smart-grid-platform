// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import ma.glasnost.orika.CustomConverter;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AbstractPushSetup;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AbstractPushSetupDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemObisCodeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemObjectDefinitionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SendDestinationAndMethodDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.WindowElementDto;

/*
 * squid:S2160 Subclasses that add fields should override "equals"
 *
 * Following the advice for this rule would violate the Liskov substitution principle.
 */
@SuppressWarnings("squid:S2160")
public abstract class AbstractPushSetupConverter<S, D> extends CustomConverter<S, D> {

  private final ConfigurationMapper configurationMapper;

  protected AbstractPushSetupConverter(final ConfigurationMapper configurationMapper) {
    this.configurationMapper = configurationMapper;
  }

  /**
   * Converts a push setup implementation to its corresponding DTO.
   *
   * <p><strong>NB</strong> Classes extending this abstract converter should implement {@link
   * #newBuilder()} to return a builder appropriate to the specific push setup type to be converted.
   *
   * @param pushSetup the specific push setup to be converted
   * @return the DTO corresponding to the given {@code pushSetup}
   */
  protected AbstractPushSetupDto convert(final AbstractPushSetup pushSetup) {

    if (pushSetup == null) {
      return null;
    }

    final AbstractPushSetupDto.AbstractBuilder<?> builder = this.newBuilder();
    this.configureBuilder(builder, pushSetup);
    return builder.build();
  }

  /**
   * Returns a builder that is used to create the correct type of PushSetup DTO from {@link
   * #convert(AbstractPushSetup)}.
   *
   * @return a new builder appropriate for the specific PushSetup type
   */
  protected abstract <T extends AbstractPushSetupDto.AbstractBuilder<T>>
      AbstractPushSetupDto.AbstractBuilder<T> newBuilder();

  private void configureBuilder(
      final AbstractPushSetupDto.AbstractBuilder<?> builder, final AbstractPushSetup pushSetup) {

    if (pushSetup.getCommunicationWindow() != null) {
      builder.withCommunicationWindow(
          this.configurationMapper.mapAsList(
              pushSetup.getCommunicationWindow(), WindowElementDto.class));
    }
    builder.withLogicalName(
        this.configurationMapper.map(pushSetup.getLogicalName(), CosemObisCodeDto.class));
    builder.withNumberOfRetries(pushSetup.getNumberOfRetries());
    if (pushSetup.getPushObjectList() != null) {
      builder.withPushObjectList(
          this.configurationMapper.mapAsList(
              pushSetup.getPushObjectList(), CosemObjectDefinitionDto.class));
    }
    builder.withRandomisationStartInterval(pushSetup.getRandomisationStartInterval());
    builder.withRepetitionDelay(pushSetup.getRepetitionDelay());
    builder.withSendDestinationAndMethod(
        this.configurationMapper.map(
            pushSetup.getSendDestinationAndMethod(), SendDestinationAndMethodDto.class));
  }
}
