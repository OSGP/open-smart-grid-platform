/*
 * Copyright 2023 Alliander N.V.
 */

package org.opensmartgridplatform.shared.application.config.kafka;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;

class KafkaConfigTest {

  private static class KafkaTestConfig extends KafkaConfig {
    public KafkaTestConfig(Environment environment) {
      super(environment);
    }
  }

  final MockEnvironment mockEnvironment =
      new MockEnvironment()
          .withProperty("one.property", "one")
          .withProperty("prefix.one.property", "prefix-one")
          .withProperty("two.property", "two")
          .withProperty("prefix.three.property", "prefix-three")
          .withProperty("boolean.property", "true")
          .withProperty("list.property", "one,two,three")
          .withProperty(
              "class.property",
              "org.opensmartgridplatform.shared.application.config.kafka.KafkaConfigTest");

  @Test
  void configDefToProperties() {
    KafkaConfig kafkaConfig = new KafkaTestConfig(mockEnvironment);

    ConfigDef configDef =
        new ConfigDef()
            .define("one.property", Type.STRING, Importance.MEDIUM, "one")
            .define("two.property", Type.STRING, Importance.MEDIUM, "one")
            .define("three.property", Type.STRING, Importance.MEDIUM, "three")
            .define("boolean.property", Type.BOOLEAN, Importance.MEDIUM, "boolean")
            .define("list.property", Type.LIST, Importance.MEDIUM, "list")
            .define("class.property", Type.CLASS, Importance.MEDIUM, "class")
            .define("undefined.property", Type.STRING, Importance.MEDIUM, "undefined");
    Map<String, Object> properties = kafkaConfig.configDefToProperties(configDef, "prefix");

    Map<String, Object> expectedProperties = new HashMap<>();
    expectedProperties.put("one.property", "prefix-one");
    expectedProperties.put("two.property", "two");
    expectedProperties.put("three.property", "prefix-three");
    expectedProperties.put("boolean.property", true);
    expectedProperties.put("list.property", Arrays.asList("one", "two", "three"));
    expectedProperties.put("class.property", KafkaConfigTest.class);
    assertEquals(expectedProperties, properties);
  }

  @Test
  void exists() {
    KafkaConfig kafkaConfig = new KafkaTestConfig(mockEnvironment);

    assertTrue(kafkaConfig.exists("one.property", "prefix"));
    assertTrue(kafkaConfig.exists("one.property", "nonprefix"));

    assertTrue(kafkaConfig.exists("two.property", "prefix"));
    assertTrue(kafkaConfig.exists("two.property", "nonprefix"));

    assertTrue(kafkaConfig.exists("three.property", "prefix"));
    assertFalse(kafkaConfig.exists("three.property", "nonprefix"));
  }

  @Test
  void getValue() {
    KafkaConfig kafkaConfig = new KafkaTestConfig(mockEnvironment);

    Object oneProperty = kafkaConfig.getValue("one.property", Type.STRING, "prefix");
    assertEquals("prefix-one", oneProperty);

    Object twoProperty = kafkaConfig.getValue("two.property", Type.STRING, "prefix");
    assertEquals("two", twoProperty);

    Object threeProperty = kafkaConfig.getValue("three.property", Type.STRING, "prefix");
    assertEquals("prefix-three", threeProperty);

    Object booleanValue = kafkaConfig.getValue("boolean.property", Type.BOOLEAN, "prefix");
    assertTrue(booleanValue instanceof Boolean);
    assertTrue((Boolean) booleanValue);

    Object listValue = kafkaConfig.getValue("list.property", Type.LIST, "prefix");
    assertTrue(listValue instanceof List);
    assertEquals(Arrays.asList("one", "two", "three"), listValue);

    Object classValue = kafkaConfig.getValue("class.property", Type.CLASS, "prefix");
    assertTrue(classValue instanceof Class);

    assertEquals(KafkaConfigTest.class, classValue);
  }
}
