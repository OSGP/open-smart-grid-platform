-- Script for migration legacy device_output_setting.
-- Only migrate device_output_settings from devices in dev simulator.

CREATE OR REPLACE FUNCTION migration_for_device_output_setting_osgp_core() 
	RETURNS VARCHAR AS
$BODY$
DECLARE 

BEGIN

   -- Remove constraint from table
   ALTER TABLE device_output_setting DROP CONSTRAINT device_output_setting_device_id_internal_id_key;

   CREATE TABLE copy_device_output_setting (
	device_id bigint, 
	internal_id smallint,
	external_id smallint,
    alias varchar(255),
	output_type smallint	
    );

   INSERT INTO copy_device_output_setting 
	 (SELECT device_id AS deviceId
		, 1 AS internalId
		, 1 AS externalId
		, CASE WHEN alias IS NULL or alias = ''
			       THEN NULL
			       ELSE alias || ' (migrated from TARIFF or TARIFF REVERSED)'
			  END AS aliasMig
		, output_type AS outputType
		from device_output_setting      
		where output_type IN (1, 2)   -- TARIFF or TARIFF REVERSED
		and internal_id != 4
		and device_id NOT IN (select id from device 
					where network_address <> '127.0.0.1')

	UNION
	
	SELECT device_id AS deviceId
		, 2 AS internalId
		, 2 AS externalId
		, CASE WHEN alias IS NULL or alias = ''
			       THEN NULL
			       ELSE alias || ' (migrated from light relay)'
			  END AS aliasMig
		, output_type AS outputType
		from device_output_setting dos1
		where output_type = 0  -- LIGHT
		and internal_id  != 4
		and not exists (select 1 
				from device_output_setting dos2 
				where dos2.output_type = 0 
				and dos2.internal_id != 4 
				--and dos2.internal_id > 1
				and dos2.device_id = dos1.device_id
				and dos2.ctid < dos1.ctid) 
		and device_id NOT IN (select id from device 
					where network_address <> '127.0.0.1')

	UNION
	
	SELECT device_id AS deviceId
		, 3 AS internalId
		, 3 AS externalId
		,  CASE WHEN alias IS NULL or alias = ''
			       THEN NULL
			       ELSE alias || ' (migrated from light relay)'
			  END AS aliasMig
		, output_type AS outputType
		from device_output_setting dos1
		where output_type = 0   -- LIGHT
		and internal_id != 4
		and exists (select 1 
			    from device_output_setting dos2 
			    where dos2.output_type = 0   -- LIGHT
			    and dos2.internal_id != 4 
                            and dos2.internal_id > 1
			    and dos2.device_id = dos1.device_id
			    and dos2.ctid < dos1.ctid)
		and device_id NOT IN (select id from device 
					where network_address <> '127.0.0.1')
		
	UNION

	-- add the internal_id = 4 relays (like a boiler).
        SELECT device_id AS deviceId
		, internal_id AS internalId
		, external_id AS externalId
		, alias AS aliasMig
		, output_type AS outputType
		from device_output_setting dos1
		where internal_id = 4
		and device_id NOT IN (select id from device 
					where network_address <> '127.0.0.1')

	UNION
	
	SELECT device_id AS deviceId
		, 1 AS internalId
		, 1 AS externalId
		,  CASE WHEN alias IS NULL or alias = ''
			       THEN NULL
			       ELSE alias || ' (migrated from light relay)'
			  END AS aliasMig
		, output_type AS outputType
		from device_output_setting dos1
		where output_type = 0   -- LIGHT
		and internal_id = 1
		and not exists (select 1 
			    from device_output_setting dos2 
			    where dos2.output_type IN (1, 2)   -- TARIFF or TARIFF REVERSED
			    and dos2.internal_id != 4 
			    and dos2.device_id = dos1.device_id
			    )
		and device_id NOT IN (select id from device 
					where network_address <> '127.0.0.1')
	order by deviceId, internalid);

   -- Only delete output_settings from devices in simulator
   DELETE FROM device_output_setting
   WHERE device_id NOT IN (SELECT id FROM device 
			WHERE network_address <> '127.0.0.1');
   
   INSERT INTO device_output_setting(device_id, internal_id, external_id, alias, output_type)
   SELECT device_id, internal_id, external_id, alias, output_type 
   FROM copy_device_output_setting 
   ORDER BY device_id;

   DROP TABLE copy_device_output_setting;
   
   -- Add constraint to table
   ALTER TABLE device_output_setting
   ADD CONSTRAINT device_output_setting_device_id_internal_id_key UNIQUE(device_id, internal_id); 
  
 RETURN 'Migrated device_output_settings osgp_core succesfully';
END;
$BODY$
LANGUAGE 'plpgsql';

-- Usage:
SELECT * FROM migration_for_device_output_setting_osgp_core();

-- Drop function regarding clean code
DROP FUNCTION IF EXISTS migration_for_device_output_setting_osgp_core();