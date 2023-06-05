-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

-- Script for migration legacy relay status.

CREATE OR REPLACE FUNCTION migration_for_relay_status() RETURNS SETOF relay_status AS
$BODY$
DECLARE 
    r relay_status%rowtype;
    
BEGIN

  FOR r IN SELECT * FROM relay_status 
  			where device_id IN (select id from device 
						where network_address = '127.0.0.1')
  	   order by id
  LOOP
	IF r.index = 1 THEN
		UPDATE relay_status SET index = 2 where id = r.id;
	ELSEIF r.index = 2 THEN
		UPDATE relay_status SET index = 3 where id = r.id;
    ELSEIF r.index = 3 THEN
		UPDATE relay_status SET index = 1 where id = r.id;
    ELSEIF r.index = 4 THEN
		UPDATE relay_status SET index = 4 where id = r.id;
	END IF;

	RETURN NEXT r;    
  END LOOP;
  RETURN;   

END
$BODY$
LANGUAGE 'plpgsql';

-- Usage:
SELECT * FROM migration_for_relay_status();

-- Drop function regarding clean code
DROP FUNCTION IF EXISTS migration_for_relay_status();