-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

-- Script for migration legacy device_output_setting.

CREATE OR REPLACE FUNCTION migration_for_device_output_setting_device_simulator() 
	RETURNS VARCHAR AS
$BODY$
DECLARE 
   x int[];
   deviceId int;
BEGIN
    x := array(select distinct device_id from device_output_setting order by device_id);
 
   CREATE TABLE copy_device_output_setting (
	device_id bigint, 
	internal_id smallint,
	external_id smallint,
        output_type smallint
   );

   FOREACH deviceId IN ARRAY x
   LOOP
   
      insert into copy_device_output_setting (device_id, internal_id, external_id, output_type)	
      values (deviceId, 1, 1, 1);
   
      insert into copy_device_output_setting (device_id, internal_id, external_id, output_type)	
      values (deviceId, 2, 2, 0);
   
      insert into copy_device_output_setting (device_id, internal_id, external_id, output_type)	
      values (deviceId, 3, 3, 0);

   END LOOP;

   insert into copy_device_output_setting (device_id, internal_id, external_id, output_type)
   select device_id, 4, 4, output_type from device_output_setting where external_id = 4;
   
   TRUNCATE device_output_setting;

   INSERT INTO device_output_setting(device_id, internal_id, external_id, output_type)
   SELECT device_id, internal_id, external_id, output_type
   FROM copy_device_output_setting 
   ORDER BY device_id;

   DROP TABLE copy_device_output_setting;
  
 RETURN 'Migrated device_output_settings device_simulator succesfully';
END;
$BODY$
LANGUAGE 'plpgsql';

-- Usage:
SELECT * FROM migration_for_device_output_setting_device_simulator();

-- Drop function regarding clean code
DROP FUNCTION IF EXISTS migration_for_device_output_setting_device_simulator();