// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.config;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ClientSideConfig;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.redisson.cas.RedissonBasedProxyManager;
import java.io.File;
import java.io.IOException;
import java.security.*;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.command.CommandAsyncExecutor;
import org.redisson.config.Config;
import org.redisson.config.ConfigSupport;
import org.redisson.config.SingleServerConfig;
import org.redisson.connection.ConnectionManager;
import org.redisson.liveobject.core.RedissonObjectBuilder;
import org.redisson.liveobject.core.RedissonObjectBuilder.ReferenceType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RedisConfig {

  @Value("${redis.host}")
  private String host;

  @Value("${redis.port}")
  private int port;

  @Value("${redis.ssl}")
  private boolean useSsl;

  @Value("${redis.protocol:TLSv1.2}")
  private String protocol;

  @Value("${redis.password}")
  private String password;

  @Value("${redis.ssl.keystore.location}")
  private String redisKeystoreLocation;

  @Value("${redis.ssl.keystore.password}")
  private String redisKeystorePassword;

  @Value("${redis.ssl.truststore.location}")
  private String redisTruststoreLocation;

  @Value("${redis.ssl.truststore.password}")
  private String redisTruststorePassword;

  @Bean
  public RedissonClient redissonClient(final Config redissonConfig) {
    this.installJCAProvider();
    return Redisson.create(redissonConfig);
  }

  @Bean
  public Config redissonConfig() throws IOException {
    final Config config = new Config();
    final SingleServerConfig singleServerConfig = config.useSingleServer();

    singleServerConfig.setPassword(this.password.isEmpty() ? null : this.password);
    singleServerConfig.setSslEnableEndpointIdentification(false);

    if (this.useSsl) {
      singleServerConfig.setAddress(String.format("rediss://%s:%d", this.host, this.port));
      singleServerConfig.setSslProtocols(this.protocol.split(","));
      singleServerConfig.setSslKeystore(new File(this.redisKeystoreLocation).toURI().toURL());
      singleServerConfig.setSslKeystorePassword(this.redisKeystorePassword);
      singleServerConfig.setSslTruststore(new File(this.redisTruststoreLocation).toURI().toURL());
      singleServerConfig.setSslTruststorePassword(this.redisTruststorePassword);
    } else {
      singleServerConfig.setAddress(String.format("redis://%s:%d", this.host, this.port));
    }

    return config;
  }

  @Bean
  public ProxyManager<String> redissonBasedProxyManager(
      final RedissonClient redissonClient, final Config redissonConfig) {
    final ConnectionManager connectionManager =
        ConfigSupport.createConnectionManager(redissonConfig);
    RedissonObjectBuilder objectBuilder = null;
    if (redissonConfig.isReferenceEnabled()) {
      objectBuilder = new RedissonObjectBuilder(redissonClient);
    }

    final CommandAsyncExecutor commandExecutor =
        connectionManager.createCommandExecutor(objectBuilder, ReferenceType.DEFAULT);

    return RedissonBasedProxyManager.builderFor(commandExecutor)
        .withClientSideConfig(
            ClientSideConfig.getDefault()
                .withExpirationAfterWriteStrategy(
                    ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(
                        Duration.ofSeconds(1L))))
        .build();
  }

  private void installJCAProvider() {
    final BouncyCastleProvider bouncyCastleProvider = new BouncyCastleProvider();

    log.info("About to add Bouncy Castle Provider: {}", bouncyCastleProvider.getInfo());
    Security.addProvider(bouncyCastleProvider);

    for (final Provider provider : Security.getProviders()) {
      log.info("Installed security provider: {}", provider.getInfo());
    }
  }
}
