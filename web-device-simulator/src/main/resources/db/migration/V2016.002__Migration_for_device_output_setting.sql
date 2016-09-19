-- Script for migration legacy device_output_setting in device simulator.

CREATE OR REPLACE FUNCTION migration_for_device_output_setting_device_sim() 
	RETURNS VARCHAR AS
$BODY$
DECLARE 

BEGIN

   CREATE TABLE copy_device_output_setting (
	device_id bigint, 
	internal_id smallint,
	external_id smallint,
        output_type smallint	
    );

   INSERT INTO copy_device_output_setting 
	 (SELECT device_id AS deviceId
		, 1 AS internalId
		, 1 AS externalId
		, output_type AS outputType
		from device_output_setting      
		where output_type IN (1, 2)   -- TARIFF or TARIFF REVERSED
		and internal_id != 4

	UNION
	
	SELECT device_id AS deviceId
		, 2 AS internalId
		, 2 AS externalId
		, output_type AS outputType
		from device_output_setting dos1
		where output_type = 0  -- LIGHT
		and internal_id  != 4
		and not exists (select 1 
				from device_output_setting dos2 
				where dos2.output_type = 0 
				and dos2.internal_id != 4 
				and dos2.device_id = dos1.device_id
				and dos2.ctid < dos1.ctid)

	UNION
	
	SELECT device_id AS deviceId
		, 3 AS internalId
		, 3 AS externalId
		, output_type AS outputType
		from device_output_setting dos1
		where output_type = 0   -- LIGHT
		and internal_id != 4
		and exists (select 1 
			    from device_output_setting dos2 
			    where dos2.output_type = 0   -- LIGHT
			    and dos2.internal_id != 4 
			    and dos2.device_id = dos1.device_id
			    and dos2.ctid < dos1.ctid)	
	UNION

	-- add the internal_id = 4 relays (like a boiler).
        SELECT device_id AS deviceId
		, internal_id AS internalId
		, external_id AS externalId
		, output_type AS outputType
		from device_output_setting dos1
		where internal_id = 4
   );

   TRUNCATE device_output_setting;

   INSERT INTO device_output_setting(device_id, internal_id, external_id, output_type)
   SELECT device_id, internal_id, external_id, output_type 
   FROM copy_device_output_setting 
   ORDER BY device_id;

   DROP TABLE copy_device_output_setting;
   
 RETURN 'Migrated device_output_settings device simulator succesfully';
END;
$BODY$
LANGUAGE 'plpgsql';

-- Usage:
SELECT * FROM migration_for_device_output_setting_device_sim();

-- Drop function regarding clean code
DROP FUNCTION IF EXISTS migration_for_device_output_setting_device_sim();