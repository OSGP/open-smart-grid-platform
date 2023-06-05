-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

  IF NOT EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = current_schema AND table_name = 'protocol_info' AND column_name = 'parallel_requests_allowed')
  THEN
    ALTER TABLE protocol_info ADD COLUMN parallel_requests_allowed BOOLEAN DEFAULT TRUE;

    COMMENT ON COLUMN protocol_info.parallel_requests_allowed IS 'Flag indicating if the devices using this protocol support parallel requests.';

    UPDATE protocol_info SET parallel_requests_allowed = FALSE WHERE protocol = 'IEC61850' AND protocol_version = '1.0';
    
  END IF;
  
END;
$$ 