// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.web.api;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import javax.validation.Valid;
import org.opensmartgridplatform.throttling.MaxConcurrencyByThrottlingConfig;
import org.opensmartgridplatform.throttling.PermitsByThrottlingConfig;
import org.opensmartgridplatform.throttling.api.ThrottlingConfig;
import org.opensmartgridplatform.throttling.mapping.ThrottlingMapper;
import org.opensmartgridplatform.throttling.repositories.ThrottlingConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/throttling-configs", produces = "application/json")
public class ThrottlingConfigController {

  private static final int PAGING_MIN_PAGE = 0;
  private static final int PAGING_MIN_SIZE = 1;
  private static final int PAGING_MAX_SIZE = 100;

  private static final Logger LOGGER = LoggerFactory.getLogger(ThrottlingConfigController.class);

  private final Lock throttlingConfigLock = new ReentrantLock();

  private final ThrottlingMapper throttlingMapper;
  private final ThrottlingConfigRepository throttlingConfigRepository;
  private final MaxConcurrencyByThrottlingConfig maxConcurrencyByThrottlingConfig;
  private final PermitsByThrottlingConfig permitsByThrottlingConfig;

  public ThrottlingConfigController(
      final ThrottlingMapper throttlingMapper,
      final ThrottlingConfigRepository throttlingConfigRepository,
      final MaxConcurrencyByThrottlingConfig maxConcurrencyByThrottlingConfig,
      final PermitsByThrottlingConfig permitsByThrottlingConfig) {

    this.throttlingMapper = throttlingMapper;
    this.throttlingConfigRepository = throttlingConfigRepository;
    this.maxConcurrencyByThrottlingConfig = maxConcurrencyByThrottlingConfig;
    this.permitsByThrottlingConfig = permitsByThrottlingConfig;
  }

  @GetMapping
  public ResponseEntity<List<ThrottlingConfig>> throttlingConfigurations(
      @RequestParam(name = "page", required = false, defaultValue = "0") final int page,
      @RequestParam(name = "size", required = false, defaultValue = "10") final int size) {

    if (page < PAGING_MIN_PAGE || size < PAGING_MIN_SIZE) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    final Page<org.opensmartgridplatform.throttling.entities.ThrottlingConfig>
        pageOfThrottlingConfigurations =
            this.throttlingConfigRepository.findAll(
                PageRequest.of(page, Math.min(PAGING_MAX_SIZE, size), Sort.by("id").ascending()));

    final List<ThrottlingConfig> configurations =
        this.throttlingMapper.mapAsList(
            pageOfThrottlingConfigurations.getContent(), ThrottlingConfig.class);

    return ResponseEntity.ok(configurations);
  }

  /**
   * Create or update a throttling configuration identified by its name. If a throttling
   * configuration is already known with this name, the configuration settings (max concurrency)
   * will be updated with the values provided with the {@code throttlingConfig} argument.
   *
   * <p>When the throttling configuration is stored with the throttling service an ID is returned
   * that should be used to identify the throttling configuration in further requests. This allows
   * for the throttling service to be used with different configurations.
   *
   * @param throttlingConfig a throttling configuration to create or update
   * @return the {@code throttlingConfigId} to be provided when requesting or releasing permits for
   *     the {@code throttlingConfig}
   */
  @PostMapping
  public ResponseEntity<Short> createOrUpdateConfig(
      @Valid @RequestBody final ThrottlingConfig throttlingConfig) {

    org.opensmartgridplatform.throttling.entities.ThrottlingConfig throttlingConfigEntity = null;
    this.throttlingConfigLock.lock();
    try {
      throttlingConfigEntity =
          this.throttlingConfigRepository
              .findOneByName(throttlingConfig.getName())
              .orElseGet(this.newThrottlingConfigEntityFromApi(throttlingConfig));

      if (throttlingConfigEntity.getId() == null) {

        throttlingConfigEntity =
            this.throttlingConfigRepository.saveAndFlush(throttlingConfigEntity);
        LOGGER.info("Created new throttling configuration: {}", throttlingConfigEntity);
        this.permitsByThrottlingConfig.newThrottlingConfigCreated(throttlingConfigEntity.getId());

      } else if (this.throttlingConfigNeedsUpdate(throttlingConfigEntity, throttlingConfig)) {

        if (throttlingConfig.getId() != null
            && !throttlingConfig.getId().equals(throttlingConfigEntity.getId())) {
          LOGGER.warn(
              "Updating exisiting throttling configuration with ID {}, ignoring ID from API request: {}",
              throttlingConfigEntity.getId(),
              throttlingConfig.getId());
        }

        final int oldMaxConcurrency = throttlingConfigEntity.getMaxConcurrency();
        this.updateThrottlingConfig(throttlingConfigEntity, throttlingConfig);
        throttlingConfigEntity =
            this.throttlingConfigRepository.saveAndFlush(throttlingConfigEntity);
        LOGGER.info(
            "Updated throttling configuration: {} (previous max concurrency: {})",
            throttlingConfigEntity,
            oldMaxConcurrency);
      }

      this.maxConcurrencyByThrottlingConfig.setMaxConcurrency(
          throttlingConfigEntity.getId(), throttlingConfigEntity.getMaxConcurrency());

    } finally {
      this.throttlingConfigLock.unlock();
    }

    return ResponseEntity.ok(throttlingConfigEntity.getId());
  }

  private boolean throttlingConfigNeedsUpdate(
      final org.opensmartgridplatform.throttling.entities.ThrottlingConfig throttlingConfigEntity,
      final ThrottlingConfig throttlingConfig) {
    return throttlingConfigEntity.getMaxConcurrency() != throttlingConfig.getMaxConcurrency();
  }

  private void updateThrottlingConfig(
      final org.opensmartgridplatform.throttling.entities.ThrottlingConfig throttlingConfigEntity,
      final ThrottlingConfig throttlingConfig) {

    this.throttlingMapper.map(throttlingConfig, throttlingConfigEntity);
  }

  private Supplier<org.opensmartgridplatform.throttling.entities.ThrottlingConfig>
      newThrottlingConfigEntityFromApi(final ThrottlingConfig throttlingConfig) {

    return () -> {
      if (throttlingConfig.getId() != null) {
        LOGGER.warn(
            "Creating new throttling configuration, ignoring ID from API request: {}",
            throttlingConfig.getId());
      }
      return this.throttlingMapper.map(
          throttlingConfig, org.opensmartgridplatform.throttling.entities.ThrottlingConfig.class);
    };
  }
}
