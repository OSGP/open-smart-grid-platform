/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;

import redis.clients.jedis.JedisPoolConfig;

import com.alliander.osgp.adapter.ws.smartmetering.redis.AddMeterPublisher;

/**
 * @author OSGP
 *
 */
@Configuration
// @EnableRedisHttpSession
@PropertySource("file:${osp/osgpAdapterWsSmartMetering/config}")
@EnableScheduling
public class RedisConfig {

    private static final String PROPERTY_NAME_REDIS_HOST = "redis.host";
    private static final String PROPERTY_NAME_REDIS_PORT = "redis.port";

    private static final String PROPERTY_NAME_REDIS_ADDMETER_TOPIC = "redis.addmeter.topic";

    @Resource
    private Environment environment;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        final JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(new JedisPoolConfig());
        jedisConnectionFactory.setHostName(this.environment.getRequiredProperty(PROPERTY_NAME_REDIS_HOST));
        jedisConnectionFactory
        .setPort(Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_REDIS_PORT)));
        return jedisConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(this.jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericToStringSerializer<Object>(Object.class));
        template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
        return template;
    }

    @Bean
    public AddMeterPublisher addMeterPublisher() {
        return new AddMeterPublisher(this.redisTemplate(), this.addMeterTopic());
    }

    @Bean
    public ChannelTopic addMeterTopic() {
        return new ChannelTopic(this.environment.getRequiredProperty(PROPERTY_NAME_REDIS_ADDMETER_TOPIC));
    }
}
