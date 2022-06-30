# Domain Smartmetering


### Component Description
Config objects for smart meters.

### Usage

1. Add dependency
```xml
<dependency>
    <groupId>org.opensmartgridplatform</groupId>
    <artifactId>osgp-domain-smartmetering</artifactId>
</dependency>
```
2. Add the DlmsObjectService bean to the spring config file
```java
 @Bean
  public DlmsObjectService dlmsObjectService() {
    return new DlmsObjectService();
  }
```

