# Protocol Adapter for IEC60870-5-104 Protocol

## Grid eXchange Fabric information and news

High-level project information and news can be found on the Grid eXchange Fabric website:
* [www.lfenergy.org/projects/gxf/](https://www.lfenergy.org/projects/gxf/)

Grid eXchange Fabric detailed documentation:
* [grid-exchange-fabric.gitbook.io/gxf](https://grid-exchange-fabric.gitbook.io/gxf)

Grid eXchange Fabric issue tracker:
* [github.com/OSGP/Documentation/issues](https://github.com/OSGP/Documentation/issues)

## Add 104 devices to the database

```
-- osgp_core

INSERT INTO device
    (creation_time, modification_time, version, device_identification, device_type, is_activated,
     protocol_info_id, network_address, in_maintenance, gateway_device_id, device_lifecycle_status)
  VALUES
    (current_timestamp, current_timestamp, 0, 'LM_RTU_1', 'RTU' , true,
        (SELECT id FROM protocol_info WHERE protocol = '60870-5-104' AND protocol_version = '1.0'),
        '127.0.0.1', false, null, 'IN_USE'),
    (current_timestamp, current_timestamp, 0, 'LMD_1', 'LMD', true,
        (SELECT id FROM protocol_info WHERE protocol = '60870-5-104' AND protocol_version = '1.0'),
        null, false, (SELECT id FROM device WHERE device_identification = 'LM_RTU_1'), 'IN_USE'),
    (current_timestamp, current_timestamp, 0, 'LMD_2', 'LMD', true,
        (SELECT id FROM protocol_info WHERE protocol = '60870-5-104' AND protocol_version = '1.0'),
        null, false, (SELECT id FROM device WHERE device_identification = 'LM_RTU_1'), 'IN_USE');

INSERT INTO rtu_device
    (id, domain_info_id)
  VALUES 
    ((SELECT id FROM device WHERE device_identification = 'LM_RTU_1'), 
    (SELECT id FROM domain_info WHERE domain = 'PUBLIC_LIGHTING' AND domain_version = '1.0'));

INSERT INTO light_measurement_device
    (id, description, code, color, digital_input)
  VALUES
    ((SELECT id FROM device WHERE device_identification = 'LMD_1'), 'Test LMD 1', 'LMD-1', '#c9eec9', 1),
    ((SELECT id FROM device WHERE device_identification = 'LMD_2'), 'Test LMD 2', 'LMD-2', '#c9eec9', 2);

INSERT INTO device_authorization
    (creation_time, modification_time, version, function_group, device, organisation)
  VALUES
    (current_timestamp, current_timestamp, 0, 0,
      (SELECT id FROM device WHERE device_identification = 'LM_RTU_1'),
      (SELECT id FROM organisation WHERE organisation_identification = 'test-org')),
    (current_timestamp, current_timestamp, 0, 0,
      (SELECT id FROM device WHERE device_identification = 'LMD_1'),
      (SELECT id FROM organisation WHERE organisation_identification = 'test-org')),
    (current_timestamp, current_timestamp, 0, 0,
      (SELECT id FROM device WHERE device_identification = 'LMD_2'),
      (SELECT id FROM organisation WHERE organisation_identification = 'test-org'));

-- osgp_adapter_protocol_iec60870

INSERT INTO iec60870_device
    (creation_time, modification_time, version, device_identification, common_address, port, 
     device_type, gateway_device_identification, information_object_address)
  VALUES
    (current_timestamp, current_timestamp, 0, 'LM_RTU_1', 1, 2404, 'LIGHT_MEASUREMENT_RTU', null, null),
    (current_timestamp, current_timestamp, 0, 'LMD_1', -1, null, 'LIGHT_SENSOR', 'LM_RTU_1', 1),
    (current_timestamp, current_timestamp, 0, 'LMD_2', -1, null, 'LIGHT_SENSOR', 'LM_RTU_1', 2);
```
