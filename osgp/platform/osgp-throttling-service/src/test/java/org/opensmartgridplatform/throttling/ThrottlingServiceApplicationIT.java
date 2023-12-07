// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.opensmartgridplatform.throttling.api.ThrottlingConfig;
import org.opensmartgridplatform.throttling.entities.BtsCellConfig;
import org.opensmartgridplatform.throttling.mapping.ThrottlingMapper;
import org.opensmartgridplatform.throttling.repositories.BtsCellConfigRepository;
import org.opensmartgridplatform.throttling.repositories.PermitRepository;
import org.opensmartgridplatform.throttling.repositories.ThrottlingConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;

@Slf4j
@SpringBootTest(
    classes = ThrottlingServiceApplication.class,
    webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = ThrottlingServiceApplicationIT.Initializer.class)
class ThrottlingServiceApplicationIT {
  private static final int MAX_WAIT_FOR_HIGH_PRIO = 1000;

  @ClassRule
  private static final PostgreSQLContainer<?> postgreSQLContainer =
      new PostgreSQLContainer<>("postgres:12.4")
          .withDatabaseName("throttling_integration_test_db")
          .withUsername("osp_admin")
          .withPassword("1234")
          .withTmpFs(Collections.singletonMap("/var/lib/postgresql/data", "rw"));

