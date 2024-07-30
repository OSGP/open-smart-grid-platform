// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.config;

import io.github.bucket4j.BandwidthBuilder;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ClientSideConfig;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.jedis.cas.JedisBasedProxyManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import javax.net.ssl.SSLSocketFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import redis.clients.jedis.*;
import redis.clients.jedis.DefaultJedisClientConfig.Builder;

@Slf4j
@Configuration
public class RedisConfig {

  @Value("${redis.host}")
  private String host;

  @Value("${redis.port}")
  private int port;

  @Value("${redis.cluster}")
  private boolean cluster;

  @Value("${redis.ssl}")
  private boolean useSsl;

  @Value("${redis.key.location}")
  private String keyLocation;

  @Value("${redis.crt.location}")
  private String crtLocation;

  @Value("${redis.ca.location}")
  private String caLocation;

  @Value("${redis.protocol:TLSv1.2}")
  private String protocol;

  @Value("${redis.password}")
  private String password;

  @Value("${spring.redis.redisson.file:classpath:redisson-config.yaml}")
  private String redissonConfigFile;

  @Value("${redisson.tmp.keystore.location:temp/keystore.jks}")
  private String redissonTmpKeystoreLocation;

  @Value("${redisson.tmp.truststore.location:temp/truststore.jks}")
  private String redissonTmpTruststoreLocation;

  @Bean
  public RedissonClient redissonClient() throws IOException, GeneralSecurityException {
    this.installJCAProvider();

    final KeyStore keyStore = this.loadKeyStore();
    this.writeToFile(keyStore, this.redissonTmpKeystoreLocation);

    final File caFile = new File(this.caLocation);
    final KeyStore trustStore = KeystoreBuilder.loadTrustStore(caFile);
    this.writeToFile(trustStore, this.redissonTmpTruststoreLocation);

    final Config config = Config.fromYAML(ResourceUtils.getFile(this.redissonConfigFile));
    return Redisson.create(config);
  }

  @Bean
  public UnifiedJedis jedisConnection() {

    final HostAndPort hostAndPort = new HostAndPort(this.host, this.port);
    final Builder redisConfigBuilder = DefaultJedisClientConfig.builder().ssl(this.useSsl);
    if (!StringUtils.isEmpty(this.password)) {
      redisConfigBuilder.password(this.password);
    }

    try {
      final KeyStore keyStore = this.loadKeyStore();

      final File caFile = new File(this.caLocation);
      final KeyStore trustStore = KeystoreBuilder.loadTrustStore(caFile);

      final SSLSocketFactory sslFactory =
          KeystoreBuilder.createSslSocketFactory(keyStore, trustStore, this.protocol);
      redisConfigBuilder.sslSocketFactory(sslFactory);

    } catch (final UnrecoverableKeyException e) {
      throw new IllegalArgumentException(
          "Redis SSLSocketFactory configuration unsuccessful, keystore UnrecoverableKeyException problem.",
          e);
    } catch (final GeneralSecurityException e) {
      throw new IllegalArgumentException(
          "Redis SSLSocketFactory configuration unsuccessful, keystore GeneralSecurityException problem.",
          e);
    } catch (final IOException e) {
      throw new IllegalArgumentException(
          "Redis SSLSocketFactory configuration unsuccessful, keystore IOException problem.", e);
    }

    final JedisClientConfig clientConfig = redisConfigBuilder.build();

    if (this.cluster) {
      final Set<HostAndPort> jedisClusterNodes = new HashSet<>();
      jedisClusterNodes.add(hostAndPort);
      return new JedisCluster(jedisClusterNodes, clientConfig);
    } else {
      return new JedisPooled(hostAndPort, clientConfig);
    }
  }

  @Bean
  public ProxyManager<byte[]> jedisBasedProxyManager(final UnifiedJedis jedisConnection) {
    return JedisBasedProxyManager.builderFor(jedisConnection)
        .withClientSideConfig(
            ClientSideConfig.getDefault()
                .withExpirationAfterWriteStrategy(
                    ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(
                        Duration.ofSeconds(1L))))
        .build();
  }

  @Bean
  public Supplier<BucketConfiguration> bucketConfiguration() {
    return () ->
        BucketConfiguration.builder()
            .addLimit(
                BandwidthBuilder.builder()
                    .capacity(200)
                    .refillGreedy(200, Duration.ofSeconds(1L))
                    .build())
            .build();
  }

  private KeyStore loadKeyStore() throws GeneralSecurityException, IOException {
    if (!StringUtils.isEmpty(this.crtLocation) && !StringUtils.isEmpty(this.keyLocation)) {
      final File crtFile = new File(this.crtLocation);
      final File keyFile = new File(this.keyLocation);

      return KeystoreBuilder.loadKeyStore(crtFile, keyFile);
    } else {
      final KeyStore keyStore = KeyStore.getInstance("JKS");
      keyStore.load(null, null);
      return keyStore;
    }
  }

  private void installJCAProvider() {
    final BouncyCastleProvider bouncyCastleProvider = new BouncyCastleProvider();

    log.info("About to add Bouncy Castle Provider: {}", bouncyCastleProvider.getInfo());
    Security.addProvider(bouncyCastleProvider);

    for (final Provider provider : Security.getProviders()) {
      log.info("Installed security provider: {}", provider.getInfo());
    }
  }

  private void writeToFile(final KeyStore keyStore, final String keystoreLocation)
      throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
    log.info("Write keystore to file: {}", new File(keystoreLocation).getAbsolutePath());
    try (final FileOutputStream fos = new FileOutputStream(keystoreLocation)) {
      keyStore.store(fos, "".toCharArray());
    }
  }
}
