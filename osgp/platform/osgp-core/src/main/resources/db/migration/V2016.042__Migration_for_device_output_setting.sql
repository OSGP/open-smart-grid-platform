-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

-- Script for migration legacy device_output_setting.
-- Only migrate device_output_settings from devices in dev simulator.

CREATE OR REPLACE FUNCTION migration_for_device_output_setting_osgp_core() 
	RETURNS VARCHAR AS
$BODY$
DECLARE 
   x int[];
   deviceId int;
   alias_relay1 varchar(255);
   alias_relay2 varchar(255);
BEGIN
   x := array(select distinct device_id from device_output_setting 
              where device_id IN (select id from device 
				                  where network_address = '127.0.0.1')
	          order by device_id);
 
   CREATE TABLE copy_device_output_setting (
			device_id bigint, 
			internal_id smallint,
			external_id smallint,
		    output_type smallint,
		    alias varchar(255)	
    );

   FOREACH deviceId IN ARRAY x
   LOOP
   
   alias_relay1 := (select alias from device_output_setting where device_id = deviceId and internal_id = 1);
   alias_relay2 := (select alias from device_output_setting where device_id = deviceId and internal_id = 2);

   insert into copy_device_output_setting (device_id, internal_id, external_id, output_type, alias)	
   values (deviceId, 1, 1, 1, null);
   
   if alias_relay1 is null or alias_relay1 = '' then
      insert into copy_device_output_setting (device_id, internal_id, external_id, output_type, alias)	
      values (deviceId, 2, 2, 0, null);
   else
      insert into copy_device_output_setting (device_id, internal_id, external_id, output_type, alias)	
      values (deviceId, 2, 2, 0, alias_relay1 || (' (migrated from R1)'));
   end if;
   
   if alias_relay2 is null or alias_relay2 = '' then
      insert into copy_device_output_setting (device_id, internal_id, external_id, output_type, alias)	
      values (deviceId, 3, 3, 0, null);
   else
      insert into copy_device_output_setting (device_id, internal_id, external_id, output_type, alias)	
      values (deviceId, 3, 3, 0, alias_relay2 || (' (migrated from R2)'));
   end if;

   END LOOP;

   insert into copy_device_output_setting (device_id, internal_id, external_id, output_type, alias)
   select device_id, 4, 4, output_type, null 
   from device_output_setting 
   where external_id = 4
   and device_id IN (select id from device 
		     where network_address = '127.0.0.1');

   DELETE FROM device_output_setting
   WHERE device_id IN (select id from device 
		     where network_address = '127.0.0.1');

   INSERT INTO device_output_setting(device_id, internal_id, external_id, output_type, alias)
   SELECT device_id, internal_id, external_id, output_type, alias
   FROM copy_device_output_setting 
   ORDER BY device_id;

   DROP TABLE copy_device_output_setting;
  
 RETURN 'Migrated device_output_settings osgp_core succesfully';
END;
$BODY$
LANGUAGE 'plpgsql';

-- Usage:
SELECT * FROM migration_for_device_output_setting_osgp_core();

-- Drop function regarding clean code
DROP FUNCTION IF EXISTS migration_for_device_output_setting_osgp_core();