  static class Initializer
      implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(final ConfigurableApplicationContext configurableApplicationContext) {
      TestPropertyValues.of(
              "spring.datasource.driver-class-name=" + postgreSQLContainer.getDriverClassName(),
              "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
              "spring.datasource.username=" + postgreSQLContainer.getUsername(),
              "spring.datasource.password=" + postgreSQLContainer.getPassword(),
              "spring.jpa.show-sql=false",
              "max.wait.for.high.prio.in.ms=" + MAX_WAIT_FOR_HIGH_PRIO)
          .applyTo(configurableApplicationContext.getEnvironment());
    }
  }

  @BeforeAll
  static void beforeAll() {
    postgreSQLContainer.start();
  }

  @AfterAll
  static void afterAll() {
    postgreSQLContainer.stop();
  }

  private static final String EXISTING_THROTTLING_CONFIG_NAME = "pre-added-config";
  private static final int EXISTING_THROTTLING_CONFIG_INITIAL_MAX_CONCURRENCY = 8;

  private static final String PAGING_PARAMETERS = "?page={page}&size={size}";
  private static final String ID_PATH = "/{id}";
  private static final String THROTTLING_CONFIGS_URL = "/throttling-configs";
  private static final String THROTTLING_CONFIGS_PAGE_URL =
      THROTTLING_CONFIGS_URL + PAGING_PARAMETERS;
  private static final String CLIENTS_URL = "/clients";
  private static final String CLIENT_URL = CLIENTS_URL + ID_PATH;
  private static final String PERMITS_URL = "/permits";
  private static final String THROTTLING_AND_CLIENT_PATH = "/{throttlingConfigId}/{clientId}";
  private static final String NETWORK_SEGMENT_PATH = "/{baseTransceiverStationId}/{cellId}";
  private static final String DISCARD_PATH = "/discard/{clientId}/{requestId}";
  private static final String PRIORITY_PARAM = "?priority={priority}";

  private static final String PERMITS_URL_FOR_THROTTLING_AND_CLIENT =
      PERMITS_URL + THROTTLING_AND_CLIENT_PATH;
  private static final String PERMITS_URL_FOR_THROTTLING_AND_CLIENT_FOR_REQUEST =
      PERMITS_URL_FOR_THROTTLING_AND_CLIENT + PRIORITY_PARAM;
  private static final String PERMITS_URL_FOR_THROTTLING_AND_CLIENT_AND_NETWORK_SEGMENT =
      PERMITS_URL_FOR_THROTTLING_AND_CLIENT + NETWORK_SEGMENT_PATH;
  private static final String
      PERMITS_URL_FOR_THROTTLING_AND_CLIENT_AND_NETWORK_SEGMENT_FOR_REQUEST =
          PERMITS_URL_FOR_THROTTLING_AND_CLIENT_AND_NETWORK_SEGMENT + PRIORITY_PARAM;
  private static final String PERMITS_URL_FOR_DISCARD = PERMITS_URL + DISCARD_PATH;

  private static final AtomicInteger requestIdCounter = new AtomicInteger(0);

  private final Random random = new SecureRandom();

  @Autowired private TestRestTemplate testRestTemplate;

  @Autowired private ThrottlingMapper throttlingMapper;

  @Autowired private ThrottlingConfigRepository throttlingConfigRepository;
  @Autowired private BtsCellConfigRepository btsCellConfigRepository;

  @Autowired private PermitRepository permitRepository;

  @Autowired private MaxConcurrencyByThrottlingConfig maxConcurrencyByThrottlingConfig;
  @Autowired private MaxConcurrencyByBtsCellConfig maxConcurrencyByBtsCellConfig;

  @Autowired private PermitsByThrottlingConfig permitsByThrottlingConfig;

  private short existingThrottlingConfigId;
  private final int registeredClientId = 73;

  @BeforeEach
  void beforeEach() {
    this.existingThrottlingConfigId =
        this.throttlingConfigRepository
            .save(
                new org.opensmartgridplatform.throttling.entities.ThrottlingConfig(
                    EXISTING_THROTTLING_CONFIG_NAME,
                    EXISTING_THROTTLING_CONFIG_INITIAL_MAX_CONCURRENCY))
            .getId();
  }

  @AfterEach
  void afterEach() {
    this.permitRepository.deleteAllInBatch();
    this.throttlingConfigRepository.deleteAllInBatch();
    this.btsCellConfigRepository.deleteAllInBatch();
  }

  @Test
  void registerNewConfiguration() {
    final String name = "register-config";
    final int maxConcurrency = 99;

    final short id = this.idForNewThrottlingConfig(name, maxConcurrency);
    this.assertThrottlingConfigEntityExistsWithValues(id, name, maxConcurrency);
  }

  private short idForNewThrottlingConfig(final String name, final int maxConcurrency) {
    final ResponseEntity<Short> responseEntity =
        this.registerThrottlingConfig(name, maxConcurrency);
    return this.validThrottlingConfigId(responseEntity);
  }

  @Test
  void registeringExistingConfigurationUpdatesMaxConcurrency() {
    final String name = EXISTING_THROTTLING_CONFIG_NAME;
    final org.opensmartgridplatform.throttling.entities.ThrottlingConfig throttlingConfigEntity =
        this.findExistingThrottlingConfigByName(name);
    final int updatedMaxConcurrency = throttlingConfigEntity.getMaxConcurrency() + 3;

    final ResponseEntity<Short> responseEntity =
        this.registerThrottlingConfig(name, updatedMaxConcurrency);

    final short id = this.validThrottlingConfigId(responseEntity);
    assertThat(id).isEqualTo(this.existingThrottlingConfigId);
    this.assertThrottlingConfigEntityExistsWithValues(id, name, updatedMaxConcurrency);
  }

  @Test
  void registeringConfigurationWithNegativeMaxConcurrencyIsNotAllowed() {
    final String name = "config-with-negative-max-concurrency";
    final int maxConcurrency = -29;

    final ResponseEntity<Short> responseEntity =
        this.registerThrottlingConfig(name, maxConcurrency);

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(this.throttlingConfigRepository.findOneByName(name)).isEmpty();
  }

  @Test
  void registeredConfigurationsCanBeRetrievedWithPageableRequests() {
    final int numberOfThrottlingConfigs = 4;
    final List<ThrottlingConfig> apiThrottlingConfigs = new ArrayList<>();

    final ThrottlingConfig existingApiThrottlingConfig =
        this.throttlingMapper.map(
            this.findExistingThrottlingConfigByName(EXISTING_THROTTLING_CONFIG_NAME),
            ThrottlingConfig.class);
    apiThrottlingConfigs.add(existingApiThrottlingConfig);

    for (int i = 0; i < numberOfThrottlingConfigs - 1; i++) {
      final ThrottlingConfig addedApiThrottlingConfig =
          this.apiThrottlingConfig("additional-config-" + i, i + 2);
      apiThrottlingConfigs.add(addedApiThrottlingConfig);
      final ResponseEntity<Short> response =
          this.registerThrottlingConfig(addedApiThrottlingConfig);
      final short id = this.validThrottlingConfigId(response);
      addedApiThrottlingConfig.setId(id);
    }

    int page = 0;
    final int size = 3;

    while (page * size < numberOfThrottlingConfigs) {

      final List<ThrottlingConfig> pageOfThrottlingConfigs =
          this.retrievePageOfThrottlingConfigs(page, size);

      assertThat(pageOfThrottlingConfigs)
          .usingFieldByFieldElementComparator()
          .containsExactlyElementsOf(
              apiThrottlingConfigs.subList(
                  page * size, Math.min((page + 1) * size, numberOfThrottlingConfigs)));

      page = page + 1;
    }
  }

  private ThrottlingConfig apiThrottlingConfig(final String name, final int maxConcurrency) {
    return new ThrottlingConfig(name, maxConcurrency);
  }

  private org.opensmartgridplatform.throttling.entities.ThrottlingConfig
      findExistingThrottlingConfigByName(final String name) {

    return this.throttlingConfigRepository
        .findOneByName(name)
        .orElseThrow(
            () -> new AssertionError("Expected ThrottlingConfig entity for name: " + name));
  }

  private ResponseEntity<Short> registerThrottlingConfig(
      final String name, final int maxConcurrency) {

    return this.registerThrottlingConfig(this.apiThrottlingConfig(name, maxConcurrency));
  }

  private ResponseEntity<Short> registerThrottlingConfig(final ThrottlingConfig throttlingConfig) {

    return this.testRestTemplate.postForEntity(
        THROTTLING_CONFIGS_URL, throttlingConfig, Short.class);
  }

  private short validThrottlingConfigId(final ResponseEntity<Short> responseEntity) {
    assertThat(responseEntity.getStatusCode().series()).isEqualTo(HttpStatus.Series.SUCCESSFUL);
    assertThat(responseEntity.getBody()).isNotNull();
    final short id = responseEntity.getBody();
    assertThat(id).isPositive();
    return id;
  }

  private List<ThrottlingConfig> retrievePageOfThrottlingConfigs(final int page, final int size) {
    final ResponseEntity<ThrottlingConfig[]> responseEntity =
        this.testRestTemplate.getForEntity(
            THROTTLING_CONFIGS_PAGE_URL, ThrottlingConfig[].class, page, size);
    assertThat(responseEntity.getStatusCode().series()).isEqualTo(HttpStatus.Series.SUCCESSFUL);
    return Arrays.asList(responseEntity.getBody());
  }

  private void assertThrottlingConfigEntityExistsWithValues(
      final short id, final String name, final int maxConcurrency) {

    final org.opensmartgridplatform.throttling.entities.ThrottlingConfig throttlingConfig =
        this.throttlingConfigRepository
            .findById(id)
            .orElseThrow(
                () -> new AssertionError("Expected ThrottlingConfig entity for ID: " + id));
    assertThat(throttlingConfig.getName()).isEqualTo(name);
    assertThat(throttlingConfig.getMaxConcurrency()).isEqualTo(maxConcurrency);
  }

  @Test
  void registerClient() {

    final ResponseEntity<Integer> responseEntity =
        this.testRestTemplate.postForEntity(CLIENTS_URL, null, Integer.class);

    assertThat(responseEntity.getStatusCode().series()).isEqualTo(HttpStatus.Series.SUCCESSFUL);
    assertThat(responseEntity.getBody()).isNotNull();
    final int id = responseEntity.getBody();
    assertThat(id).isPositive();
  }

  @Test
  void unregisterClient() {
    final ResponseEntity<Void> responseEntity = this.unregisterClient(this.registeredClientId);

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
  }

  @Test
  void unregisterUnknownClient() {
    final int unknownClientId = Integer.MAX_VALUE;

    final ResponseEntity<Void> responseEntity = this.unregisterClient(unknownClientId);

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
  }

  private ResponseEntity<Void> unregisterClient(final int clientId) {
    return this.testRestTemplate.exchange(
        CLIENT_URL, HttpMethod.DELETE, null, Void.class, clientId);
  }

  @Test
  void requestPermitForNetworkSegment() {
    final int baseTransceiverStationId = 98549874;
    final int cellId = 0;
    final int priority = 4;
    this.successfullyRequestPermit(
        this.existingThrottlingConfigId,
        this.registeredClientId,
        baseTransceiverStationId,
        cellId,
        priority);
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1})
  void requestPermitForBtsCell(final int maxConcurrency) {
    this.maxConcurrencyByBtsCellConfig.reset();

    final int baseTransceiverStationId = 123;
    final int cellId = 1;
    final int requestId = 2;
    final int priority = 4;

    this.btsCellConfigRepository.save(
        new BtsCellConfig(baseTransceiverStationId, cellId, maxConcurrency));

    if (maxConcurrency == 0) {
      this.unsuccessfullyRequestPermit(
          this.existingThrottlingConfigId,
          this.registeredClientId,
          baseTransceiverStationId,
          cellId,
          requestId,
          priority);
    } else {
      this.successfullyRequestPermit(
          this.existingThrottlingConfigId,
          this.registeredClientId,
          baseTransceiverStationId,
          cellId,
          priority);
    }
  }

  @Test
  void lowPrioDenyPermit() {
    this.maxConcurrencyByBtsCellConfig.reset();

    final int baseTransceiverStationId = 123;
    final int cellId = 1;
    final int requestId = 2;
    final int maxConcurrency = 1;
    final int priority = 4;

    this.btsCellConfigRepository.save(
        new BtsCellConfig(baseTransceiverStationId, cellId, maxConcurrency));

    this.successfullyRequestPermit(
        this.existingThrottlingConfigId,
        this.registeredClientId,
        baseTransceiverStationId,
        cellId,
        priority);

    this.unsuccessfullyRequestPermit(
        this.existingThrottlingConfigId,
        this.registeredClientId,
        baseTransceiverStationId,
        cellId,
        requestId,
        priority);
  }

  @Test
  void highPrioDenyManyRequests() {
    this.maxConcurrencyByBtsCellConfig.reset();

    final int baseTransceiverStationId = 123;
    final int cellId = 1;
    final int maxConcurrency = 1;
    final int priority = 7;

    this.btsCellConfigRepository.save(
        new BtsCellConfig(baseTransceiverStationId, cellId, maxConcurrency));

    assertThat(this.permitRepository.count()).isZero();

    final int nrOfPermits = 100;
    for (int i = 0; i < nrOfPermits - 1; i++) {
      log.debug("successfullyReleasePermitWithDelay");
      this.successfullyReleasePermitWithDelay(
          this.existingThrottlingConfigId,
          this.registeredClientId,
          baseTransceiverStationId,
          cellId,
          (100 + i * 50));
    }

    for (int i = 0; i < nrOfPermits; i++) {
      this.successfullyRequestPermit(
          this.existingThrottlingConfigId,
          this.registeredClientId,
          baseTransceiverStationId,
          cellId,
          priority);
    }
    // release last permit
    this.successfullyReleasePermit(
        this.existingThrottlingConfigId, this.registeredClientId, baseTransceiverStationId, cellId);

    assertThat(this.permitRepository.count()).isZero();
  }

  @Test
  void highPrioDenyPermit() {
    this.maxConcurrencyByBtsCellConfig.reset();

    final int baseTransceiverStationId = 123;
    final int cellId = 1;
    final int maxConcurrency = 1;
    final int priority = 7;

    this.btsCellConfigRepository.save(
        new BtsCellConfig(baseTransceiverStationId, cellId, maxConcurrency));

    this.successfullyRequestPermit(
        this.existingThrottlingConfigId,
        this.registeredClientId,
        baseTransceiverStationId,
        cellId,
        priority);

    // low prio not possible
    this.unsuccessfullyRequestPermit(
        this.existingThrottlingConfigId,
        this.registeredClientId,
        baseTransceiverStationId,
        cellId,
        requestIdCounter.incrementAndGet(),
        4);

    final int delay = 100;
    final long startTime = System.currentTimeMillis();
    // release after delay
    this.successfullyReleasePermitWithDelay(
        this.existingThrottlingConfigId,
        this.registeredClientId,
        baseTransceiverStationId,
        cellId,
        delay);

    // high prio not possible after delay
    this.successfullyRequestPermit(
        this.existingThrottlingConfigId,
        this.registeredClientId,
        baseTransceiverStationId,
        cellId,
        priority);
    assertThat(System.currentTimeMillis() - startTime).isGreaterThanOrEqualTo(delay);
  }

  @Test
  void highPrioDenyPermitMaxTime() {
    this.maxConcurrencyByBtsCellConfig.reset();

    final int baseTransceiverStationId = 123;
    final int cellId = 1;
    final int maxConcurrency = 1;
    final int priority = 7;

    this.btsCellConfigRepository.save(
        new BtsCellConfig(baseTransceiverStationId, cellId, maxConcurrency));

    this.successfullyRequestPermit(
        this.existingThrottlingConfigId,
        this.registeredClientId,
        baseTransceiverStationId,
        cellId,
        priority);

    final long startTime = System.currentTimeMillis();
    // high prio not possible
    this.unsuccessfullyRequestPermit(
        this.existingThrottlingConfigId,
        this.registeredClientId,
        baseTransceiverStationId,
        cellId,
        requestIdCounter.incrementAndGet(),
        priority);
    assertThat(System.currentTimeMillis() - startTime)
        .isGreaterThanOrEqualTo(MAX_WAIT_FOR_HIGH_PRIO);
  }

  private void successfullyReleasePermitWithDelay(
      final short existingThrottlingConfigId,
      final int registeredClientId,
      final int baseTransceiverStationId,
      final int cellId,
      final long delay) {
    final Timer timer = new Timer();
    final TimerTask task =
        new TimerTask() {
          @Override
          public void run() {
            ThrottlingServiceApplicationIT.this.successfullyReleasePermit(
                existingThrottlingConfigId, registeredClientId, baseTransceiverStationId, cellId);
          }
        }; // creating timer task
    timer.schedule(task, delay); // scheduling the task after the delay
  }

  private void successfullyRequestPermit(
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId,
      final int priority) {

    this.successfullyRequestPermit(
        throttlingConfigId, clientId, baseTransceiverStationId, cellId, null, priority);
  }

  private void successfullyRequestPermit(
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId,
      final Integer requestId,
      final int priority) {

    final ResponseEntity<Integer> responseEntity =
        this.requestPermit(
            throttlingConfigId, clientId, baseTransceiverStationId, cellId, requestId, priority);

    assertThat(responseEntity.getStatusCode().series()).isEqualTo(HttpStatus.Series.SUCCESSFUL);
    assertThat(this.numberOfGrantedPermits(responseEntity)).isOne();
  }

  private void unsuccessfullyRequestPermit(
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId,
      final Integer requestId,
      final int priority) {

    final ResponseEntity<Integer> responseEntity =
        this.requestPermit(
            throttlingConfigId, clientId, baseTransceiverStationId, cellId, requestId, priority);

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(this.numberOfGrantedPermits(responseEntity)).isZero();
  }

  private void nonUniqueRequestIdOnRequestPermit(
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId,
      final Integer requestId,
      final int priority) {

    final ResponseEntity<JsonNode> responseEntity =
        this.testRestTemplate.<JsonNode>postForEntity(
            PERMITS_URL_FOR_THROTTLING_AND_CLIENT_AND_NETWORK_SEGMENT_FOR_REQUEST,
            requestId,
            JsonNode.class,
            throttlingConfigId,
            clientId,
            baseTransceiverStationId,
            cellId,
            priority);

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    final JsonNode errorBody = responseEntity.getBody();
    final ObjectNode expected =
        new ObjectMapper()
            .createObjectNode()
            .put("error", "non-unique-request-id")
            .put("clientId", clientId)
            .put("requestId", requestId);
    assertThat(errorBody).isEqualTo(expected);
  }

  private int numberOfGrantedPermits(final ResponseEntity<Integer> responseEntity) {
    assertThat(responseEntity.getBody()).isNotNull();
    return responseEntity.getBody();
  }

  @Test
  void requestPermitForUnknownNetworkSegment() {

    final int requestId = 5534879;
    final int priority = 4;

    final ResponseEntity<Integer> responseEntity =
        this.requestPermit(
            this.existingThrottlingConfigId, this.registeredClientId, requestId, priority);

    assertThat(responseEntity.getStatusCode().series()).isEqualTo(HttpStatus.Series.SUCCESSFUL);
  }

  @Test
  void requestIdForTheClientMustBeUniqueAcrossPermitRequests() {
    final short throttlingConfigId = this.existingThrottlingConfigId;
    final int clientId = this.registeredClientId;
    final int baseTransceiverStationId1 = 34;
    final int cellId1 = 1;
    final int baseTransceiverStationId2 = 45;
    final int cellId2 = 3;
    final int priority = 4;

    final int reusedRequestId = requestIdCounter.incrementAndGet();

    this.successfullyRequestPermit(
        throttlingConfigId,
        clientId,
        baseTransceiverStationId1,
        cellId1,
        reusedRequestId,
        priority);
    this.nonUniqueRequestIdOnRequestPermit(
        throttlingConfigId,
        clientId,
        baseTransceiverStationId2,
        cellId2,
        reusedRequestId,
        priority);
  }

  @Test
  void atMostMaxConcurrencyPermitsPerNetworkSegmentAreGranted() {

    final int maxConcurrency = 2;
    final short throttlingConfigId =
        this.idForNewThrottlingConfig("at-most-2-concurrent-permits", maxConcurrency);
    final int clientId = this.registeredClientId;

    final int baseTransceiverStationId = 45910;
    final int cellId = 2;
    final int priority = 4;

    this.requestPermitsThatAreGranted(
        throttlingConfigId, clientId, baseTransceiverStationId, cellId, maxConcurrency);

    this.unsuccessfullyRequestPermit(
        throttlingConfigId,
        clientId,
        baseTransceiverStationId,
        cellId,
        requestIdCounter.incrementAndGet(),
        priority);
  }

  private void requestPermitsThatAreGranted(
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId,
      final int numberOfGrantedPermits) {

    for (int i = 0; i < numberOfGrantedPermits; i++) {
      this.successfullyRequestPermit(
          throttlingConfigId,
          clientId,
          baseTransceiverStationId,
          cellId,
          requestIdCounter.incrementAndGet());
    }
  }

  @Test
  void releasePermitForNetworkSegment() {
    final short throttlingConfigId = this.existingThrottlingConfigId;
    final int clientId = this.registeredClientId;
    final int baseTransceiverStationId = 846574;
    final int cellId = 1;
    final int requestId = 299164;
    final int priority = 4;

    final ResponseEntity<Integer> resp =
        this.requestPermit(
            throttlingConfigId, clientId, baseTransceiverStationId, cellId, requestId, priority);

    final ResponseEntity<Void> responseEntity =
        this.releasePermit(
            throttlingConfigId, clientId, baseTransceiverStationId, cellId, requestId);

    assertThat(responseEntity.getStatusCode().series()).isEqualTo(HttpStatus.Series.SUCCESSFUL);
  }

  @Test
  void releasePermitForUnknownNetworkSegment() {
    final short throttlingConfigId = this.existingThrottlingConfigId;
    final int clientId = this.registeredClientId;
    final Integer requestId = null;
    final int priority = 4;

    this.requestPermit(throttlingConfigId, clientId, requestId, priority);

    final ResponseEntity<Void> responseEntity =
        this.releasePermit(throttlingConfigId, clientId, requestId);

    System.out.println(responseEntity);
    assertThat(responseEntity.getStatusCode().series()).isEqualTo(HttpStatus.Series.SUCCESSFUL);
  }

  @Test
  void releasePermitIsNotAllowedWhenThePermitIsNotHeldFromAnEarlierRequest() {
    final int baseTransceiverStationId = 24967;
    final int cellId = 3;

    this.unsuccessfullyReleasePermit(
        this.existingThrottlingConfigId,
        this.registeredClientId,
        baseTransceiverStationId,
        cellId,
        requestIdCounter.incrementAndGet());
  }

  @Test
  void aRequestedPermitIsOnlyReleasedSuccessfullyOnce() {
    final short throttlingConfigId = this.existingThrottlingConfigId;
    final int clientId = this.registeredClientId;
    final int baseTransceiverStationId = 59;
    final int cellId = 1;
    final int priority = 4;

    this.successfullyRequestPermit(
        throttlingConfigId, clientId, baseTransceiverStationId, cellId, priority);
    this.successfullyReleasePermit(throttlingConfigId, clientId, baseTransceiverStationId, cellId);
    this.unsuccessfullyReleasePermit(
        throttlingConfigId, clientId, baseTransceiverStationId, cellId);
  }

  @Test
  void releasePermitAlwaysRemoveDbRecord() {
    final short throttlingConfigId = this.existingThrottlingConfigId;
    final int clientId = this.registeredClientId;
    final int baseTransceiverStationId = 59;
    final int cellId = 1;
    final int requestId = requestIdCounter.incrementAndGet();
    final int priority = 4;
    final double secondsSinceEpoch = System.currentTimeMillis() / 1000.0;

    // First do one successful request/release, so config exist
    this.successfullyRequestPermit(
        throttlingConfigId, clientId, baseTransceiverStationId, cellId, priority);
    this.successfullyReleasePermit(throttlingConfigId, clientId, baseTransceiverStationId, cellId);
    assertThat(this.permitRepository.findAll()).isEmpty();

    // Add permit to repository
    this.permitRepository.storePermit(
        throttlingConfigId,
        clientId,
        baseTransceiverStationId,
        cellId,
        requestId,
        secondsSinceEpoch);

    // The permit should be released with success
    this.successfullyReleasePermit(
        throttlingConfigId, clientId, baseTransceiverStationId, cellId, requestId);

    // The permit should be removed successful from database
    assertThat(this.permitRepository.findByClientIdAndRequestId(clientId, requestId)).isEmpty();
  }

  @Test
  void aPermitThatWasGrantedWhereTheClientMissedTheResponseCanBeDiscarded() {
    final short throttlingConfigId = this.existingThrottlingConfigId;
    final int clientId = this.registeredClientId;
    final int baseTransceiverStationId = 4375;
    final int cellId = 3;
    final int requestId = requestIdCounter.incrementAndGet();
    final int priority = 4;

    this.successfullyRequestPermit(
        throttlingConfigId, clientId, baseTransceiverStationId, cellId, requestId, priority);

    this.discardPermitThatWasGranted(clientId, requestId);

    this.thePermitIsNoLongerHeldInMemoryOrDatabase(
        throttlingConfigId, clientId, baseTransceiverStationId, cellId, requestId);
  }

  private void thePermitIsNoLongerHeldInMemoryOrDatabase(
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId,
      final int requestId) {

    final Map<Short, PermitsPerNetworkSegment> actualPermitsPerNetworkSegmentByConfig =
        this.permitsByThrottlingConfig.permitsPerNetworkSegmentByConfig();

    assertThat(
            actualPermitsPerNetworkSegmentByConfig
                .get(throttlingConfigId)
                .permitsPerNetworkSegment()
                .get(baseTransceiverStationId)
                .get(cellId))
        .isZero();
    assertThat(this.permitRepository.findByClientIdAndRequestId(clientId, requestId)).isEmpty();
  }

  @Test
  void aPermitThatWasDeniedWhereTheClientMissedTheResponseCanBeDiscarded() {
    final int clientId = this.registeredClientId;
    final int requestId = requestIdCounter.incrementAndGet();

    this.discardPermitThatWasNotGranted(clientId, requestId);
  }

  private void successfullyReleasePermit(
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId) {

    this.successfullyReleasePermit(
        throttlingConfigId, clientId, baseTransceiverStationId, cellId, null);
  }

  private void successfullyReleasePermit(
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId,
      final Integer requestId) {

    final ResponseEntity<Void> responseEntity =
        this.releasePermit(
            throttlingConfigId, clientId, baseTransceiverStationId, cellId, requestId);

    if (responseEntity.getStatusCode().series() != HttpStatus.Series.SUCCESSFUL) {
      log.error(responseEntity.toString());
    }
    assertThat(responseEntity.getStatusCode().series()).isEqualTo(HttpStatus.Series.SUCCESSFUL);
  }

  private void unsuccessfullyReleasePermit(
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId) {

    this.unsuccessfullyReleasePermit(
        throttlingConfigId, clientId, baseTransceiverStationId, cellId, null);
  }

  private void unsuccessfullyReleasePermit(
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId,
      final Integer requestId) {

    final ResponseEntity<Void> responseEntity =
        this.releasePermit(
            throttlingConfigId, clientId, baseTransceiverStationId, cellId, requestId);

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  private ResponseEntity<Integer> requestPermit(
      final short throttlingConfigId,
      final int clientId,
      final Integer requestId,
      final int priority) {

    return this.testRestTemplate.postForEntity(
        PERMITS_URL_FOR_THROTTLING_AND_CLIENT_FOR_REQUEST,
        requestId,
        Integer.class,
        throttlingConfigId,
        clientId,
        priority);
  }

  private ResponseEntity<Integer> requestPermit(
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId,
      final Integer requestId,
      final int priority) {

    return this.testRestTemplate.postForEntity(
        PERMITS_URL_FOR_THROTTLING_AND_CLIENT_AND_NETWORK_SEGMENT_FOR_REQUEST,
        requestId,
        Integer.class,
        throttlingConfigId,
        clientId,
        baseTransceiverStationId,
        cellId,
        priority);
  }

  private ResponseEntity<Void> releasePermit(
      final short throttlingConfigId, final int clientId, final Integer requestId) {

    return this.testRestTemplate.exchange(
        PERMITS_URL_FOR_THROTTLING_AND_CLIENT,
        HttpMethod.DELETE,
        new HttpEntity<>(requestId),
        Void.class,
        throttlingConfigId,
        clientId);
  }

  private ResponseEntity<Void> releasePermit(
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId,
      final Integer requestId) {

    return this.testRestTemplate.exchange(
        PERMITS_URL_FOR_THROTTLING_AND_CLIENT_AND_NETWORK_SEGMENT,
        HttpMethod.DELETE,
        new HttpEntity<>(requestId),
        Void.class,
        throttlingConfigId,
        clientId,
        baseTransceiverStationId,
        cellId);
  }

  private void discardPermitThatWasGranted(final int clientId, final int requestId) {

    final ResponseEntity<Void> responseEntity = this.discardPermit(clientId, requestId);
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  private void discardPermitThatWasNotGranted(final int clientId, final int requestId) {

    final ResponseEntity<Void> responseEntity = this.discardPermit(clientId, requestId);
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  private ResponseEntity<Void> discardPermit(final int clientId, final int requestId) {

    return this.testRestTemplate.exchange(
        PERMITS_URL_FOR_DISCARD, HttpMethod.DELETE, null, Void.class, clientId, requestId);
  }

  @Test
  @Sql(scripts = "/max-concurrency-by-throttling-config-initializes-from-database.sql")
  void initializesMaxConcurrencyByThrottlingConfigFromDatabase() {
    this.maxConcurrencyByThrottlingConfig.reset();

    final Map<Short, Integer> expected = new TreeMap<>();
    // values added by the SQL script
    expected.put((short) 1, 3);
    expected.put((short) 2, 7123);
    expected.put((short) 3, 2);
    expected.put((short) 4, 3);
    expected.put((short) 5, 383);
    // value added before each test
    expected.put((short) 6, EXISTING_THROTTLING_CONFIG_INITIAL_MAX_CONCURRENCY);

    final Map<Short, Integer> actualMaxConcurrencyByConfigId =
        this.maxConcurrencyByThrottlingConfig.maxConcurrencyByConfigId();
    assertThat(actualMaxConcurrencyByConfigId).containsExactlyEntriesOf(expected);
  }

  @Test
  @Sql(scripts = "/permits-by-throttling-config-initializes-from-database.sql")
  void initializesPermitsByThrottlingConfigFromDatabase() {
    this.permitsByThrottlingConfig.initialize();

    final Map<Short, PermitsPerNetworkSegment> actualPermitsPerNetworkSegmentByConfig =
        this.permitsByThrottlingConfig.permitsPerNetworkSegmentByConfig();

    // 4 throttling configurations from SQL plus 1 added before each test
    assertThat(actualPermitsPerNetworkSegmentByConfig).hasSize(5);

    final PermitsPerNetworkSegment permitsPerNetworkSegment1 =
        actualPermitsPerNetworkSegmentByConfig.get((short) 1);
    final Map<Integer, Map<Integer, Integer>> permitCountPerNetworkSegment1 =
        permitsPerNetworkSegment1.permitsPerNetworkSegment();
    assertThat(permitCountPerNetworkSegment1).hasSize(3);
    this.assertPermitCount(permitCountPerNetworkSegment1, 1, 1, 3);
    this.assertPermitCount(permitCountPerNetworkSegment1, 27, 2, 1);
    this.assertPermitCount(permitCountPerNetworkSegment1, 27, 3, 4);
    this.assertPermitCount(permitCountPerNetworkSegment1, 92, 2, 2);
    this.assertTotalPermitCount(permitCountPerNetworkSegment1, 10);

    final PermitsPerNetworkSegment permitsPerNetworkSegment2 =
        actualPermitsPerNetworkSegmentByConfig.get((short) 2);
    final Map<Integer, Map<Integer, Integer>> permitCountPerNetworkSegment2 =
        permitsPerNetworkSegment2.permitsPerNetworkSegment();
    assertThat(permitCountPerNetworkSegment2).hasSize(4);
    this.assertPermitCount(permitCountPerNetworkSegment2, -1, -1, 1);
    this.assertPermitCount(permitCountPerNetworkSegment2, 1, 1, 1);
    this.assertPermitCount(permitCountPerNetworkSegment2, 1, 2, 1);
    this.assertPermitCount(permitCountPerNetworkSegment2, 2, 3, 1);
    this.assertPermitCount(permitCountPerNetworkSegment2, 93, 1, 1);
    this.assertTotalPermitCount(permitCountPerNetworkSegment2, 5);

    final PermitsPerNetworkSegment permitsPerNetworkSegment3 =
        actualPermitsPerNetworkSegmentByConfig.get((short) 3);
    final Map<Integer, Map<Integer, Integer>> permitCountPerNetworkSegment3 =
        permitsPerNetworkSegment3.permitsPerNetworkSegment();
    assertThat(permitCountPerNetworkSegment3).hasSize(1);
    this.assertPermitCount(permitCountPerNetworkSegment3, -1, -1, 1);
    this.assertTotalPermitCount(permitCountPerNetworkSegment3, 1);

    final PermitsPerNetworkSegment permitsPerNetworkSegment4 =
        actualPermitsPerNetworkSegmentByConfig.get((short) 4);
    final Map<Integer, Map<Integer, Integer>> permitCountPerNetworkSegment4 =
        permitsPerNetworkSegment4.permitsPerNetworkSegment();
    assertThat(permitCountPerNetworkSegment4).isEmpty();
    this.assertTotalPermitCount(permitCountPerNetworkSegment4, 0);
  }

  private void assertTotalPermitCount(
      final Map<Integer, Map<Integer, Integer>> permitCountPerNetworkSegment,
      final int totalNumberOfPermits) {

    assertThat(
            permitCountPerNetworkSegment.values().stream()
                .flatMap(m -> m.values().stream())
                .mapToInt(Integer::valueOf)
                .sum())
        .isEqualTo(totalNumberOfPermits);
  }

  private void assertPermitCount(
      final Map<Integer, Map<Integer, Integer>> permitCountPerNetworkSegment,
      final int baseTransceiverStationId,
      final int cellId,
      final int numberOfPermits) {

    assertThat(permitCountPerNetworkSegment)
        .as("baseTransceiverStationId %d", baseTransceiverStationId)
        .containsKey(baseTransceiverStationId);
    final Map<Integer, Integer> permitCountPerCell =
        permitCountPerNetworkSegment.get(baseTransceiverStationId);
    assertThat(permitCountPerCell)
        .as("bts_id %d, cellId %d, count %d", baseTransceiverStationId, cellId, numberOfPermits)
        .containsEntry(cellId, numberOfPermits);
  }

  @Test
  void multipleWorkersPerformNetworkTasksObservingThrottlingConstraints() {

    final String throttlingIdentity = "shared-throttling-used-by-multiple-workers";
    final int maxConcurrency = 3;
    final int numberOfWorkers = 50;
    final int numberOfNetworkTasks = 2000;
    final int maxTaskDurationMillis = 20;
    final int minimumBaseTransceiverStationId = 1;
    final int maximumBaseTransceiverStationId = 4;
    final int minimumCellId = 1;
    final int maximumCellId = 2;
    final int priority = 4;
    final FakeConcurrencyRestrictedNetwork network =
        new FakeConcurrencyRestrictedNetwork(maxConcurrency);
    final List<NetworkTask> networkTasks =
        this.createNetworkTasks(
            numberOfNetworkTasks,
            maxTaskDurationMillis,
            minimumBaseTransceiverStationId,
            maximumBaseTransceiverStationId,
            minimumCellId,
            maximumCellId,
            priority);
    final NetworkTaskQueue networkTaskQueue = new NetworkTaskQueue();
    for (final NetworkTask networkTask : networkTasks) {
      networkTaskQueue.add(networkTask);
    }

    for (int i = 0; i < numberOfWorkers; i++) {
      final NetworkUser networkUser =
          new NetworkUser(
              throttlingIdentity,
              maxConcurrency,
              network,
              this.testRestTemplate.getRestTemplate(),
              networkTaskQueue);
      networkUser.processTasksInQueue();
    }

    int remainingNetworkTasks = networkTaskQueue.remainingNetworkTasks();
    while (remainingNetworkTasks > 0) {
      this.sleep(100 * maxTaskDurationMillis);
      final int nextRemainingNetworkTasks = networkTaskQueue.remainingNetworkTasks();
      assertThat(nextRemainingNetworkTasks)
          .as("No progress was made processing tasks")
          .isLessThan(remainingNetworkTasks);
      remainingNetworkTasks = nextRemainingNetworkTasks;
    }

    final long unfinishedTasks =
        networkTasks.stream().filter(networkTask -> !networkTask.finished).count();

    final long tasksWithAnException =
        networkTasks.stream().filter(networkTask -> networkTask.throwable != null).count();

    networkTasks.stream()
        .filter(networkTask -> networkTask.throwable != null)
        .findFirst()
        .ifPresent(
            networkTaskWithThrowable -> {
              throw new AssertionError(
                  String.format(
                      "%d unfinished tasks, where %d tasks have a caught exception, using first exception as cause",
                      unfinishedTasks, tasksWithAnException),
                  networkTaskWithThrowable.throwable);
            });
    assertThat(unfinishedTasks).isZero();
  }

  private void sleep(final int millis) {
    try {
      TimeUnit.MILLISECONDS.sleep(millis);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private List<NetworkTask> createNetworkTasks(
      final int numberOfNetworkTasks,
      final int maxDurationInMillis,
      final int minimumBaseTransceiverStationId,
      final int maximumBaseTransceiverStationId,
      final int minimumCellId,
      final int maximumCellId,
      final int priority) {

    final List<NetworkTask> networkTasks = new ArrayList<>(numberOfNetworkTasks);
    for (int i = 0; i < numberOfNetworkTasks; i++) {
      networkTasks.add(
          new NetworkTask(
              minimumBaseTransceiverStationId
                  + this.random.nextInt(
                      1 + maximumBaseTransceiverStationId - minimumBaseTransceiverStationId),
              minimumCellId + this.random.nextInt(1 + maximumCellId - minimumCellId),
              priority,
              maxDurationInMillis));
    }
    return networkTasks;
  }
}
