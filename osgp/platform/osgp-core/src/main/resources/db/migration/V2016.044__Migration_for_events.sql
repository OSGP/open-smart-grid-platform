-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

CREATE OR REPLACE FUNCTION migration_for_events() RETURNS SETOF event AS
$BODY$
DECLARE
	r event%rowtype;
BEGIN

FOR r IN SELECT * FROM event
LOOP

   IF r.index = 1 THEN
      UPDATE event SET index = 2 where r.id = id;
   ELSIF
      r.index = 2 THEN
      UPDATE event SET index = 3 where r.id = id;
   ELSIF
      r.index = 3 THEN
      UPDATE event SET index = 1 where r.id = id;
   END IF;

END LOOP;
RETURN;
END;
$BODY$
LANGUAGE 'plpgsql';


-- Usage:
SELECT * FROM migration_for_events();

-- Drop function regarding clean code
DROP FUNCTION IF EXISTS migration_for_events();