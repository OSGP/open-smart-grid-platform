-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

  IF NOT EXISTS(SELECT 1
                FROM information_schema.columns 
                WHERE table_schema = current_schema
                AND table_name = 'relay_status'
                AND column_name = 'last_status_retrieval_state')
  THEN
    ALTER TABLE relay_status RENAME COLUMN last_known_state TO last_switching_event_state;
    ALTER TABLE relay_status RENAME COLUMN last_know_switching_time TO last_switching_event_time;
    
    ALTER TABLE relay_status ALTER COLUMN last_switching_event_state DROP NOT NULL;
    ALTER TABLE relay_status ALTER COLUMN last_switching_event_time DROP NOT NULL;
    
    COMMENT ON COLUMN relay_status.last_switching_event_state IS 'The state of the relay as derived from latest events of an SSLD.';
    COMMENT ON COLUMN relay_status.last_switching_event_time IS 'The timestamp of the latest switching event for the relay of an SSLD.';
    
    ALTER TABLE relay_status ADD COLUMN last_known_state boolean;
    ALTER TABLE relay_status ADD COLUMN last_known_state_time timestamp without time zone;
    
    COMMENT ON COLUMN relay_status.last_known_state IS 'The state of the relay as derived from latest events and status retrieval of an SSLD.';
    COMMENT ON COLUMN relay_status.last_known_state_time IS 'The timestamp of the last known state for the relay of an SSLD.';

    UPDATE relay_status
    SET last_known_state = last_switching_event_state
    , last_known_state_time = last_switching_event_time;
    
    ALTER TABLE relay_status ALTER COLUMN last_known_state SET NOT NULL;
    ALTER TABLE relay_status ALTER COLUMN last_known_state_time SET NOT NULL;
  END IF;

END;
$$
