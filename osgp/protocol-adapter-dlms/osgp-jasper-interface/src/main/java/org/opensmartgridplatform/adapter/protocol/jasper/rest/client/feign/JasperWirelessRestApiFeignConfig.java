/// *
// * Copyright 2022 Alliander N.V.
// */
//
// package org.opensmartgridplatform.adapter.protocol.jasper.rest.client.feign;
//
// import feign.Client;
// import feign.Feign;
// import feign.Logger;
// import feign.codec.Decoder;
// import feign.codec.Encoder;
// import org.springframework.beans.factory.ObjectFactory;
// import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
// import org.springframework.cloud.openfeign.support.SpringDecoder;
// import org.springframework.cloud.openfeign.support.SpringEncoder;
// import org.springframework.context.annotation.Bean;
//
/// ** Feign managed config file, out of Spring package scan scope. */
// public class JasperWirelessRestApiFeignConfig {
//
//  @Bean
//  Logger.Level feignLoggerLevel() {
//    return Logger.Level.FULL;
//  }
//
//  @Bean
//  public Feign.Builder feignBuilder(final Client feignClient, final Logger.Level feignLoggerLevel)
// {
//    return Feign.builder().logLevel(feignLoggerLevel).client(feignClient);
//  }
//
//  @Bean
//  public Decoder feignDecoder() {
//
//    final ObjectFactory<HttpMessageConverters> messageConverters =
//        () -> {
//          final HttpMessageConverters converters = new HttpMessageConverters();
//          return converters;
//        };
//    return new SpringDecoder(messageConverters);
//  }
//
//  @Bean
//  public Encoder feignEncoder() {
//
//    final ObjectFactory<HttpMessageConverters> messageConverters =
//        () -> {
//          final HttpMessageConverters converters = new HttpMessageConverters();
//          return converters;
//        };
//    return new SpringEncoder(messageConverters);
//  }
// }
