/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

DO
$$
    BEGIN

        IF NOT EXISTS(SELECT 1 FROM information_schema.columns
                      WHERE table_schema = current_schema AND table_name = 'feeder' AND column_name = 'field_code')
        THEN
            ALTER TABLE feeder ADD COLUMN field_code CHARACTER VARYING(3);

            COMMENT ON COLUMN feeder.asset_label IS 'A coding of the field with the feeder according to asset registration.';
        END IF;

    END;
$$
